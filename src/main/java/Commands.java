import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Commands {

    public static HashMap<String, Offer> allOffers = new HashMap<String, Offer>();
    public static HashMap<String, ArrayList<Offer>> allOffersOfACourse = new HashMap<String, ArrayList<Offer>>();
    public static HashMap<String, Student> allStds = new HashMap<String, Student>();

    public Commands() {
    }

    static String commandReader() {
        Scanner reader = new Scanner(System.in);
        return reader.nextLine();
    }
    void commandHandler(String[] cmdp) throws IOException, ParseException {
        switch (cmdp[0]) {
            case "addOffering":
                System.out.println(addCourse(cmdp[1]));
                break;
            case "addStudent":
                System.out.println(addStudent(cmdp[1]));
                break;
            case "getOffering":
                System.out.println(getOffer(cmdp[1]));
                break;
            case "getOfferings":
                System.out.println(getOffers(cmdp[1]));
                break;
            case "addToWeeklySchedule":
                System.out.println(addCourseToSch(cmdp[1]));
                break;
            case "removeFromWeeklySchedule":
                System.out.println(removeCourseFromSch(cmdp[1]));
                break;
            case "getWeeklySchedule":
                System.out.println(getWeeklySch(cmdp[1]));
                break;
            case "finalize":
                // TODO
                break;
        }
    }



    public static String createOutputJson(boolean b, String data, String s) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode message = mapper.createObjectNode();
        message.put("success", b);
        message.put(data, s);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
//        System.out.println(json);
        return json;
    }

     public static String  addCourse(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Offer newCourse = mapper.readValue(js, Offer.class);
        allOffers.put(newCourse.getCode(), newCourse);
        if(allOffersOfACourse.get(newCourse.getName()) != null) {
            allOffersOfACourse.get(newCourse.getName()).add(newCourse);
        } else  {
            allOffersOfACourse.put(newCourse.getName(), new ArrayList<Offer>());
            allOffersOfACourse.get(newCourse.getName()).add(newCourse);

        }
        return createOutputJson(true, "data", "OfferingAdded");
    }

    public static String addStudent(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student newStd = mapper.readValue(js, Student.class);
        allStds.put(newStd.getStudentId(), newStd);
        return createOutputJson(true, "data", "StudentAdded");
    }

    public static String addCourseToSch(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();

        if(allOffers.get(courseCode) != null) {
            if(allStds.get(stdId) != null) {
                allStds.get(stdId).addCourseToList(allOffers.get(courseCode));
                return createOutputJson(true, "data", "CourseAdded");
            }
        }
        return createOutputJson(false, "error", "OfferingNotFound");
    }

    public static String removeCourseFromSch(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();

        if(allOffers.get(courseCode) != null) {
            if(allStds.get(stdId) != null) {
                if ((allStds.get(stdId).removeCourseFromList(allOffers.get(courseCode))) != null) {
                    return createOutputJson(true, "data", "OfferingRemoved");
                }else {
                    return createOutputJson(false, "error", "OfferingNotFound");
                }
            }
        }
        return createOutputJson(false, "error", "OfferingNotFound");
    }

    public static String getWeeklySch(String js) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String message = "{\n\t\"success\": true,\n\t\"data\": {\"weeklySchdule\": ";
        message += mapper.writerWithView(View.weeklySch.class).writeValueAsString(allStds.get(stdId).getWeeklyCourses().values());
        message += "\n}";
        return message;
    }

    public static String getOffer(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();
        String message = "{\n\t\"success\": true,\n\t\"data\": ";
        if(allOffers.get(courseCode) != null) {
            message += mapper.writerWithView(View.normal.class).writeValueAsString(allOffers.get(courseCode));
            message += "\n}";
        }
        return message;
    }

    public static String getOffers(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String message = "{\n\t\"success\": true,\n\t\"data\": ";
        message += mapper.writerWithView(View.offerings.class).writeValueAsString(allOffersOfACourse.values());
        message += "\n}";
        return message;
    }



}
