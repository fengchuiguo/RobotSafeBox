package com.robotsafebox.framework.properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.UnsupportedEncodingException;

public class PropertiesConfig {

    public static PropertiesConfiguration sysConfig;

    private static PropertiesConfiguration smsConfigProperties;

    public static String getConfig(String key) {
        if (sysConfig == null) {
            try {
                sysConfig = new PropertiesConfiguration("config.properties");
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        return sysConfig.getString(key);
    }

    public static String getSmsConfigByKey(String key) {
        if (smsConfigProperties == null) {
            try {
                smsConfigProperties = new PropertiesConfiguration("sms.properties");
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
        return smsConfigProperties.getString(key);
    }

    public static String getImagePath() {
        return getConfig("backgroundVistUrl");
    }

    public static String getWebTitle() {
        try {
            return new String(getConfig("webtitle").getBytes("iso8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUploadRoot() {
        return PropertiesConfig.getConfig("uploadRoot");
    }

}
