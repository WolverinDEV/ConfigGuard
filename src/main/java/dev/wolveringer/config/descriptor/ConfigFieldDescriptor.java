package dev.wolveringer.config.descriptor;

import dev.wolveringer.config.ConfigConfiguration;
import dev.wolveringer.config.annotation.CommentGenerator;
import dev.wolveringer.config.annotation.Comments;
import dev.wolveringer.config.annotation.Configurable;
import dev.wolveringer.config.annotation.Path;
import dev.wolveringer.config.exception.ConfigException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wolverindev on 09.09.17.
 */
@RequiredArgsConstructor
@Getter
@ToString
public class ConfigFieldDescriptor {
    private final Field field;

    public boolean isRelevant(ConfigConfiguration cfg) throws ConfigException {
        if(cfg.getConfigurationAnnonation().isRequired() && !field.isAnnotationPresent(Configurable.class)) return false;
        if(cfg.getConfigurationAnnonation().isRequired() && !field.isAnnotationPresent(Path.class)) return false;
        if((field.getModifiers() & (Modifier.FINAL | Modifier.TRANSIENT)) != 0) return false;

        return true;
    }

    public String getConfigPath(){
        if(field.isAnnotationPresent(Path.class))
            return field.getAnnotation(Path.class).value();
        return field.getName();
    }

    public List<String> getComments(Object value){
        ArrayList<String> output = new ArrayList<>();

        CommentGenerator gen = null;
        Object annoClass = null;

        if(field.isAnnotationPresent(CommentGenerator.class)) annoClass = (gen = field.getAnnotation(CommentGenerator.class));
        else { //For "extends" like an interface (Used at default annonations)
            for(Annotation anno : field.getAnnotations()){
                if(anno.annotationType().isAnnotationPresent(CommentGenerator.class)){
                    gen = anno.annotationType().getAnnotation(CommentGenerator.class);
                    annoClass = anno;
                    break;
                }
            }
        }
        genLoop:
        if(gen != null){
            try {
                List<Method> methods = new ArrayList<>();
                methods.addAll(Arrays.asList(gen.generatorClass().getMethods()));
                methods.addAll(Arrays.asList(gen.generatorClass().getDeclaredMethods()));

                Object result = null;
                boolean success = false;
                for(Method m : methods){
                    if(m.getName().equalsIgnoreCase(gen.methode())){
                        if(m.getParameterTypes().length == 3){
                            success = true;
                            result = m.invoke(null, field, value, annoClass);
                        } else if(m.getParameterTypes().length == 2){
                            success = true;
                            result = m.invoke(null, field, value);
                        } else if(m.getParameterTypes().length == 1){
                            success = true;
                            if(m.getParameterTypes()[0] == Field.class){
                                result = m.invoke(null, field);
                            } else {
                                result = m.invoke(null, value);
                            }
                        } else if(m.getParameterTypes().length == 0){
                            success = true;
                            try {
                                result = m.invoke(null);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if(!success){
                    //TODO Throw exception etc.
                    break genLoop;
                }

                if(result instanceof List)
                    ((List) result).forEach(e -> output.add(e == null ? "null" : e.toString()));
                else
                    output.add(result == null ? "null" : result.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if(field.isAnnotationPresent(Comments.class)) output.addAll(Arrays.asList(field.getAnnotation(Comments.class).value()));

        return output;
    }

    public boolean requireValue(){
        return field.isAnnotationPresent(dev.wolveringer.config.annotation.NonNull.class);
    }

    public Object getValue(Object handle) throws IllegalAccessException {
        if(!field.isAccessible()) field.setAccessible(true);
        return field.get(handle);
    }

    public void setValue(Object handle, Object obj) throws IllegalAccessException {
        if(!field.isAccessible()) field.setAccessible(true);
        field.set(handle, obj);
    }
}
