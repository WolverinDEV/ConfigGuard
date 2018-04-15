package dev.wolveringer.config;

import lombok.*;
import lombok.experimental.Wither;

import java.io.File;

/**
 * Created by wolverindev on 09.09.17.
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Wither
public class ConfigConfiguration {
    public interface DebugFunction {
        void log(String message);
    }

    @RequiredArgsConstructor
    @Getter
    public enum EnforcementType {
        NONE(false),
        PREFERRED(false),
        FORCED(true);

        private final boolean required;
    }
    private final @NonNull File configFile;

    private boolean createIfNotExist = true;
    private DebugFunction debug = message -> {};

    private EnforcementType configurationAnnonation = EnforcementType.PREFERRED;
    private EnforcementType pathAnnonation = EnforcementType.PREFERRED;

    private EnforcementType callConstructor = EnforcementType.PREFERRED;
    private EnforcementType useEnumName = EnforcementType.PREFERRED;

}
