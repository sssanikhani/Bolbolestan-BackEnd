package application;

import java.util.ArrayList;

import models.database.repositories.OfferingRepository;
import models.entities.Offering;

public class SubmitWaitingListTask implements Runnable {

	@Override
	public void run() {
		ArrayList<Offering> offerings = OfferingRepository.getAll();
		for (Offering o : offerings) {
			o.registerWaitingStudents();
			OfferingRepository.updateStudents(o);
		}
	}
}
