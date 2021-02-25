import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;

public class Commands {

    private static ArrayList<Course> allCourses = new ArrayList<Course>();
    private static ArrayList<Offer> Offers = new ArrayList<Offer>();
    private static ArrayList<Student> allStds = new ArrayList<Student>();

    public Commands() {
    }

    private static void createOutputJson(boolean b, String data, String s) throws JsonProcessingException {
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
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Offer newOffer = mapper.readValue(js, Offer.class);
        allCourses.add(newCourse);
        Offers.add(newOffer);
        createOutputJson(true, "data", "OfferingAdded");
    }

    public static void addStudent(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student newStd = mapper.readValue(js, Student.class);
        allStds.add(newStd);
        createOutputJson(true, "data", "StudentAdded");
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
                    createOutputJson(true, "data", "CourseAdded");
                }
            }
        }else {
            createOutputJson(false, "error", "OfferingNotFound");
        }
    }

    public static void removeCourseFromSch(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();
        Course fCourse = allCourses.stream().filter(course_t -> courseCode.equals(course_t.getCode())).findAny().orElse(null);
        if(fCourse != null){
            for (Student s : allStds){
                if(s.getStudentId().equals(stdId)) {
                    if (s.removeCourseFromList(fCourse)) {
                        createOutputJson(true, "data", "OfferingRemoved");
                    }else {
                        createOutputJson(false, "error", "OfferingNotFound");
                    }
                }
            }
        }else {
            createOutputJson(false, "error", "OfferingNotFound");
        }
    }

    public static void getOffer(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();
        Course fCourse = allCourses.stream().filter(course_t -> courseCode.equals(course_t.getCode())).findAny().orElse(null);
        if(fCourse != null) {
            String message = mapper.writeValueAsString(fCourse);
            System.out.println(message);
        } else {
            createOutputJson(false, "error", "OfferingNotFound");
        }

    }

    public static void getOffers(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String message = "\"data\": ";
        message += mapper.writeValueAsString(Offers);
        System.out.println(message);
    }
}
