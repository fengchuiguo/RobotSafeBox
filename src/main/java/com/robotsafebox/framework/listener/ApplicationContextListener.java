package com.robotsafebox.framework.listener;

import com.robotsafebox.framework.properties.AuthorityConfig;
import com.robotsafebox.framework.properties.PropertiesConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {

//        //init properties todo：暂未使用
//        AuthorityConfig.getInstance();

        ServletContext application = servletContextEvent.getServletContext();
        //context path
        application.setAttribute("path", application.getContextPath());
        //image path
        application.setAttribute("imagePath", PropertiesConfig.getImagePath());
        //webtitle
        application.setAttribute("webtitle", PropertiesConfig.getWebTitle());
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext application = servletContextEvent.getServletContext();
        application.removeAttribute("path");
        application.removeAttribute("imagePath");
    }

}
