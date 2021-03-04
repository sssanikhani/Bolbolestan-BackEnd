import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataBase {

    public static class OfferingManager {
        static String retrieveAllUrl = "http://138.197.181.131:5000/api/courses";

        public static ArrayList<Offering> getAllFromWebServer() throws IOException, InterruptedException {
            HashMap<String, Object> webRes = Utils.sendRequest("GET", retrieveAllUrl, null, null);
            String data = (String) webRes.get("data");

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Offering> list = mapper.readValue(data, new TypeReference<ArrayList<Offering>>() {
            });

            return list;
        }
    }

    public static class StudentManager {
        static String retrieveAllUrl = "http://138.197.181.131:5000/api/students";
        static String retrieveGradesUrl = "http://138.197.181.131:5000/api/grades";

        public static ArrayList<Student> getAllFromWebServer(String url) throws IOException, InterruptedException {
            HashMap<String, Object> webRes = Utils.sendRequest("GET", retrieveAllUrl, null, null);
            String data = (String) webRes.get("data");

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Student> list = mapper.readValue(data, new TypeReference<ArrayList<Student>>() {
            });

            return list;
        }

        public static ArrayList<HashMap<String, Object>> getAllGradesFromWebServer(String studentId) throws IOException, InterruptedException {

            String url = retrieveGradesUrl + "/" + studentId;
            HashMap<String, Object> webRes = Utils.sendRequest("GET", url, null, null);
            String data = (String) webRes.get("data");

            ObjectMapper mapper = new ObjectMapper();
            ArrayList<HashMap<String,Object>> list = mapper.readValue(data, new TypeReference<ArrayList<HashMap<String,Object>>>(){});

            return list;
        }
    }

}
