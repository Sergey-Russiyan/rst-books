package com.api.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/test/resources/config.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getBaseUrl() {
        return props.getProperty("api.base.url");
    }

    public static String getBooksEndpoint() {
        return props.getProperty("api.books.endpoint", "/books");
    }

    public static String getUsername() {
        return props.getProperty("api.username").trim();
    }

    public static String getPassword() {
        return props.getProperty("api.password").trim();
    }
}
