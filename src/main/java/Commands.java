import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Commands {

//    private static ArrayList<Course> allCourses = new ArrayList<Course>();
    public static HashMap<String, Offer> allOffers = new HashMap<String, Offer>();
    public static HashMap<String, ArrayList<Offer>> allOffersOfACourse = new HashMap<String, ArrayList<Offer>>();
    //    private static ArrayList<Student> allStds = new ArrayList<Student>();
    public static HashMap<String, Student> allStds = new HashMap<String, Student>();

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
        Offer newCourse = mapper.readValue(js, Offer.class);
//        allCourses.add(newCourse);
        allOffers.put(newCourse.getCode(), newCourse);
//        allOffersOfACourse.put(newCourse.getName(), newCourse);
        if(allOffersOfACourse.get(newCourse.getName()) != null) {
            allOffersOfACourse.get(newCourse.getName()).add(newCourse);
        } else  {
            allOffersOfACourse.put(newCourse.getName(), new ArrayList<Offer>());
            allOffersOfACourse.get(newCourse.getName()).add(newCourse);

        }
        createOutputJson(true, "data", "OfferingAdded");
    }

    public static void addStudent(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student newStd = mapper.readValue(js, Student.class);
//        allStds.add(newStd);
        allStds.put(newStd.getStudentId(), newStd);
        createOutputJson(true, "data", "StudentAdded");
    }

    public static void addCourseToSch(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();

        if(allOffers.get(courseCode) != null) {
            if(allStds.get(stdId) != null) {
                allStds.get(stdId).addCourseToList(allOffers.get(courseCode));
                createOutputJson(true, "data", "CourseAdded");
            }
        } else {
            createOutputJson(false, "error", "OfferingNotFound");
        }

//
//        Course fCourse = allCourses.stream().filter(course_t -> courseCode.equals(course_t.getCode())).findAny().orElse(null);
//        if(fCourse != null){
//            for (Student s : allStds){
//                if(s.getStudentId().equals(stdId)){
//                    s.addCourseToList(fCourse);
//                    createOutputJson(true, "data", "CourseAdded");
//                }
//            }
//        }else {
//            createOutputJson(false, "error", "OfferingNotFound");
//        }
    }

    public static void removeCourseFromSch(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();

        if(allOffers.get(courseCode) != null) {
            if(allStds.get(stdId) != null) {
                if ((allStds.get(stdId).removeCourseFromList(allOffers.get(courseCode))) != null) {
                    createOutputJson(true, "data", "OfferingRemoved");
                }else {
                    createOutputJson(false, "error", "OfferingNotFound");
                }
            }
        } else {
            createOutputJson(false, "error", "OfferingNotFound");
        }

//        Course fCourse = allCourses.stream().filter(course_t -> courseCode.equals(course_t.getCode())).findAny().orElse(null);
//        if(fCourse != null){
//            for (Student s : allStds){
//                if(s.getStudentId().equals(stdId)) {
//                    if (s.removeCourseFromList(fCourse)) {
//                        createOutputJson(true, "data", "OfferingRemoved");
//                    }else {
//                        createOutputJson(false, "error", "OfferingNotFound");
//                    }
//                }
//            }
//        }else {
//            createOutputJson(false, "error", "OfferingNotFound");
//        }
    }

    public static void getOffer(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();
//        Course fCourse = allCourses.stream().filter(course_t -> courseCode.equals(course_t.getCode())).findAny().orElse(null);
        String message = "\"data\": ";
        if(allOffers.get(courseCode) != null) {
            message += mapper.writerWithView(View.normal.class).writeValueAsString(allOffers.get(courseCode));
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
        message += mapper.writerWithView(View.offerings.class).writeValueAsString(allOffersOfACourse.values());
        System.out.println(message);
    }

    public static void getWeeklySch(String js) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String message = "\"data\": {\"weeklySchdule\": ";

//        Student fStd = allStds.stream().filter(std_f -> stdId.equals(std_f.getStudentId())).findAny().orElse(null);

        message += mapper.writerWithView(View.weeklySch.class).writeValueAsString(allStds.get(stdId).getWeeklyCourses().values());
        message += "}";
        System.out.println(message);
    }

//    public static void finalizeSch(String js) throws JsonProcessingException, ParseException {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode jn = mapper.readTree(js);
//        String stdId = jn.get("StudentId").asText();
//        Student fStd = allStds.stream().filter(std_f -> stdId.equals(std_f.getStudentId())).findAny().orElse(null);
//        ArrayList<Course> stdCourses = fStd.getWeeklyCourses();
//        if(stdCourses == null) {
//            createOutputJson(false, "error", "Course list is empty.");
//            return;
//        }
//        int units = 0;
//        for (int i = 0; i< stdCourses.size(); i++) {
//            units += stdCourses.get(i).getUnits();
//            for (int j = i+1; j< stdCourses.size(); j++) {
//                checkClassTime(stdCourses.get(i).getClassTime(), stdCourses.get(j).getClassTime());
//            }
//        }
//    }
//
//    public static void checkClassTime(CourseClassTime c1, CourseClassTime c2) throws ParseException {
//        System.out.println("Here");
//        int m, n;
//        for(m = 0; m< c1.getDays().size(); m++) {
//            for (n = 0; n< c2.getDays().size(); n++) {
//                if(c1.getDays().get(m) == c2.getDays().get(n)){
//                    System.out.println(c1.getDays().get(m));
//                    SimpleDateFormat format = new SimpleDateFormat("HH:mm-HH:mm");
//                    Date c1Date = format.parse(c1.getTime());
//                    Date c2Date = format.parse(c2.getTime());
//                    System.out.println(c1Date);
//                    System.out.println(c2Date);
//                }
//            }
//        }
//
//    }


}
