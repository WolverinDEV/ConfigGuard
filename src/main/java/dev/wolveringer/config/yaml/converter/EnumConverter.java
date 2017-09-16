package dev.wolveringer.config.yaml.converter;

import dev.wolveringer.config.ConfigConfiguration;
import dev.wolveringer.config.descriptor.ConfigFieldDescriptor;
import dev.wolveringer.config.exception.InvalidConfigException;

import java.lang.reflect.Type;

/**
 * Created by wolverindev on 10.09.17.
 */
public class EnumConverter extends ValueConverter {
    @Override
    public boolean supports(Class<?> type) {
        return type.isEnum();
    }

    @Override
    public Object convertFromConfig(ConfigConfiguration cfg, Class<?> destClass, Type type, Object input) throws Exception {
        Enum[] constants = ((Class<Enum>) destClass).getEnumConstants();
        if(input instanceof Integer) return constants[((Integer) input).intValue()];

        for(int index = 0; index < constants.length; index++)
            if(constants[index].name().equalsIgnoreCase(input.toString())) return constants[index];

        throw new InvalidConfigException("Cant convert from " + input + " to an enum constant of " + destClass.getName());
    }

    @Override
    public Object convertFromInstance(ConfigConfiguration cfg, ConfigFieldDescriptor field, Object input) throws Exception {
        if(cfg.getUseEnumName() != ConfigConfiguration.EnforcementType.NONE)
            return ((Enum) input).name();
        return ((Enum) input).ordinal();
    }
}
