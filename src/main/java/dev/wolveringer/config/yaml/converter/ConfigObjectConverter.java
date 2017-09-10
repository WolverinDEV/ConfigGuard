package dev.wolveringer.config.yaml.converter;

import dev.wolveringer.config.Config;
import dev.wolveringer.config.ConfigConfiguration;
import dev.wolveringer.config.descriptor.ConfigClassDescriptor;
import dev.wolveringer.config.exception.InvalidConfigException;
import dev.wolveringer.config.yaml.YamlConfig;
import dev.wolveringer.config.yaml.YamlConfigHelper;

import java.util.Map;

/**
 * Created by wolverindev on 09.09.17.
 */
public class ConfigObjectConverter extends ValueConverter {
    @Override
    public boolean supports(Class<?> type) {
        return YamlConfig.class.isAssignableFrom(type);
    }

    @Override
    public Object convertFromConfig(ConfigConfiguration cfg, Class<?> destClass, Object input) throws Exception {
        if(!(input instanceof Map)) throw new InvalidConfigException("Try to parse a config entry but dont received a map!");

        ConfigClassDescriptor descriptor = new ConfigClassDescriptor((Class<Config>) (Class) destClass).parse();
        Config instance = descriptor.createNewInstance(cfg);

        YamlConfigHelper.loadConfigFromMap((YamlConfig) instance, (Map) input, descriptor, cfg);

        return instance;
    }

    @Override
    public Object convertFromInstance(ConfigConfiguration cfg, Object handle, Object input) throws Exception {
        if(!(input instanceof Config)) throw new InvalidConfigException("Tried to convert to config map from an invalid Config");

        ConfigClassDescriptor descriptor = new ConfigClassDescriptor((Class<Config>) (Class) input.getClass()).parse();
        return YamlConfigHelper.saveConfigToMap((YamlConfig) input, descriptor, cfg);
    }
}
