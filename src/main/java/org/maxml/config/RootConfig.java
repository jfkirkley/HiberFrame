package org.maxml.config;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class RootConfig {

    public static String     rootPath           = "/home/jkirkley";
    private static String    rootConfigFileName = "config.properties";

    private Properties       rootConfigProperties;

    public static RootConfig instance           = null;

    public RootConfig() {
        rootConfigProperties = loadProperties(rootPath + rootConfigFileName);
    }

    public RootConfig(String rpath) {
        rootPath = rpath;

        loadProperties(rootPath + rootConfigFileName);
    }

    public static RootConfig i() {
        if (instance == null) {
            instance = new RootConfig();
        }
        return instance;
    }

    public static RootConfig i(String rpath) {
        if (instance == null) {
            instance = new RootConfig(rpath);
        }
        return instance;
    }

    public String getXmlDir() {
        return null;
    }

    public String getImageFilePath() {
        return null;
    }

    public String get(String propertyName) {
        return (String) rootConfigProperties.get(propertyName);
    }

    public String getProperty(String propertyName) {
        return get(propertyName);
    }

    public String getPath(String propertyName) {
        return rootPath + get(propertyName);
    }

    public Properties loadProperties(String propertyFileName) {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(propertyFileName);
            properties.load(fis);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return properties;
    }

}
