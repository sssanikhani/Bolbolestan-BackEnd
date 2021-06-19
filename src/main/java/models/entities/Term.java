package models.entities;

import java.util.ArrayList;
import java.util.HashMap;

public class Term {

	private int term;
	private HashMap<String, Grade> grades;

	public Term(int _term) {
		this.term = _term;
		this.grades = new HashMap<>();
	}

	public int getTerm() {
		return this.term;
	}

	public int getUnits() {
		int units = 0;
		for (Grade g : this.grades.values()) {
			units += g.getCourse().getUnits();
		}
		return units;
	}

	public float getGpa() {
		float total = 0;
		for (Grade g : this.grades.values()) {
			total += g.getGrade() * g.getCourse().getUnits();
		}
		return total / this.getUnits();
	}

	public ArrayList<Grade> getGrades() {
		return new ArrayList<Grade>(this.grades.values());
	}

	public void addGrade(Grade _grade) {
		String code = _grade.getCourse().getCode();
		this.grades.put(code, _grade);
	}
}
