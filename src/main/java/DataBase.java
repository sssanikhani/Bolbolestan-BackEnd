import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataBase {

    public static class OfferingManager {

        private static HashMap<String, HashMap<String, Offering>> codeOfferingsMap = new HashMap<String, HashMap<String, Offering>>();

        static String retrieveAllUrl = "http://138.197.181.131:5000/api/courses";

        public static void updateFromExternalServer() throws IOException, InterruptedException {
            HashMap<String, Object> webRes = Utils.sendRequest("GET", retrieveAllUrl, null, null);
            String data = (String) webRes.get("data");

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Offering> list = mapper.readValue(data, new TypeReference<ArrayList<Offering>>() {
            });

            codeOfferingsMap.clear();
            for (Offering o : list) {
                String code = o.getCode();
                String classCode = o.getClassCode();
                if (codeOfferingsMap.get(code) == null)
                    codeOfferingsMap.put(code, new HashMap<String, Offering>());
                codeOfferingsMap.get(code).put(classCode, o);
            }
        }

        public static ArrayList<Offering> getAll() throws IOException, InterruptedException {
            ArrayList<Offering> list = new ArrayList<Offering>();
            for (HashMap<String, Offering> group : codeOfferingsMap.values()) {
                list.addAll(group.values());
            }
            return list;
        }

        public static Offering get(String code, String classCode) throws Exception {
            HashMap<String, Offering> group = codeOfferingsMap.get(code);
            if (group == null)
                throw new Exceptions.offeringNotFound();
            Offering offering = group.get(classCode);
            if (offering == null)
                throw new Exceptions.offeringNotFound();

            return offering;
        }
    }

    public static class StudentManager {

        private static HashMap<String, Student> students = new HashMap<String, Student>();

        static String retrieveAllUrl = "http://138.197.181.131:5000/api/students";
        static String retrieveGradesUrl = "http://138.197.181.131:5000/api/grades";

        public static void updateFromExternalServer() throws Exception {
            HashMap<String, Object> webRes = Utils.sendRequest("GET", retrieveAllUrl, null, null);
            String data = (String) webRes.get("data");

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Student> list = mapper.readValue(data, new TypeReference<ArrayList<Student>>() {
            });

            students.clear();
            for (Student s : list) {
                String id = s.getId();
                students.put(id, s);
                ArrayList<Grade> grades = getAllGradesFromExternalServer(id);
                for (Grade g : grades) {
                    s.addGrade(g);
                }
            }
        }

        public static ArrayList<Student> getAll() throws IOException, InterruptedException {
            ArrayList<Student> list = new ArrayList<Student>(students.values());
            return list;
        }

        public static Student get(String studentId) throws Exception {
            Student student = students.get(studentId);
            if (student == null)
                throw new Exceptions.StudentNotFound();
            return student;
        }

        public static ArrayList<Grade> getAllGradesFromExternalServer(String studentId) throws Exception {

            Student student = students.get(studentId);
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
