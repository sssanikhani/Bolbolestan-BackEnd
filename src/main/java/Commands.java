import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class Commands {

    public Commands() {
    }
    public static Course addCourse(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Course newCourse = mapper.readValue(js, Course.class);
        return newCourse;
    }

    public static Student addStudent(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student newStd = mapper.readValue(js, Student.class);
        return newStd;
    }

    public static void addCourseToSch(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();
        System.out.println(stdId);
        System.out.println(courseCode);
    }
}
