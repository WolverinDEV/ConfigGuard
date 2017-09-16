package dev.wolveringer.config.yaml.converter;

import dev.wolveringer.config.ConfigConfiguration;
import dev.wolveringer.config.descriptor.ConfigFieldDescriptor;
import dev.wolveringer.config.exception.InternalConfigException;
import lombok.NonNull;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by wolverindev on 16.09.17.
 */
public class ArrayObjectConverter extends ValueConverter {
    @Override
    public boolean supports(Class<?> type) {
        return type.isArray();
    }

    @Override
    public Object convertFromConfig(ConfigConfiguration cfg, Class<?> targetClass, Type type, @NonNull Object input) throws Exception {
        if(!targetClass.isArray())
            throw new InternalConfigException("Target class isnt an array");

        System.out.println("Convert to " + targetClass);

        Class<?> typeClass = targetClass.getComponentType(); //Must be a class. Else invalid list!
        List elements = (List) input;
        Object array = Array.newInstance(typeClass, elements.size());

        Optional<ValueConverter> converter = ValueConverter.getConverter(typeClass);

        int index = 0;
        if(converter.isPresent()){
            for(Object obj : elements){
                Object target = converter.get().convertFromConfig(cfg, typeClass, null, obj);
                Array.set(array, index++, target);
            }
        } else {
            while(!elements.isEmpty())
                Array.set(array, index++, elements.remove(0));
        }

        return array;
    }

    @Override
    public Object convertFromInstance(ConfigConfiguration cfg, ConfigFieldDescriptor field, @NonNull Object input) throws Exception {
        if(field == null)
            throw new InternalConfigException("Only one layer of arrays supported!");

        Class<?> typeClass = field.getField().getType().getComponentType(); //Must be a class. Else invalid list!
        int length = Array.getLength(input);

        List output = new ArrayList<>();

        Optional<ValueConverter> converter = ValueConverter.getConverter(typeClass);

        if(converter.isPresent()){
            for(int index = 0; index < length; index++){
                Object elm = Array.get(input, index);
                Object target = converter.get().convertFromInstance(cfg, null, elm);
                if(target != null)
                    output.add(target);
            }
        } else {
            for(int index = 0; index < length; index++)
                output.add(Array.get(input, index));
        }

        System.out.println("Convert " + input.getClass() + "/" + typeClass);

        return output;
    }
}
