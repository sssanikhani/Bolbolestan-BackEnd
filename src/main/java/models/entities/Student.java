package models.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import models.logic.DataBase;
import models.statics.Exceptions;

public class Student {

	private String id;
	private String name;
	private String secondName;
	private String birthDate;
	private String field;
	private String faculty;
	private String level;
	private String status;
	private String img;
	private HashMap<String, Offering> chosenOfferings;
	private HashMap<String, Offering> lastPlan = new HashMap<>();
	private TreeMap<Integer, Term> termsReport;
	private HashMap<String, Grade> grades;

	public Student() {
		this.chosenOfferings = new HashMap<>();
		this.termsReport = new TreeMap<>();
		this.grades = new HashMap<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String _name) {
		this.name = _name;
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

	public String getField() {
		return this.field;
	}

	public void setField(String _field) {
		this.field = _field;
	}

	public String getFaculty() {
		return this.faculty;
	}

	public void setFaculty(String _faculty) {
		this.faculty = _faculty;
	}

	public String getLevel() {
		return this.level;
	}

	public void setLevel(String _level) {
		this.level = _level;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String _status) {
		this.status = _status;
	}

	public String getImg() {
		return this.img;
	}

	public void setImg(String _img) {
		this.img = _img;
	}

	public ArrayList<Offering> getChosenOfferings() {
		return new ArrayList<>(this.chosenOfferings.values());
	}

	public ArrayList<Offering> getLastPlan() {
		return new ArrayList<>(this.lastPlan.values());
	}

	public HashMap<String, Grade> _getGrades() {
		return this.grades;
	}

	public void addGrade(Grade _grade) {
		String code = _grade.getCourse().getCode();
		int term = _grade.getTerm();
		Term termObj = this.termsReport.computeIfAbsent(term, k -> new Term(term));

		termObj.addGrade(_grade);
		this.grades.put(code, _grade);
	}

	public int getNumberChosenUnits() {
		int units = 0;
		for (String key : this.chosenOfferings.keySet()) {
			Offering o = this.chosenOfferings.get(key);
			units += o.getCourse().getUnits();
		}
		return units;
	}

	public Term getTerm(int term) {
		return this.termsReport.get(term);
	}

	public ArrayList<Term> getTermsReport() {
		return new ArrayList<Term>(this.termsReport.values());
	}

	public ArrayList<Offering> getDayOfferings(String day) {
		ArrayList<Offering> result = new ArrayList<>();
		for (Offering o : this.lastPlan.values()) {
			ArrayList<String> oDays = o.getClassTime().getDays();
			if (oDays.contains(day)) result.add(o);
		}
		return result;
	}

	public HashMap<String, ArrayList<Offering>> getPlan() {
		HashMap<String, ArrayList<Offering>> result = new HashMap<>();
		String[] days = { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday" };
		for (String day : days) {
			result.put(day, this.getDayOfferings(day));
		}
		return result;
	}

	public boolean hasPassed(String _code) {
		Grade grade = this.grades.get(_code);
		if (grade == null) return false;
		return grade.getPassed();
	}

	public boolean hasPassedPrerequisites(String _code) throws Exceptions.offeringNotFound {
		Course course = DataBase.CourseManager.getOrCreate(_code);
		ArrayList<Course> preCourses = course.getPrerequisites();
		for (Course preCourse : preCourses) {
			if (!hasPassed(preCourse.getCode())) return false;
		}
		return true;
	}

	public ArrayList<Grade> getPassedCoursesGrades() {
		ArrayList<Grade> passedGrades = new ArrayList<>();
		for (Grade g : this.grades.values()) {
			String code = g.getCourse().getCode();
			if (this.hasPassed(code)) passedGrades.add(g);
		}
		return passedGrades;
	}

	public int getTotalPassedUnits() {
		int passed = 0;
		for (Grade g : this.grades.values()) {
			String code = g.getCourse().getCode();
			if (this.hasPassed(code)) {
				passed += DataBase.CourseManager.get(code).getUnits();
			}
		}
		return passed;
	}

	public float getGpa() {
		float sumGrades = 0;
		int totalUnits = 0;
		for (Grade g : this.grades.values()) {
			String code = g.getCourse().getCode();
			int unit = DataBase.CourseManager.get(code).getUnits();
			sumGrades += g.getGrade() * unit;
			totalUnits += unit;
		}
		if (totalUnits == 0) return 0;

		return sumGrades / totalUnits;
	}

	public void addOfferingToList(Offering o) {
		this.chosenOfferings.put(o.getCourse().getCode(), o);
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
		throws Exceptions.ExamTimeCollision, Exceptions.ClassTimeCollision {
		for (String k1 : this.chosenOfferings.keySet()) {
			for (String k2 : this.chosenOfferings.keySet()) {
				if (!k1.equals(k2)) {
					Offering o1 = this.chosenOfferings.get(k1);
					Offering o2 = this.chosenOfferings.get(k2);
					if (
						o1.hasOfferingTimeCollision(o2)
					) throw new Exceptions.ClassTimeCollision(
						o1.getCourse().getCode(),
						o2.getCourse().getCode()
					);
					if (o1.hasExamTimeCollision(o2)) throw new Exceptions.ExamTimeCollision(
						o1.getCourse().getCode(),
						o2.getCourse().getCode()
					);
				}
			}
		}
	}

	public void validateOfferingCapacities() throws Exceptions.OfferingCapacity {
		Set<String> offeringKeySet = chosenOfferings.keySet();
		for (String key : offeringKeySet) {
			Offering o = chosenOfferings.get(key);
			if (o.getRemainingCapacity() <= 0) {
				throw new Exceptions.OfferingCapacity(o.getCourse().getCode());
			}
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
