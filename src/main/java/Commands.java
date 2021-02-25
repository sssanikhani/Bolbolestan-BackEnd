import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class Commands {

    private static ArrayList<Course> allCourses = new ArrayList<Course>();
    private static ArrayList<Student> allStds = new ArrayList<Student>();

    public Commands() {
    }
    public static void addCourse(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Course newCourse = mapper.readValue(js, Course.class);
        allCourses.add(newCourse);
        System.out.println(newCourse.getName());
    }

    public static void addStudent(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student newStd = mapper.readValue(js, Student.class);
        allStds.add(newStd);
        System.out.println(newStd.getName());
    }

    public static void addCourseToSch(String js) throws IOException {
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
                if(s.getStudentId().equals(stdId)){
                    s.addCourseToList(fCourse);
                    System.out.println("This Course added Successfully!");
                }
            }
        }else {
            System.out.println("This Course not exist!");
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
                if(s.getStudentId().equals(stdId)){
                    s.removeCourseFromList(fCourse);
                    System.out.println("This Course removes Successfully!");
                }
            }
        }else {
            System.out.println("This Course not exist!");
        }
    }
}
