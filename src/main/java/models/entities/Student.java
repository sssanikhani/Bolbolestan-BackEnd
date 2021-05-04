package models.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import models.logic.DataBase;
import models.statics.Exceptions;

public class Student {

	private String id;
	private String name;
	private String secondName;
	private String email;
	private String password;
	private String birthDate;
	private String field;
	private String faculty;
	private String level;
	private String status;
	private String img;
	private HashMap<String, Offering> chosenOfferings;
	private HashMap<String, Offering> lastPlan;
	private TreeMap<Integer, Term> termsReport;
	private HashMap<String, Grade> grades;

	public Student() {
		this.chosenOfferings = new HashMap<>();
		this.lastPlan = new HashMap<>();
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

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String _email) {
		this.email = _email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String _password) {
		this.password = _password;
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

	public void _setChosenOfferings(HashMap<String, Offering> _chosenOfferings) {
		this.chosenOfferings = _chosenOfferings;
	}

	public Offering _getChosenOffering(String code, String classCode) {
		Offering o = this.chosenOfferings.get(code);
		if (o == null)
			return null;
		if (o.getClassCode().equals(classCode))
			return o;
		return null;
	}

	public ArrayList<Offering> getLastPlan() {
		return new ArrayList<>(this.lastPlan.values());
	}

	public void _setLastPlan(HashMap<String, Offering> _lastPlan) {
		this.lastPlan = _lastPlan;
	}

	public Offering _getLastOffering(String code, String classCode) {
		Offering o = this.lastPlan.get(code);
		if (o == null)
			return null;
		if (o.getClassCode().equals(classCode))
			return o;
		return null;
	}

	public boolean existsOffering(String code, String classCode) {
		Offering o1 = this._getChosenOffering(code, classCode);
		Offering o2 = this._getLastOffering(code, classCode);
		if (o1 != null || o2 != null)
			return true;
		return false; 
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

	public String getOfferingStatus(Offering o) {
		if (o.isRegisteredStudent(this.id)) return "registered";
		if (o.isWaitingStudent(this.id)) return "waiting";
		
		String courseCode = o.getCourse().getCode();
		if (this.chosenOfferings.get(courseCode) != null) return "chosen";
		
		return "none";
	}

	public boolean hasPassed(String _code) {
		Grade grade = this.grades.get(_code);
		if (grade == null) return false;
		return grade.getPassed();
	}

	public boolean hasPassedPrerequisites(String _code) throws Exceptions.offeringNotFound {
		Course course = DataBase.CourseManager.get(_code);
		ArrayList<String> preCourses = course.getPrerequisites();
		for (String preCourse : preCourses) {
			if (!hasPassed(preCourse)) return false;
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
		Offering submittedOffering = this.lastPlan.get(_code);
		if (submittedOffering != null)
			this.lastPlan.remove(_code);
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

	public void finalizeOfferings() {
		lastPlan.clear();
		lastPlan.putAll(chosenOfferings);
		for (Offering o : this.chosenOfferings.values()) {
			o.addStudent(this);
			DataBase.OfferingManager.updateStudents(o);
		}
	}

	public void resetPlan() {
		chosenOfferings.clear();
		chosenOfferings.putAll(lastPlan);
	}
}
