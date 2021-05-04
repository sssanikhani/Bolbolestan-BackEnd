package models.entities;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Course {

	private String code;
	private String name;
	private String type;
	private int units;
	private ArrayList<String> prerequisites;

	public Course() {
		this.prerequisites = new ArrayList<>();
	}

	public Course(String _code) {
		this.code = _code;
		this.prerequisites = new ArrayList<>();
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

	public String getType() {
		return this.type;
	}

	public void setType(String _type) {
		this.type = _type;
	}

	public int getUnits() {
		return this.units;
	}

	public void setUnits(int _units) {
		this.units = _units;
	}

	@JsonIgnore
	public ArrayList<String> getPrerequisites() {
		return this.prerequisites;
	}

	public void setPrerequisites(ArrayList<String> _prerequisites) {
		this.prerequisites = _prerequisites;
	}
}
