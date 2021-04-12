package models.entities;

import java.util.ArrayList;
import java.util.HashMap;

import models.statics.Exceptions;

public class Offering {

	private String code;
	private String classCode;
	private String name;
	private String type;
	private String instructor;
	private int units;
	private OfferingClassTime classTime;
	private OfferingExamTime examTime;
	private int capacity;
	private ArrayList<String> prerequisites;

	private HashMap<String, Student> waitingStudents;
	private HashMap<String, Student> registeredStudents;

	public Offering() {
		this.registeredStudents = new HashMap<>();
		this.waitingStudents = new HashMap<>();
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String _code) {
		this.code = _code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String _name) {
		this.name = _name;
	}

	public String getInstructor() {
		return this.instructor;
	}

	public void setInstructor(String _instructor) {
		this.instructor = _instructor;
	}

	public int getUnits() {
		return this.units;
	}

	public void setUnits(int _units) {
		this.units = _units;
	}

	public int getCapacity() {
		return this.capacity;
	}

	public void setCapacity(int _capacity) {
		this.capacity = _capacity;
	}

	public int getNumRegisteredStudents() {
		return this.registeredStudents.size();
	}

	public int getNumWaitingStudents() {
		return this.waitingStudents.size();
	}

	public int getRemainingCapacity() {
		return this.getCapacity() - (this.getNumRegisteredStudents() + this.getNumWaitingStudents());
	}

	public void addStudent(Student s) {
		this.registeredStudents.put(s.getId(), s);
	}

	public void removeStudent(String studentId) throws Exceptions.StudentNotFound {
		Student s = this.registeredStudents.get(studentId);
		if (s == null) throw new Exceptions.StudentNotFound();
		this.registeredStudents.remove(studentId);
	}

	public boolean existStudent(String studentId) {
		Student s = this.registeredStudents.get(studentId);
		return s != null;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public ArrayList<String> getPrerequisites() {
		return prerequisites;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPrerequisites(ArrayList<String> prerequisites) {
		this.prerequisites = prerequisites;
	}

	public OfferingClassTime getClassTime() {
		return this.classTime;
	}

	public void setClassTime(OfferingClassTime _classTime) {
		this.classTime = _classTime;
	}

	public OfferingExamTime getExamTime() {
		return this.examTime;
	}

	public void setExamTime(OfferingExamTime _examTime) {
		this.examTime = _examTime;
	}

	public boolean hasOfferingTimeCollision(Offering c) {
		return this.classTime.hasCollision(c.getClassTime());
	}

	public boolean hasExamTimeCollision(Offering c) {
		return this.examTime.hasCollision(c.getExamTime());
	}
}
