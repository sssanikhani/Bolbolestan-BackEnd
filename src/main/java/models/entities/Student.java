package models.entities;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import models.logic.DataBase;
import models.statics.Exceptions;
import models.utils.Utils;

public class Student {

	private String id;
	private String name;
	private String secondName;
	private String birthDate;
	private HashMap<String, Offering> chosenOfferings;
	private HashMap<String, Offering> lastPlan = new HashMap<>();
	private HashMap<String, Grade> grades;

	public Student() {
		this.chosenOfferings = new HashMap<String, Offering>();
		this.grades = new HashMap<String, Grade>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String _name) {
		this.name = _name;
	}

	public ArrayList<Offering> getChosenOfferings() {
		return new ArrayList<Offering>(this.chosenOfferings.values());
	}

	public ArrayList<Offering> getLastPlan() {
		return new ArrayList<Offering>(this.lastPlan.values());
	}

	public List<LinkedHashMap<String, Object>> _getChosenOfferingsData() {
		List<LinkedHashMap<String, Object>> data = new ArrayList<LinkedHashMap<String, Object>>();
		for (String code : this.chosenOfferings.keySet()) {
			Offering o = this.chosenOfferings.get(code);
			LinkedHashMap<String, Object> oData = new LinkedHashMap<String, Object>();

			oData.put("code", o.getCode());
			oData.put("name", o.getName());
			oData.put("instructor", o.getInstructor());
			oData.put("classTime", o.getClassTime());
			oData.put("examTime", o.getExamTime());
			boolean isFinalized = o.existStudent(this.id);
			String finalized = isFinalized ? "finalized" : "non-finalized";
			oData.put("status", finalized);

			data.add(oData);
		}
		return data;
	}

	public HashMap<String, Grade> _getGrades() {
		return this.grades;
	}

	public void addGrade(Grade _grade) {
		String code = _grade.getCode();
		this.grades.put(code, _grade);
	}

	public int getNumberChosenUnits() {
		int units = 0;
		for (String key : this.chosenOfferings.keySet()) {
			Offering o = this.chosenOfferings.get(key);
			units += o.getUnits();
		}
		return units;
	}

	public boolean hasPassed(String _code) {
		Grade grade = this.grades.get(_code);
		if (grade == null) return false;
		if (grade.getGrade() < 10) return false;
		return true;
	}

	public boolean hasPassedPrerequisites(String _code) throws Exceptions.offeringNotFound {
		ArrayList<Offering> codeOfferings = DataBase.OfferingManager.getCodeOfferings(_code);
		if (codeOfferings.size() == 0) throw new Exceptions.offeringNotFound();
		Offering o = codeOfferings.get(0);
		ArrayList<String> preCodes = o.getPrerequisites();
		for (String preCode : preCodes) {
			if (!hasPassed(preCode)) return false;
		}
		return true;
	}

	public ArrayList<Grade> getPassedCoursesGrades() {
		ArrayList<Grade> passedGrades = new ArrayList<Grade>();
		for (Grade g : this.grades.values()) {
			if (this.hasPassed(g.getCode())) passedGrades.add(g);
		}
		return passedGrades;
	}

	public int getTotalPassedUnits() throws Exceptions.offeringNotFound {
		int passed = 0;
		for (Grade g : this.grades.values()) {
			String code = g.getCode();
			if (this.hasPassed(code)) {
				ArrayList<Offering> codeOfferings = DataBase.OfferingManager.getCodeOfferings(
					code
				);
				if (codeOfferings.size() == 0) throw new Exceptions.offeringNotFound();
				passed += codeOfferings.get(0).getUnits();
			}
		}
		return passed;
	}

	public float getGpa() throws Exceptions.offeringNotFound {
		float sumGrades = 0;
		int totalUnits = 0;
		for (Grade g : this.grades.values()) {
			String code = g.getCode();
			int unit = Utils.getCodeUnits(code);
			sumGrades += g.getGrade() * unit;
			totalUnits += unit;
		}
		if (totalUnits == 0) return 0;

		return sumGrades / totalUnits;
	}

	public void addOfferingToList(Offering o) {
		this.chosenOfferings.put(o.getCode(), o);
	}

	public void removeOfferingFromList(String _code) throws Exceptions.offeringNotFound {
		Offering offering = this.chosenOfferings.get(_code);
		if (offering == null) throw new Exceptions.offeringNotFound();
		boolean finalized = offering.existStudent(this.id);
		try {
			if (finalized) offering.removeStudent(this.id);
		} catch (Exceptions.StudentNotFound e) {
			// Never Execute
		}
		this.chosenOfferings.remove(_code);
	}

	public void validateExamClassTimes()
		throws ParseException, Exceptions.ExamTimeCollision, Exceptions.ClassTimeCollision {
		for (String k1 : this.chosenOfferings.keySet()) {
			for (String k2 : this.chosenOfferings.keySet()) {
				if (!k1.equals(k2)) {
					Offering o1 = this.chosenOfferings.get(k1);
					Offering o2 = this.chosenOfferings.get(k2);
					if (
						o1.hasOfferingTimeCollision(o2)
					) throw new Exceptions.ClassTimeCollision(o1.getCode(), o2.getCode());
					if (o1.hasExamTimeCollision(o2)) throw new Exceptions.ExamTimeCollision(
						o1.getCode(),
						o2.getCode()
					);
				}
			}
		}
	}

	public void validateOfferingCapacities() throws Exceptions.OfferingCapacity {
		Set<String> offeringKeySet = chosenOfferings.keySet();
		for (String key : offeringKeySet) {
			Offering o = chosenOfferings.get(key);
			if (
				o.getCapacity() <= o.getNumRegisteredStudents()
			) throw new Exceptions.OfferingCapacity(o.getCode());
		}
	}

	public void finalizeOfferings() {
		lastPlan.clear();
		lastPlan.putAll(chosenOfferings);
		for (Offering o : this.chosenOfferings.values()) {
			o.addStudent(this);
		}
	}

	public void resetPlan() {
		chosenOfferings.clear();
		chosenOfferings.putAll(lastPlan);
	}
}
