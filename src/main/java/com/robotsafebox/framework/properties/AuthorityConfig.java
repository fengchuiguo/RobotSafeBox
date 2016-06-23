package com.robotsafebox.framework.properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorityConfig {

    private static final Logger logger = LoggerFactory.getLogger(AuthorityConfig.class);

    private static final String fileName = "authority.properties";

    private static AuthorityConfig instance = new AuthorityConfig();

    private String[] interceptor;
    private String loginPage;
    private String defaultLoginPage;

    public String[] getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(String[] interceptor) {
        this.interceptor = interceptor;
    }

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public String getDefaultLoginPage() {
        return defaultLoginPage;
    }

    public void setDefaultLoginPage(String defaultLoginPage) {
        this.defaultLoginPage = defaultLoginPage;
    }

    public static AuthorityConfig getInstance() {

        return instance;
    }

    /**
     * private constructor
     */
    private AuthorityConfig() {

        loadProperties();

    }

    private void loadProperties() {

        try {
            PropertiesConfiguration properties = new PropertiesConfiguration(fileName);

            this.setInterceptor(properties.getStringArray("interceptor"));
            this.setLoginPage(properties.getString("loginPage"));
            this.setDefaultLoginPage(properties.getString("defaultLoginPage"));
        } catch (Exception e) {
            logger.error("加载配置文件失败", e);
        }

    }

}
