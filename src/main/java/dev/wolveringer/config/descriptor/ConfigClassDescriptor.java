package dev.wolveringer.config.descriptor;

import dev.wolveringer.config.Config;
import dev.wolveringer.config.ConfigConfiguration;
import dev.wolveringer.config.exception.ConfigException;
import dev.wolveringer.config.exception.InternalConfigException;
import dev.wolveringer.config.exception.InvalidConfigClassException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wolverindev on 09.09.17.
 */
@Getter
@RequiredArgsConstructor
public class ConfigClassDescriptor {
    private final
    @NonNull
    Class<Config> clazz;
    @Getter
    private List<ConfigFieldDescriptor> fields = new ArrayList<>();

    public ConfigClassDescriptor parse() {
        List<Field> clsFields = new ArrayList<>();
        clsFields.addAll(Arrays.asList(clazz.getFields()));
        clsFields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        clsFields.forEach(e -> fields.add(new ConfigFieldDescriptor(e)));
        return this;
    }

    public Config createNewInstance(ConfigConfiguration cfg) throws ConfigException {
        Config instance = null;
        try {
            instance = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException ex) {
            if (cfg.getCallConstructor().isRequired())
                throw new InvalidConfigClassException("Class " + clazz.getName() + " hasn't a default constructor, but the config configuration forced a constructor call.");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (instance == null) {
            try {
                instance = (Config) getUnsafe().allocateInstance(clazz);
            } catch (InstantiationException e) {
                throw new InternalConfigException(e);
            } catch (Exception e){
                throw new InternalConfigException(e);
            }
        }
        return instance;
    }

    private static Unsafe getUnsafe() throws Exception{
        Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
        singleoneInstanceField.setAccessible(true);
        return (Unsafe) singleoneInstanceField.get(null);
    }
}
