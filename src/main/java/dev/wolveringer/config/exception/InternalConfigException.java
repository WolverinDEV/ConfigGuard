package dev.wolveringer.config.exception;

/**
 * Created by wolverindev on 09.09.17.
 */
public class InternalConfigException extends ConfigException {
    public InternalConfigException() {
    }

    public InternalConfigException(String s) {
        super(s);
    }

    public InternalConfigException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InternalConfigException(Throwable throwable) {
        super(throwable);
    }

    public InternalConfigException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
