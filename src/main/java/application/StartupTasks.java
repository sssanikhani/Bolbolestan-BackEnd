package application;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import models.logic.DataBase;

@Component
public class StartupTasks implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			System.out.println("Trying to retrieve data from external DataBase...");
			DataBase.OfferingManager.updateFromExternalServer();
			DataBase.StudentManager.updateFromExternalServer();
			System.out.println("All data have been received...");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: failed to connect with external server");
		}
	}
}
