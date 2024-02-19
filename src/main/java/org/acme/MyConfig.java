package org.acme;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

import java.util.Map;

@ConfigMapping(prefix = "mycfg")
public interface MyConfig {

    @WithParentName
    Map<String, InnerConfig> innerConfigs();

    interface InnerConfig {
        String value();
    }

}
