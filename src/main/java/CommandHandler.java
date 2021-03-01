import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandHandler {

    public static HashMap<String, Offering> allOfferings = new HashMap<String, Offering>();
    public static HashMap<String, ArrayList<Offering>> courseOfferingsMap = new HashMap<String, ArrayList<Offering>>();
    public static HashMap<String, Student> allStds = new HashMap<String, Student>();

    public static HashMap<String, Offering> getAllOfferings() {
        return allOfferings;
    }

    public static HashMap<String, ArrayList<Offering>> getCourseOfferingsMap() {
        return courseOfferingsMap;
    }

    public static HashMap<String, Student> getAllStds() {
        return allStds;
    }

    public CommandHandler() {
    }

    static String readCommand() {
        Scanner reader = new Scanner(System.in);
        return reader.nextLine();
    }

    static String performCommand(String[] cmdp) throws IOException, ParseException, Exception {
        switch (cmdp[0]) {
            case "addOffering":
                return addOffering(cmdp[1]);
            case "addStudent":
                return addStudent(cmdp[1]);
            case "getOffering":
                return getOffering(cmdp[1]);
            case "getOfferings":
                return getOfferings(cmdp[1]);
            case "addToWeeklySchedule":
                return addOfferingToSch(cmdp[1]);
            case "removeFromWeeklySchedule":
                return removeOfferingFromSch(cmdp[1]);
            case "getWeeklySchedule":
                return getWeeklySch(cmdp[1]);
            case "finalize":
                return finalize(cmdp[1]);
            default:
                throw new Exceptions.UnknownCommand(cmdp[0]);
        }
    }

    static String addOffering(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Offering newOffering = mapper.readValue(js, Offering.class);
        allOfferings.put(newOffering.getCode(), newOffering);
        if (courseOfferingsMap.get(newOffering.getName()) != null) {
            courseOfferingsMap.get(newOffering.getName()).add(newOffering);
        } else {
            courseOfferingsMap.put(newOffering.getName(), new ArrayList<Offering>());
            courseOfferingsMap.get(newOffering.getName()).add(newOffering);

        }
        return "offering added";
    }

    static String addStudent(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student newStd = mapper.readValue(js, Student.class);
        allStds.put(newStd.getStudentId(), newStd);
        return "student added";
    }

    static String addOfferingToSch(String js)
            throws IOException, Exceptions.StudentNotFound, Exceptions.offeringNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String offeringCode = jn.get("code").asText();

        if (allOfferings.get(offeringCode) != null) {
            if (allStds.get(stdId) != null) {
                allStds.get(stdId).addOfferingToList(allOfferings.get(offeringCode));
                return "offering added for student";
            } else {
                throw new Exceptions.StudentNotFound();
            }
        }
        throw new Exceptions.offeringNotFound();
    }

    static String removeOfferingFromSch(String js) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String offeringCode = jn.get("code").asText();

        if (allOfferings.get(offeringCode) != null) {
            if (allStds.get(stdId) != null) {
                if (allStds.get(stdId) != null) {
                    allStds.get(stdId).removeOfferingFromList(offeringCode);
                    return "offering removed for student";
                } else {
                    throw new Exceptions.StudentNotFound();
                }
            }
        }
        throw new Exceptions.offeringNotFound();
    }

    static String getWeeklySch(String js) throws JsonProcessingException, Exceptions.StudentNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        if (allStds.get(stdId) != null) {
            String message = mapper.writeValueAsString(allStds.get(stdId).getOfferingsData());
            return message;
        } else {
            throw new Exceptions.StudentNotFound();
        }

    }

    static String getOffering(String js) throws IOException, Exceptions.StudentNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String offeringCode = jn.get("code").asText();
        if (allStds.get(stdId) != null) {
            String message = "";
            if (allOfferings.get(offeringCode) != null) {
                message = mapper.writerWithView(View.normal.class).writeValueAsString(allOfferings.get(offeringCode));
            }
            return message;
        } else {
            throw new Exceptions.StudentNotFound();
        }
    }

    static String getOfferings(String js) throws IOException, Exceptions.StudentNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        if (allStds.get(stdId) != null) {
            String message = "";
            message = mapper.writerWithView(View.offerings.class).writeValueAsString(courseOfferingsMap.values());
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

        // Check Offering Time Collisions
        student.validateExamClassTimes();

        // Verify Enough Offering Capacities
        student.validateOfferingCapacities();

        student.finalizeOfferings();
        return "finalized successfully";
    }

}
