package com.api.utils;

import io.qameta.allure.Attachment;

public class AllureUtil {

    @Attachment(value = "{0}", type = "application/json")
    public static String attachJson(String name, String json) {
        return json;
    }
}