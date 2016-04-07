package com.maleshen.ssm.capp.model;

import com.maleshen.ssm.capp.ClientApp;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertiesLoader {
    private static final Properties properties = new Properties();
    private static final String PATH = "/resources/client/ssmc.properties";

    public static OutputStream getOutputStream() {
        try {
            return new FileOutputStream(
                    new File(
                            ClientApp.class.getResource(PATH).toURI()));
        } catch (FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Properties load() {
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("." + PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
