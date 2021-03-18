package servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import models.logic.DataBase;

public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			System.out.println("Trying to retrieve data from external DataBase...");
			DataBase.OfferingManager.updateFromExternalServer();
			DataBase.StudentManager.updateFromExternalServer();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: failed to connect with external server");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}
}
