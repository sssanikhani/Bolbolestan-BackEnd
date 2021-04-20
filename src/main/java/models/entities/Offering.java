package models.entities;

import java.util.ArrayList;
import java.util.HashMap;

import models.statics.Exceptions;

public class Offering {

	private String code;
	private String instructor;
	private int capacity;
	private Course course;
	private OfferingClassTime classTime;
	private OfferingExamTime examTime;
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

	
	public String getInstructor() {
		return this.instructor;
	}
	
	public void setInstructor(String _instructor) {
		this.instructor = _instructor;
	}
	
	public int getCapacity() {
		return this.capacity;
	}
	
	public void setCapacity(int _capacity) {
		this.capacity = _capacity;
	}

	public Course getCourse() {
		return this.course;
	}

	public void setCourse(Course _course) {
		this.course = _course;
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

	public ArrayList<String> getPrerequisites() {
		return prerequisites;
	}

	public void setPrerequisites(ArrayList<String> prerequisites) {
		this.prerequisites = prerequisites;
	}

	public int getNumRegisteredStudents() {
		return this.registeredStudents.size();
	}

	public int getNumWaitingStudents() {
		return this.waitingStudents.size();
	}

	public int getRemainingCapacity() {
		return (
			this.getCapacity() -
			(this.getNumRegisteredStudents() + this.getNumWaitingStudents())
		);
	}

	public void addStudent(Student s) {
		this.waitingStudents.put(s.getId(), s);
	}

	public void removeStudent(String studentId) throws Exceptions.StudentNotFound {
		Student s_reg = this.registeredStudents.get(studentId);
		Student s_wait = this.waitingStudents.get(studentId);
		if (s_reg == null && s_wait == null) throw new Exceptions.StudentNotFound();
		if (s_reg != null) this.registeredStudents.remove(studentId);
		if (s_wait != null) this.waitingStudents.remove(studentId);
	}

	public boolean existStudent(String studentId) {
		Student s_reg = this.registeredStudents.get(studentId);
		Student s_wait = this.waitingStudents.get(studentId);
		return s_reg != null || s_wait != null;
	}

	public void registerWaitingStudents() {
		this.registeredStudents.putAll(this.waitingStudents);
		this.waitingStudents.clear();
	}

	public boolean hasOfferingTimeCollision(Offering c) {
		return this.classTime.hasCollision(c.getClassTime());
	}

	public boolean hasExamTimeCollision(Offering c) {
		return this.examTime.hasCollision(c.getExamTime());
	}
}
