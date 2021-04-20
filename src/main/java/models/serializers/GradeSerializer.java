package models.serializers;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.entities.Course;
import models.entities.Grade;
import models.logic.DataBase;

public class GradeSerializer {

	public static HashMap<String, Object> getMap(Grade g) {
		HashMap<String, Object> gradeMap = new HashMap<>();
		gradeMap.put("term", g.getTerm());
		gradeMap.put("grade", g.getGrade());
		gradeMap.put("passed", g.getPassed());
		gradeMap.put("course", g.getCourse());
		return gradeMap;
	}

	public static String serialize(Grade g) throws JsonProcessingException {
		HashMap<String, Object> gMap = getMap(g);

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(gMap);
	}

	public static String serializeList(ArrayList<Grade> gList) throws JsonProcessingException {
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();

		for (Grade g : gList) {
			HashMap<String, Object> gMap = getMap(g);
			dataList.add(gMap);
		}

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(dataList);
	}

	public static Grade deserialize(String json)
		throws JsonProcessingException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		HashMap<String, Object> gMap = mapper.readValue(json, HashMap.class);
		Grade g = mapper.readValue(json, Grade.class);

		String courseCode = (String) gMap.get("code");
		Course course = DataBase.CourseManager.getOrCreate(courseCode);
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
