package dev.wolveringer.config.exception;

import lombok.Getter;

/**
 * Created by wolverindev on 09.09.17.
 */

@Getter
public class ValueConvertException extends ConfigException {
    private Object source;
    private Class<?> targetType;

    public ValueConvertException(Object source, Class<?> target, Exception handle, String s) {
        super(s, handle);
        this.source = source;
        this.targetType = target;
    }
}
