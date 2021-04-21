package models.serializers;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.entities.Course;
import models.entities.Offering;
import models.logic.DataBase;

public class OfferingSerializer {

	public static HashMap<String, Object> serialize(Offering o) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("classCode", o.getClassCode());
		result.put("teacher", o.getInstructor());
		result.put("capacity", o.getCapacity());
		result.put("time", o.getClassTime().getTime());
		result.put("registered", o.getNumRegisteredStudents());
		result.put("course", o.getCourse());
		return result;
	}

	public static ArrayList<HashMap<String, Object>> serializeList(ArrayList<Offering> oList) {
		ArrayList<HashMap<String, Object>> result = new ArrayList<>();

		for (Offering o : oList) {
			HashMap<String, Object> oMap = serialize(o);
			result.add(oMap);
		}

		return result;
	}

	public static Offering deserialize(String json)
		throws JsonProcessingException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Offering obj = mapper.readValue(json, Offering.class);

		HashMap<String, Object> offeringMap = mapper.readValue(json, HashMap.class);

		String courseCode = (String) offeringMap.get("code");
		String courseName = (String) offeringMap.get("name");
		String courseType = (String) offeringMap.get("type");
		int courseUnits = (int) offeringMap.get("units");
		DataBase.CourseManager.updateOrCreate(courseCode, courseName, courseType, courseUnits);
		Course course = DataBase.CourseManager.get(courseCode);
		ArrayList<String> prerequisitesCode = (ArrayList<String>) offeringMap.get(
			"prerequisites"
		);
		ArrayList<Course> prerequisites = new ArrayList<>();
		for (String code : prerequisitesCode) {
			Course preCourse = DataBase.CourseManager.getOrCreate(code);
			prerequisites.add(preCourse);
		}
		course.setPrerequisites(prerequisites);

		obj.setCourse(course);

		return obj;
	}

	public static ArrayList<Offering> deserializeList(String json)
		throws JsonProcessingException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<HashMap<String, Object>> listMap = mapper.readValue(json, ArrayList.class);

		ArrayList<Offering> result = new ArrayList<>();
		for (HashMap<String, Object> oMap : listMap) {
			String oJson = mapper.writeValueAsString(oMap);
			Offering obj = deserialize(oJson);
			result.add(obj);
		}

		return result;
	}
}
