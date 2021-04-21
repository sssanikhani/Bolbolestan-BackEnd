package models.serializers;

import java.util.HashMap;

import models.entities.Student;
import models.statics.Exceptions;

public class StudentSerializer {
    
    public HashMap<String, Object> serialize(Student s) throws Exceptions.offeringNotFound {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", s.getId());
        result.put("name", s.getName());
        result.put("secondName", s.getSecondName());
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
