import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;

public class Commands {

    private static ArrayList<Course> allCourses = new ArrayList<Course>();
    private static ArrayList<Student> allStds = new ArrayList<Student>();

    public Commands() {
    }

    private static void createJson(boolean b, String data, String s) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode message = mapper.createObjectNode();
        message.put("success", b);
        message.put(data, s);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
        System.out.println(json);
    }

    public static void addCourse(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Course newCourse = mapper.readValue(js, Course.class);
        allCourses.add(newCourse);
        createJson(true, "data", "Course added Successfully.");
    }

    public static void addStudent(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student newStd = mapper.readValue(js, Student.class);
        allStds.add(newStd);
        createJson(true, "data", "Student added Successfully.");
//
//        ObjectNode message = mapper.createObjectNode();
//        message.put("success", true);
//        message.put("data", "Student added Successfully");
//        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
//        System.out.println(json);
    }

    public static void addCourseToSch(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();
        Course fCourse = allCourses.stream().filter(course_t -> courseCode.equals(course_t.getCode())).findAny().orElse(null);
        if(fCourse != null){
            for (Student s : allStds){
                if(s.getStudentId().equals(stdId)){
                    s.addCourseToList(fCourse);
                    createJson(true, "data", "This Course added Successfully.");
//                    ObjectNode message = mapper.createObjectNode();
//                    message.put("success", true);
//                    message.put("data", "This Course added Successfully!");
//                    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
//                    System.out.println(json);
                }
            }
        }else {
            createJson(false, "error", "This Course not exist.");
//            ObjectNode message = mapper.createObjectNode();
//            message.put("success", false);
//            message.put("error", "This Course not exist!");
//            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
//            System.out.println(json);
        }
    }

    public static void removeCourseFromSch(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();
        Course fCourse = allCourses.stream().filter(course_t -> courseCode.equals(course_t.getCode())).findAny().orElse(null);
//        boolean flag = false;
//        for (Course c : allCourses){
//            if(c.getCode().equals(courseCode)){
//                course_t = c;
//                flag = true;
//            }
//        }
        if(fCourse != null){
            for (Student s : allStds){
                if(s.getStudentId().equals(stdId)) {
                    if (s.removeCourseFromList(fCourse)) {
                        createJson(true, "data", "This Course removed Successfully.");
//                        ObjectNode message = mapper.createObjectNode();
//                        message.put("success", true);
//                        message.put("data", "This Course removed Successfully!");
//                        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
//                        System.out.println(json);
                    }else {
                        createJson(false, "error", "This Student don't have this course.");

//                        ObjectNode message = mapper.createObjectNode();
//                        message.put("success", false);
//                        message.put("error", "This Student don't have this course!");
//                        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
//                        System.out.println(json);
                    }
                }
            }
        }else {
            createJson(false, "error", "This Course not exist.");

//            ObjectNode message = mapper.createObjectNode();
//            message.put("success", false);
//            message.put("error", "This Course not exist!");
//            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
//            System.out.println(json);
        }
    }
}
