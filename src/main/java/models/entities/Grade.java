package models.entities;

public class Grade {

	private float grade;
	private int term;
	private Course course;

	public Course getCourse() {
		return this.course;
	}

	public void setCourse(Course _course) {
		this.course = _course;
	}

	public float getGrade() {
		return this.grade;
	}

	public void setGrade(float _grade) {
		this.grade = _grade;
	}

	public boolean getPassed() {
		return this.grade >= 10;
	}

	public int getTerm() {
		return this.term;
	}

	public void setTerm(int _term) {
		this.term = _term;
	}
}
