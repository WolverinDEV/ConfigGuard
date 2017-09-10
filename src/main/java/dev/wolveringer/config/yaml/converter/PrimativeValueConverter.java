package dev.wolveringer.config.yaml.converter;

import dev.wolveringer.config.ConfigConfiguration;

import java.util.HashSet;

/**
 * Created by wolverindev on 09.09.17.
 */
public class PrimativeValueConverter extends ValueConverter {
    private HashSet<String> types = new HashSet<String>() {{
        add("boolean");
        add("char");
        add("byte");
        add("short");
        add("int");
        add("long");
        add("float");
        add("double");
    }};

    @Override
    public boolean supports(Class<?> type) {
        return types.contains(type.getName());
    }

    @Override
    public Object convertFromConfig(ConfigConfiguration cfg, Class<?> destClass, Object input) throws Exception {
        switch (destClass.getSimpleName()) {
            case "short":
                return (input instanceof Short) ? input : new Integer((int) input).shortValue();
            case "byte":
                return (input instanceof Byte) ? input : new Integer((int) input).byteValue();
            case "float":
                if (input instanceof Integer) {
                    return new Double((int) input).floatValue();
                }

                return (input instanceof Float) ? input : new Double((double) input).floatValue();
            case "char":
                return (input instanceof Character) ? input : ((String) input).charAt(0);
            default:
                return input;
        }
    }

    @Override
    public Object convertFromInstance(ConfigConfiguration cfg, Object handle, Object input) throws Exception {
        return input;
    }
}
