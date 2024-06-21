package com.example.ordersystem.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {
    private Properties properties = new Properties();

    public ApplicationConfig(String env) {
        String configFileName = "application" + (env.equals("test") ? "-test" : "") + ".properties";

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find " + configFileName);
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
