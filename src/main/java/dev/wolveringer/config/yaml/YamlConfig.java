package dev.wolveringer.config.yaml;

import dev.wolveringer.config.AbstractConfig;
import dev.wolveringer.config.Config;
import dev.wolveringer.config.ConfigConfiguration;
import dev.wolveringer.config.ConfigType;
import dev.wolveringer.config.descriptor.ConfigClassDescriptor;
import dev.wolveringer.config.exception.ConfigException;
import dev.wolveringer.config.exception.InvalidConfigClassException;
import lombok.NonNull;

import java.io.File;

/**
 * Created by wolverindev on 09.09.17.
 */
public class YamlConfig extends AbstractConfig implements Config {
    private transient ConfigConfiguration configuration;

    /**
     * Use this class as a segment structure class
     */
    public YamlConfig(){
        configuration = null;
    }

    /**
     * Setup class as a config file
     */
    public YamlConfig(@NonNull File file){
        this(new ConfigConfiguration(file));
    }

    /**
     * Setup class as a config file
     * @param configuration Specific config and parse options
     */
    public YamlConfig(@NonNull ConfigConfiguration configuration){
        this.configuration = configuration;
    }

    private void validateSaveLoadState() throws ConfigException {
        if(configuration == null) throw new InvalidConfigClassException("Class isn't a valid config class (Maybe just a segment)");
    }

    private ConfigClassDescriptor buildDescriptor(){
        return new ConfigClassDescriptor((Class<Config>) (Class) this.getClass()).parse();
    }

    @Override
    public void load() throws ConfigException {
        validateSaveLoadState();
        YamlConfigHelper.loadConfigFromFile(this, buildDescriptor(), configuration);
    }

    @Override
    public void save() throws ConfigException {
        validateSaveLoadState();
        YamlConfigHelper.saveConfigToFile(this, buildDescriptor(), configuration);
    }

    @Override
    public String saveAsString() throws ConfigException {
        validateSaveLoadState();
        return YamlConfigHelper.saveConfigToString(this, buildDescriptor(), configuration);
    }

    @Override
    public void loadFromString(String value) throws ConfigException {
        validateSaveLoadState();
        YamlConfigHelper.loadConfigFromString(this, value, buildDescriptor(), configuration);
    }

    @Override
    public ConfigType getBackendType() {
        return ConfigType.YAML;
    }

    @Override
    protected boolean checkConfig() throws ConfigException { return super.checkConfig(); }
}
