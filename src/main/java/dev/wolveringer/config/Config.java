package dev.wolveringer.config;

import dev.wolveringer.config.exception.ConfigException;
import dev.wolveringer.config.exception.InvalidConfigException;

/**
 * Created by wolverindev on 09.09.17.
 */
public interface Config {
    void load() throws ConfigException;
    void loadFromString(String value) throws ConfigException;

    @Deprecated
    void reload() throws ConfigException;

    void save() throws ConfigException;
    String saveAsString() throws ConfigException;

    ConfigType getBackendType();
}
