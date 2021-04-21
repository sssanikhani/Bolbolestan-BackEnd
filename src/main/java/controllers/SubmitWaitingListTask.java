package controllers;

import java.util.ArrayList;

import models.entities.Offering;
import models.logic.DataBase;

public class SubmitWaitingListTask implements Runnable {

	@Override
	public void run() {
		ArrayList<Offering> offerings = DataBase.OfferingManager.getAll();
		for (Offering o : offerings) {
			o.registerWaitingStudents();
		}
	}
}
