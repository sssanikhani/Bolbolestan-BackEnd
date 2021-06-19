package models.entities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.statics.Exceptions;

public class Offering {

	private String classCode;
	private String instructor;
	private int capacity;
	private Course course;
	private OfferingClassTime classTime;
	private OfferingExamTime examTime;

	private LinkedHashSet<String> waitingStudents;
	private HashSet<String> registeredStudents;

	public Offering() {
		this.registeredStudents = new HashSet<>();
		this.waitingStudents = new LinkedHashSet<>();
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

	@JsonIgnore
	public HashSet<String> getRegisteredStudents() {
		return this.registeredStudents;
	}

	public int getNumRegisteredStudents() {
		return this.registeredStudents.size();
	}
	
	@JsonIgnore
	public void setRegisteredStudents(HashSet<String> _registered) {
		this.registeredStudents = _registered;
	}

	public LinkedHashSet<String> getWaitingStudents() {
		return this.waitingStudents;
	}

	public int getNumWaitingStudents() {
		return this.waitingStudents.size();
	}

	@JsonIgnore
	public void setWaitingStudents(LinkedHashSet<String> _waiting) {
		this.waitingStudents = _waiting;
	}
	
	@JsonIgnore
	public int getRemainingCapacity() {
		return this.getCapacity() - this.getNumRegisteredStudents();
	}

	public boolean isFull() {
		return this.getRemainingCapacity() <= 0;
	}

	public void addStudent(Student s) {
		if (this.existStudent(s.getId())) return;
		if (this.isFull()) this.waitingStudents.add(
				s.getId()
			); else this.registeredStudents.add(s.getId());
	}

	public void removeStudent(String studentId) throws Exceptions.StudentNotFound {
		boolean s_reg = this.registeredStudents.contains(studentId);
		boolean s_wait = this.waitingStudents.contains(studentId);
		if (!s_reg && !s_wait) throw new Exceptions.StudentNotFound();
		if (s_reg) this.registeredStudents.remove(studentId);
		if (s_wait) this.waitingStudents.remove(studentId);
	}

	public boolean isRegisteredStudent(String studentId) {
		return this.registeredStudents.contains(studentId);
	}

	public boolean isWaitingStudent(String studentId) {
		return this.waitingStudents.contains(studentId);
	}

	public boolean existStudent(String studentId) {
		return this.isRegisteredStudent(studentId) || this.isWaitingStudent(studentId);
	}

	public void registerWaitingStudents() {
		int remainingCapacity = this.getRemainingCapacity();
		if (remainingCapacity >= this.getNumWaitingStudents()) {
			this.registeredStudents.addAll(this.waitingStudents);
			this.waitingStudents.clear();
			return;
		}
		for (int i = 0; i < remainingCapacity; i++) {
			LinkedHashSet<String> temp = new LinkedHashSet<>();
			temp.addAll(this.waitingStudents);
			Iterator<String> iter = temp.iterator();
			String studentId = iter.next();
			this.registeredStudents.add(studentId);
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
