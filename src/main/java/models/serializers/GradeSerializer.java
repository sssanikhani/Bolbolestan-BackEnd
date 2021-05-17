package models.serializers;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.database.DataBase;
import models.entities.Course;
import models.entities.Grade;

public class GradeSerializer {

	public static HashMap<String, Object> serialize(Grade g) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("term", g.getTerm());
		result.put("grade", g.getGrade());
		result.put("passed", g.getPassed());
		result.put("course", g.getCourse());
		return result;
	}

	public static ArrayList<HashMap<String, Object>> serializeList(ArrayList<Grade> gList) {
		ArrayList<HashMap<String, Object>> result = new ArrayList<>();

		for (Grade g : gList) {
			HashMap<String, Object> gMap = serialize(g);
			result.add(gMap);
		}

		return result;
	}

	public static Grade deserialize(String json)
		throws JsonProcessingException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		HashMap<String, Object> gMap = mapper.readValue(json, HashMap.class);
		Grade g = mapper.readValue(json, Grade.class);

		String courseCode = (String) gMap.get("code");
		Course course = DataBase.CourseManager.getOrCreateM(courseCode);
		g.setCourse(course);

		return g;
	}

	public static ArrayList<Grade> deserializeList(String json)
		throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<HashMap<String, Object>> listMap = mapper.readValue(json, ArrayList.class);

		ArrayList<Grade> result = new ArrayList<>();
		for (HashMap<String, Object> gMap : listMap) {
			String oJson = mapper.writeValueAsString(gMap);
			Grade obj = deserialize(oJson);
			result.add(obj);
		}

		return result;
	}
}
