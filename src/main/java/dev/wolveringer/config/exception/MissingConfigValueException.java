package dev.wolveringer.config.exception;

/**
 * Created by wolverindev on 09.09.17.
 */
public class MissingConfigValueException extends ConfigException {
    public MissingConfigValueException(String s) {
        super(s);
    }
}
