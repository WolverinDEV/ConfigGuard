package dev.wolveringer.config.yaml.converter;

import dev.wolveringer.config.ConfigConfiguration;
import lombok.NonNull;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by wolverindev on 09.09.17.
 */
public abstract class ValueConverter {
    private static List<ValueConverter> converter = new ArrayList<>();

    public static List<ValueConverter> getConverters(){
        return Collections.unmodifiableList(converter);
    }

    public static void registerConverter(ValueConverter converter){
        ValueConverter.converter.add(converter);
    }

    public static Optional<ValueConverter> getConverter(Class<?> type){
        return converter.stream().filter(e -> e.supports(type)).findAny();
    }

    static {
        registerConverter(new PrimativeValueConverter());
        registerConverter(new ConfigObjectConverter());
        registerConverter(new EnumConverter());
    }

    public abstract boolean supports(Class<?> type);

    public abstract Object convertFromConfig(ConfigConfiguration cfg, Class<?> destClass,@NonNull Object input) throws Exception;
    public abstract Object convertFromInstance(ConfigConfiguration cfg, Object handle,@NonNull Object input) throws Exception;
}
