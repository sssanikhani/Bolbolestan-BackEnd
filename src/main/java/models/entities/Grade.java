package models.entities;

public class Grade {

	private String code;
	private float grade;
	private int term;

	public String getCode() {
		return this.code;
	}

	public void setCode(String _code) {
		this.code = _code;
	}

	public float getGrade() {
		return this.grade;
	}

	public void setGrade(float _grade) {
		this.grade = _grade;
	}

	public int getTerm() {
		return this.term;
	}

	public void setTerm(int _term) {
		this.term = _term;
	}
}
