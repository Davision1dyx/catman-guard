package org.davision1dyx.catmanguard.datasource.config;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

public class DataSourceAutoConfigurationExclude implements AutoConfigurationImportFilter {

    private static final String DATA_SOURCE_AUTO_CONFIG = "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration";
    private static final String TX_MANAGER_AUTO_CONFIG = "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration";

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] matches = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            String className = autoConfigurationClasses[i];
            matches[i] = !DATA_SOURCE_AUTO_CONFIG.equals(className) 
                    && !TX_MANAGER_AUTO_CONFIG.equals(className);
        }
        return matches;
    }
}