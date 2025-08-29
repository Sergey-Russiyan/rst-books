package com.api.tests.base;

import com.api.config.AllureEnvWriter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeSuite;

@Slf4j
public class BaseTest {

    @BeforeSuite
    public void setupAllureEnvironment() {
        AllureEnvWriter.writeEnvironment("target/allure-results");
    }
}
