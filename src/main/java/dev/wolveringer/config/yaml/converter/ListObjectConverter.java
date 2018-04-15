package dev.wolveringer.config.yaml.converter;

import dev.wolveringer.config.ConfigConfiguration;
import dev.wolveringer.config.descriptor.ConfigFieldDescriptor;
import dev.wolveringer.config.exception.InternalConfigException;
import lombok.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by wolverindev on 12.09.17.
 */
public class ListObjectConverter extends ValueConverter {
    @Override
    public boolean supports(Class<?> type) {
        return Iterable.class.isAssignableFrom(type);
    }

    @Override
    public Object convertFromConfig(ConfigConfiguration cfg, Class<?> targetClass, Type type, @NonNull Object input) throws Exception {
        //System.out.println("Convert to " + targetClass);

        Class<?> typeClass = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0]; //Must be a class. Else invalid list!

        List out = new ArrayList();
        List elements = (List) input;

        Optional<ValueConverter> converter = ValueConverter.getConverter(typeClass);

        if(converter.isPresent()){
            for(Object obj : elements){
                Object target = converter.get().convertFromConfig(cfg, typeClass, null, obj);
                if(target != null)
                    out.add(target);
            }
        } else elements.forEach(e -> out.add(e));

        return out;
    }

    @Override
    public Object convertFromInstance(ConfigConfiguration cfg,ConfigFieldDescriptor field, @NonNull Object input) throws Exception {
        if(field == null ||
                !(((ParameterizedType) field.getField().getGenericType()).getActualTypeArguments()[0] instanceof Class))
                        throw new InternalConfigException("Only one layer of list supported!");

        Iterable<?> iterable = (Iterable<?>) input;
        Class<?> typeClass = (Class<?>) ((ParameterizedType) field.getField().getGenericType()).getActualTypeArguments()[0]; //Must be a class. Else invalid list!
        List<?> output = new ArrayList<>();

        Optional<ValueConverter> converter = ValueConverter.getConverter(typeClass);

        if(converter.isPresent()){
            for(Object obj : iterable){
                Object target = converter.get().convertFromInstance(cfg, null, obj);
                if(target != null)
                    ((List) output).add(target);
            }
        } else iterable.forEach(e -> ((List) output).add(e));

        //System.out.println("Convert " + input.getClass() + "/" + typeClass);

        return output;
    }
}
