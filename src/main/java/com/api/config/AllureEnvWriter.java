package com.api.config;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

@Slf4j
public class AllureEnvWriter {

    private static final String ENV_FILE = "environment.properties";
    private static final String ALLURE_PROPS_FILE = "allure.properties";

    public static void writeEnvironment(String resultsDir) {
        log.info("Writing Allure environment to '{}'", resultsDir);

        File dir = new File(resultsDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create directory: " + resultsDir);
        }
        writeEnvironmentProperties(dir);
        copyResourceToDir(ALLURE_PROPS_FILE, dir);
    }

    private static void writeEnvironmentProperties(File dir) {
        Properties props = new Properties();
        props.setProperty("Base URL", Config.getBaseUrl());
        props.setProperty("API Username", Config.getUsername());

        File envFile = new File(dir, ENV_FILE);
        try (FileOutputStream fos = new FileOutputStream(envFile)) {
            props.store(fos, "Allure Environment");
            log.info("Created '{}'", envFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Allure environment.properties", e);
        }
    }

    private static void copyResourceToDir(String resourceName, File dir) {
        Path target = dir.toPath().resolve(resourceName);

        try (InputStream is = AllureEnvWriter.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                log.warn("Resource '{}' not found on classpath, skipping.", resourceName);
                return;
            }
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied '{}' to '{}'", resourceName, target.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy resource " + resourceName, e);
        }
    }
}
