package models.entities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import models.statics.Exceptions;

public class Offering {

	private String classCode;
	private String instructor;
	private int capacity;
	private Course course;
	private OfferingClassTime classTime;
	private OfferingExamTime examTime;

	private LinkedHashMap<String, Student> waitingStudents;
	private HashMap<String, Student> registeredStudents;

	public Offering() {
		this.registeredStudents = new HashMap<>();
		this.waitingStudents = new LinkedHashMap<>();
	}

	public String getClassCode() {
		return this.classCode;
	}

	public void setClassCode(String _classCode) {
		this.classCode = _classCode;
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

	public int getNumRegisteredStudents() {
		return this.registeredStudents.size();
	}

	public int getNumWaitingStudents() {
		return this.waitingStudents.size();
	}

	public int getRemainingCapacity() {
		return this.getCapacity() - this.getNumRegisteredStudents();
	}

	public boolean isFull() {
		return this.getRemainingCapacity() <= 0;
	}

	public void addStudent(Student s) {
		if (this.existStudent(s.getId())) return;
		if (this.isFull())
			this.waitingStudents.put(s.getId(),s);
		else
			this.registeredStudents.put(s.getId(), s);
	}

	public void removeStudent(String studentId) throws Exceptions.StudentNotFound {
		Student s_reg = this.registeredStudents.get(studentId);
		Student s_wait = this.waitingStudents.get(studentId);
		if (s_reg == null && s_wait == null) throw new Exceptions.StudentNotFound();
		if (s_reg != null) this.registeredStudents.remove(studentId);
		if (s_wait != null) this.waitingStudents.remove(studentId);
	}

	public boolean isRegisteredStudent(String studentId) {
		Student s = this.registeredStudents.get(studentId);
		return s != null;
	}

	public boolean isWaitingStudent(String studentId) {
		Student s = this.waitingStudents.get(studentId);
		return s != null;
	}

	public boolean existStudent(String studentId) {
		return this.isRegisteredStudent(studentId) || this.isWaitingStudent(studentId);
	}

	public void registerWaitingStudents() {
		int remainingCapacity = this.getRemainingCapacity();
		if (remainingCapacity >= this.getNumWaitingStudents()) {
			this.registeredStudents.putAll(this.waitingStudents);
			this.waitingStudents.clear();
			return;
		}
		for (int i = 0; i < remainingCapacity; i++) {
			Set<String> keys = this.waitingStudents.keySet();
			Iterator<String> iter = keys.iterator();
			String studentId = iter.next();
			Student s = this.waitingStudents.get(studentId);
			this.registeredStudents.put(studentId, s);
			this.waitingStudents.remove(studentId);
		}
	}

	public boolean hasOfferingTimeCollision(Offering c) {
		return this.classTime.hasCollision(c.getClassTime());
	}

	public boolean hasExamTimeCollision(Offering c) {
		return this.examTime.hasCollision(c.getExamTime());
	}
}
