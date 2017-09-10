package dev.wolveringer.config.exception;

/**
 * Created by wolverindev on 09.09.17.
 */
public class ConfigException extends Exception {
    public ConfigException() { }

    public ConfigException(String s) {
        super(s);
    }

    public ConfigException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConfigException(Throwable throwable) {
        super(throwable);
    }

    public ConfigException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
