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

	public static HashMap<String, Object> getMap(Offering o) {
		HashMap<String, Object> objectMap = new HashMap<>();
		objectMap.put("classCode", o.getClassCode());
		objectMap.put("teacher", o.getInstructor());
		objectMap.put("capacity", o.getCapacity());
		objectMap.put("time", o.getClassTime().getTime());
		objectMap.put("registered", o.getNumRegisteredStudents());
		objectMap.put("course", o.getCourse());
		return objectMap;
	}

	public static String serialize(Offering o) throws JsonProcessingException {
		HashMap<String, Object> objectMap = getMap(o);

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(objectMap);
	}

	public static String serializeList(ArrayList<Offering> oList)
		throws JsonProcessingException {
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();

		for (Offering o : oList) {
			HashMap<String, Object> oMap = getMap(o);
			dataList.add(oMap);
		}

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(dataList);
	}

	public static Offering deserialize(String json)
		throws JsonProcessingException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Offering obj = mapper.readValue(json, Offering.class);

		HashMap<String, Object> offeringMap = mapper.readValue(json, HashMap.class);

		String courseCode = (String) offeringMap.get("classCode");
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
