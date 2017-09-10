package dev.wolveringer.config.annotation.defaults;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wolverindev on 10.09.17.
 */
public class DefaultImplementations {
    public static List<String> buildEnumAvailable(Field field, Object parm){
        if(field.isEnumConstant()) return Arrays.asList("Invalid @EnumAvailableListener usage!");

        List<String> elements = new ArrayList<>();
        elements.add("Available entries: ");
        Iterator<Enum> it = Arrays.asList(((Class<Enum>) field.getType()).getEnumConstants()).iterator();
        while(it.hasNext()){
            Enum e = it.next();
            elements.add(" [" + e.ordinal() + "] " + e.name());
        }
        return elements;
    }
}
