package models.entities;

public class Course {

	private String code;
	private String name;
	private int units;

	public Course() {}

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

	public int getUnits() {
		return this.units;
	}

	public void setUnits(int _units) {
		this.units = _units;
	}
}
