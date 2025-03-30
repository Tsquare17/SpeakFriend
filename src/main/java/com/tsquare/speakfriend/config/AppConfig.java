package com.tsquare.speakfriend.config;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static AppConfig instance;
    private static final String configDirName = ".speakfriend";

    private static String configFile;
    private static final Properties props = new Properties();

    private AppConfig() throws IOException {
        configFile = System.getProperty("user.home") + "/"  + configDirName + "/config.properties";

        props.load(new FileInputStream(configFile));
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            try {
                instance = new AppConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }

    public void setProperty(String name, String value) throws IOException {
        props.setProperty(name, value);

        FileWriter fileWriter = new FileWriter(configFile);
        props.store(fileWriter, "Speak Friend App Config");
        fileWriter.close();
    }

    public String getDbFile() {
        return props.getProperty("db_file");
    }

    public void setDbFile(String path) throws IOException {
        setProperty("db_file", path);
    }
}
