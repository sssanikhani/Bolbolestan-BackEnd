package models.serializers;

import java.util.HashMap;

import models.entities.Student;

public class StudentSerializer {

	public static HashMap<String, Object> serialize(Student s) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("id", s.getId());
		result.put("name", s.getName());
		result.put("secondName", s.getSecondName());
		result.put("email", s.getEmail());
		result.put("password", s.getPassword());
		result.put("birthDate", s.getBirthDate());
		result.put("gpa", s.getGpa());
		result.put("totalPassedUnits", s.getTotalPassedUnits());
		result.put("faculty", s.getFaculty());
		result.put("level", s.getLevel());
		result.put("field", s.getField());
		result.put("status", s.getStatus());
		result.put("img", s.getImg());
		return result;
	}
}
