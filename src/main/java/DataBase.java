import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataBase {

    public static class OfferingManager {

        public static HashMap<String, HashMap<String, Offering>> courseOfferingsMap = new HashMap<String, HashMap<String, Offering>>();

        static String retrieveAllUrl = "http://138.197.181.131:5000/api/courses";

        public static void updateFromExternalServer() throws IOException, InterruptedException {
            HashMap<String, Object> webRes = Utils.sendRequest("GET", retrieveAllUrl, null, null);
            String data = (String) webRes.get("data");

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Offering> list = mapper.readValue(data, new TypeReference<ArrayList<Offering>>() {
            });

            courseOfferingsMap.clear();
            for (Offering o : list) {
                String code = o.getCode();
                String classCode = o.getClassCode();
                if (courseOfferingsMap.get(code) == null)
                    courseOfferingsMap.put(code, new HashMap<String, Offering>());
                courseOfferingsMap.get(code).put(classCode, o);
            }
        }

        public static ArrayList<Offering> getAll() throws IOException, InterruptedException {
            ArrayList<Offering> list = new ArrayList<Offering>();
            for (HashMap<String, Offering> group : courseOfferingsMap.values()) {
                list.addAll(group.values());
            }
            return list;
        }

        public static Offering get(String code, String classCode) throws Exception {
            HashMap<String, Offering> group = courseOfferingsMap.get(code);
            if (group == null)
                throw new Exceptions.offeringNotFound();
            Offering offering = group.get(classCode);
            if (offering == null)
                throw new Exceptions.offeringNotFound();

            return offering;
        }
    }

    public static class StudentManager {

        public static HashMap<String, Student> allStudents = new HashMap<String, Student>();

        static String retrieveAllUrl = "http://138.197.181.131:5000/api/students";
        static String retrieveGradesUrl = "http://138.197.181.131:5000/api/grades";

        public static void updateFromExternalServer() throws Exception {
            HashMap<String, Object> webRes = Utils.sendRequest("GET", retrieveAllUrl, null, null);
            String data = (String) webRes.get("data");

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Student> list = mapper.readValue(data, new TypeReference<ArrayList<Student>>() {
            });

            allStudents.clear();
            for (Student s : list) {
                String id = s.getId();
                allStudents.put(id, s);
                ArrayList<Grade> grades = getAllGradesFromExternalServer(id);
                for (Grade g : grades) {
                    s.addGrade(g);
                }
            }
        }

        public static ArrayList<Student> getAll() throws IOException, InterruptedException {
            ArrayList<Student> list = new ArrayList<Student>(allStudents.values());
            return list;
        }

        public static Student get(String studentId) throws Exception {
            Student student = allStudents.get(studentId);
            if (student == null)
                throw new Exceptions.StudentNotFound();
            return student;
        }

        public static ArrayList<Grade> getAllGradesFromExternalServer(String studentId) throws Exception {

            Student student = allStudents.get(studentId);
            if (student == null)
                throw new Exceptions.StudentNotFound();

            String url = retrieveGradesUrl + "/" + studentId;
            HashMap<String, Object> webRes = Utils.sendRequest("GET", url, null, null);
            String data = (String) webRes.get("data");

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Grade> list = mapper.readValue(data, new TypeReference<ArrayList<Grade>>(){});

            return list;
        }
    }

}
