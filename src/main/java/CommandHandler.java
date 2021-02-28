import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandHandler {

    public static HashMap<String, Offer> allOffers = new HashMap<String, Offer>();
    public static HashMap<String, ArrayList<Offer>> allOffersOfACourse = new HashMap<String, ArrayList<Offer>>();
    public static HashMap<String, Student> allStds = new HashMap<String, Student>();

    public CommandHandler() {
    }

    static String readCommand() {
        Scanner reader = new Scanner(System.in);
        return reader.nextLine();
    }

    static String performCommand(String[] cmdp) throws IOException, ParseException, Exception {
        switch (cmdp[0]) {
            case "addOffering":
                return addCourse(cmdp[1]);
            case "addStudent":
                return addStudent(cmdp[1]);
            case "getOffering":
                return getOffer(cmdp[1]);
            case "getOfferings":
                return getOffers(cmdp[1]);
            case "addToWeeklySchedule":
                return addCourseToSch(cmdp[1]);
            case "removeFromWeeklySchedule":
                return removeCourseFromSch(cmdp[1]);
            case "getWeeklySchedule":
                return getWeeklySch(cmdp[1]);
            case "finalize":
                return finalize(cmdp[1]);
            default:
                throw new Exceptions.UnknownCommand(cmdp[0]);
        }
    }
//
//    static String createOutputJson(boolean b, String data, String s) throws JsonProcessingException {
//        // ObjectMapper mapper = new ObjectMapper();
//        // ObjectNode message = mapper.createObjectNode();
//        // message.put(data, s);
//        // String json = mapper.writeValueAsString(message);
//        // System.out.println(json);
//        return s;
//    }

    static String addCourse(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Offer newOffer = mapper.readValue(js, Offer.class);
        allOffers.put(newOffer.getCode(), newOffer);
        if (allOffersOfACourse.get(newOffer.getName()) != null) {
            allOffersOfACourse.get(newOffer.getName()).add(newOffer);
        } else {
            allOffersOfACourse.put(newOffer.getName(), new ArrayList<Offer>());
            allOffersOfACourse.get(newOffer.getName()).add(newOffer);

        }
        return "OfferingAdded";
    }

    static String addStudent(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student newStd = mapper.readValue(js, Student.class);
        allStds.put(newStd.getStudentId(), newStd);
        return "StudentAdded";
    }

    static String addCourseToSch(String js) throws IOException, Exceptions.StudentNotFound, Exceptions.OfferingNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();

        if (allOffers.get(courseCode) != null) {
            if (allStds.get(stdId) != null) {
                allStds.get(stdId).addCourseToList(allOffers.get(courseCode));
                return "CourseAdded";
            } else {
                throw new Exceptions.StudentNotFound();
            }
        }
        throw new Exceptions.OfferingNotFound();
    }

    static String removeCourseFromSch(String js) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();

        if (allOffers.get(courseCode) != null) {
            if (allStds.get(stdId) != null) {
                if (allStds.get(stdId) != null) {
                    if ((allStds.get(stdId).removeCourseFromList(courseCode)) != null) {
                        return "OfferingRemoved";
                    } else {
                        throw new Exceptions.OfferingNotFound();
                    }
                } else {
                    throw new Exceptions.StudentNotFound();
                }
            }
        }
        throw new Exceptions.OfferingNotFound();
//        return createOutputJson(false, "error", "OfferingNotFound");
    }

    static String getWeeklySch(String js) throws JsonProcessingException, Exceptions.StudentNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        if (allStds.get(stdId) != null) {
            String message = "";
            message = mapper.writerWithView(View.weeklySch.class)
                    .writeValueAsString(allStds.get(stdId).getWeeklyCourses().values());
            return message;
        } else {
            throw new Exceptions.StudentNotFound();
        }

    }

    static String getOffer(String js) throws IOException, Exceptions.StudentNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String courseCode = jn.get("code").asText();
        if (allStds.get(stdId) != null) {
            String message = "";
            if (allOffers.get(courseCode) != null) {
                message = mapper.writerWithView(View.normal.class).writeValueAsString(allOffers.get(courseCode));
//                message += "\n}";
            }
            return message;
        } else {
            throw new Exceptions.StudentNotFound();
        }
    }

    static String getOffers(String js) throws IOException, Exceptions.StudentNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        if (allStds.get(stdId) != null) {
            String message = "";
            message = mapper.writerWithView(View.offerings.class).writeValueAsString(allOffersOfACourse.values());
//            message += "\n}";
            return message;
        } else {
            throw new Exceptions.StudentNotFound();
        }
    }

    static String finalize(String js) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        Student student = allStds.get(stdId);
        if (student == null) {
            throw new Exceptions.StudentNotFound();
        }

        // Validate Number of Units
        if (student.getNumberChosenUnits() < Constants.MIN_ALLOWED_UNITS) {
            throw new Exceptions.MinimumUnits();
        }
        if (student.getNumberChosenUnits() > Constants.MAX_ALLOWED_UNITS) {
            throw new Exceptions.MaximumUnits();
        }

        // Validate CourseTime Collision
        student.validateCourseTimeCollision();

        // Validate ExamTime Collision
        student.validateExamTimeCollision();

        // Check Offer Capacity
        student.validateOfferCapacities();

        return "finalized successfully";
    }

}
