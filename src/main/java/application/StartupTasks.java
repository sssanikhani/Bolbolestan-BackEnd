package application;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import models.database.DataBase;
import models.database.repositories.OfferingRepository;
import models.database.repositories.StudentRepository;

@Component
public class StartupTasks implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			System.out.println("Trying to retrieve data from external DataBase...");
			OfferingRepository.updateFromExternalServer();
			StudentRepository.updateFromExternalServer();
			System.out.println("All data have been received");
			System.out.println("Updating local database...");
			DataBase.updateLocalDataBase();
			System.out.println("Local database updated successfully");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: failed to connect with external server");
		}
	}
}
