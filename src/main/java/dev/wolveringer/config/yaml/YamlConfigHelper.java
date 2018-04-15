package dev.wolveringer.config.yaml;

import dev.wolveringer.config.Config;
import dev.wolveringer.config.ConfigConfiguration;
import dev.wolveringer.config.annotation.Comments;
import dev.wolveringer.config.descriptor.ConfigClassDescriptor;
import dev.wolveringer.config.descriptor.ConfigFieldDescriptor;
import dev.wolveringer.config.exception.*;
import dev.wolveringer.config.yaml.converter.ValueConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class YamlConfigHelper {
    @AllArgsConstructor
    @Getter
    @ToString
    protected static class ConfigMap {
        private HashMap<String, List<String>> comments;
        private Map configMap;
    }

    private static String streamToString(InputStream is) throws IOException{
        StringBuilder out = new StringBuilder();
        byte[] buffer = new byte[1024];
        int readed;
        while((readed = is.read(buffer)) > 0)
            out.append(new String(buffer, 0, readed));
        return out.toString();
    }

    public static void loadConfigFromFile(YamlConfig instance, ConfigClassDescriptor cls, ConfigConfiguration cfg) throws ConfigException {
        if(!cfg.getConfigFile().exists()){
            if(!cfg.isCreateIfNotExist()) throw new ConfigNotFoundException("Could not find config file " + cfg.getConfigFile());
            saveConfigToFile(instance, cls, cfg);
        }

        String contains;
        try {
            FileInputStream fis = new FileInputStream(cfg.getConfigFile());
            contains = streamToString(fis);
            fis.close();
        } catch (Exception e) { //TODO file not found etc!
            throw new InternalConfigException(e);
        }
        loadConfigFromString(instance, contains, cls, cfg);
    }

    public static void loadConfigFromString(YamlConfig instance, String fileContains, ConfigClassDescriptor cls, ConfigConfiguration cfg) throws ConfigException {
        loadConfigFromMap(instance, (Map) new Yaml().load(fileContains), cls, cfg);
    }

    public static void loadConfigFromMap(YamlConfig instance, Map yamlMap, ConfigClassDescriptor cls, ConfigConfiguration cfg) throws ConfigException {
        for (ConfigFieldDescriptor field : cls.getFields()) {
            if (!field.isRelevant(cfg)) {
                continue;
            }

            String path = field.getConfigPath();
            Object value = null;
            boolean hasValue = false;

            Map currentDeep = yamlMap;

            int index;
            while((index = path.indexOf('.')) >= 0){
                String elm = path.substring(0, index);
                Object obj = currentDeep.get(elm);
                if(obj == null || !(obj instanceof Map)){
                    index = -2;
                    break;
                }
                currentDeep = (Map) obj;
                path =  path.substring(index + 1);
            }
            if(index != -2){
                value = currentDeep.get(path);
                hasValue = currentDeep.containsKey(path);
            }

            if(value != null){
                Optional<ValueConverter> converter = ValueConverter.getConverter(field.getField().getType());
                if (converter.isPresent())
                    try {
                        value = converter.get().convertFromConfig(cfg, field.getField().getType(), field.getField().getGenericType(), value);
                    } catch (Exception e) {
                        throw new ValueConvertException(value, field.getField().getType(), e,
                                "Cant convert config value for field " + field.getField().getDeclaringClass().getName() + "#" + field.getField().getName());
                    }
            }

            if(value == null && field.requireValue())
                throw new MissingConfigValueException("Missing config value for key: " + field.getConfigPath());
            if(!hasValue) {
                cfg.getDebug().log("Ignoring field " + field.getField() + "|" + field.getConfigPath() + ". It isnt set in config");
                continue;
            }
            if(value == null) {
                cfg.getDebug().log("Using default null value for " + field.getField());
                value = field.nullValue();
            }
            try {
                cfg.getDebug().log("Set " + field.getField() + " to " + value);
                field.setValue(instance, value);
            } catch (IllegalAccessException e) {
                throw new InvalidConfigClassException("Cant access field " + field.getField().getName() + " in class " + field.getField().getDeclaringClass().getName());
            }
        }

        if(!instance.checkConfig()) throw new InvalidConfigException("Config check failed! (checkConfig() -> return false)");
    }

    public static ConfigMap saveConfigToMap(YamlConfig instance, ConfigClassDescriptor cls, ConfigConfiguration cfg) throws ConfigException {
        Map valueMap = new HashMap();

        HashMap<String, List<String>> comments = new HashMap<>();
        if(instance.getClass().isAnnotationPresent(Comments.class))
            comments.put("", Arrays.asList(instance.getClass().getAnnotation(Comments.class).value()));

        for (ConfigFieldDescriptor field : cls.getFields()) {
            if (!field.isRelevant(cfg)) {
                continue;
            }

            Object value;
            try {
                value = field.getValue(instance);
            } catch (IllegalAccessException e) {
                throw new InvalidConfigClassException("Cant access field " + field.getField().getName() + " in class " + field.getField().getDeclaringClass().getName());
            }

            List<String> fieldComments = new ArrayList<>(field.getComments(value));
            if(value instanceof Config){ //Config class could also have comments
                if(value.getClass().isAnnotationPresent(Comments.class))
                    fieldComments.addAll(Arrays.asList(value.getClass().getAnnotation(Comments.class).value()));
            }

            Optional<ValueConverter> converter = ValueConverter.getConverter(field.getField().getType());
            if (converter.isPresent() && value != null)
                try {
                    value = converter.get().convertFromInstance(cfg, field, value);
                } catch (Exception e) {
                    throw new ValueConvertException(value, field.getField().getType(), e, "Cant convert object value for field " + field.getField().getName());
                }

            String path = field.getConfigPath();
            if(value instanceof Iterable){ //Extra handling for lists
                for(Object elm : (Iterable) value)
                    if(elm instanceof ConfigMap){
                        final String fpath = path;
                        ((ConfigMap) elm).comments.forEach((k, v) ->  comments.put(fpath + "." + k, v));
                    }
            } else if(value instanceof ConfigMap){
                ConfigMap map = (ConfigMap) value;
                final String fpath = path;
                map.comments.forEach((k, v) ->  comments.put(fpath + "." + k, v));
            }

            Map currentDeep = valueMap;

            int index;
            while((index = path.indexOf('.')) >= 0){
                String elm = path.substring(0, index);
                Map map = (Map) currentDeep.getOrDefault(elm, new HashMap<>());
                currentDeep.put(elm, map);
                currentDeep = map;
                path =  path.substring(index + 1);
            }

            if(value instanceof Iterable){ //Extra handling for lists
                List<Object> output = new ArrayList<>();
                for(Object elm : (Iterable) value){
                    if(elm instanceof ConfigMap)
                        output.add(((ConfigMap) elm).configMap);
                    else
                        output.add(elm);
                }
                currentDeep.put(path, output);
            } else if(value instanceof ConfigMap){
                currentDeep.put(path, ((ConfigMap) value).configMap);
            } else {
                currentDeep.put(path, value);
            }


            if(!fieldComments.isEmpty()) comments.put(field.getConfigPath(), fieldComments);
        }

        System.out.println("Value map: " + valueMap + " - " + comments);
        return new ConfigMap(comments, valueMap);
    }

    public static String saveConfigToString(YamlConfig instance, ConfigClassDescriptor cls, ConfigConfiguration cfg) throws ConfigException {
        ConfigMap cfgMap = saveConfigToMap(instance, cls, cfg);
        return YamlCommentHelper.addComments(new Yaml().dumpAsMap(cfgMap.configMap), cfgMap.getComments());
    }

    public static void saveConfigToFile(YamlConfig instance, ConfigClassDescriptor cls, ConfigConfiguration cfg) throws ConfigException {
        String str = saveConfigToString(instance, cls, cfg);

        try {
            File target = cfg.getConfigFile();
            if(!target.exists()){
                target.getParentFile().mkdirs();
                target.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(target);
            fos.write(str.getBytes(Charset.forName("UTF-8")));
            fos.close();
        } catch (Exception e) { //TODO file not found etc!
            e.printStackTrace();
            throw new InternalConfigException(e);
        }
    }
}
