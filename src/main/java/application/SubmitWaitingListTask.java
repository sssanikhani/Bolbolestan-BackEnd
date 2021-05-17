package application;

import java.util.ArrayList;

import models.database.DataBase;
import models.entities.Offering;

public class SubmitWaitingListTask implements Runnable {

	@Override
	public void run() {
		ArrayList<Offering> offerings = DataBase.OfferingManager.getAll();
		for (Offering o : offerings) {
			o.registerWaitingStudents();
			DataBase.OfferingManager.updateStudents(o);
		}
	}
}
