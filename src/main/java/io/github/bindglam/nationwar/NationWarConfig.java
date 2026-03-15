package io.github.bindglam.nationwar;

import com.bindglam.config.Configuration;
import com.bindglam.config.Field;

import java.io.File;

public final class NationWarConfig extends Configuration {
    public final Core core = new Core();
    public final class Core {
        public final Field<Integer> defaultMaxHealth = createPrimitiveField("core.default-max-health", 10000);
    }

    public NationWarConfig(File file) {
        super(file);
    }
}
