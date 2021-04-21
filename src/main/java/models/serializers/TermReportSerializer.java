package models.serializers;

import java.util.ArrayList;
import java.util.HashMap;

import models.entities.Grade;
import models.entities.Term;

public class TermReportSerializer {

	public static HashMap<String, Object> serialize(Term t) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("term", t.getTerm());
		result.put("gpa", t.getGpa());
		result.put("units", t.getUnits());
		ArrayList<Grade> grades = t.getGrades();
		ArrayList<HashMap<String, Object>> gradesData = GradeSerializer.serializeList(grades);
		result.put("grades", gradesData);
		return result;
	}

	public static ArrayList<HashMap<String, Object>> serializeList(ArrayList<Term> tList) {
		ArrayList<HashMap<String, Object>> result = new ArrayList<>();

		for (Term t : tList) {
			HashMap<String, Object> tData = serialize(t);
			result.add(tData);
		}

		return result;
	}
}
