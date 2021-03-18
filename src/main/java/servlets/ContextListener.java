package servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import models.logic.Handlers;

public class ContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Handlers.startup();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
