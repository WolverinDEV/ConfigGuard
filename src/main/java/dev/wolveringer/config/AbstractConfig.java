package dev.wolveringer.config;

import dev.wolveringer.config.exception.ConfigException;
import dev.wolveringer.config.exception.InvalidConfigException;

/**
 * Created by wolverindev on 09.09.17.
 */
public abstract class AbstractConfig implements Config {
    @Override
    public void reload() throws ConfigException {
        load();
    }

    /**
     * @return true if the parsed config is valid and acceptable (By an implementation)
     */
    protected boolean checkConfig() throws ConfigException { return true; }
}
