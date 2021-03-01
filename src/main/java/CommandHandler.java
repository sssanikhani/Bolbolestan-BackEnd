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
    public static HashMap<String, ArrayList<Offer>> courseOffersMap = new HashMap<String, ArrayList<Offer>>();
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
                return addOffer(cmdp[1]);
            case "addStudent":
                return addStudent(cmdp[1]);
            case "getOffering":
                return getOffer(cmdp[1]);
            case "getOfferings":
                return getOffers(cmdp[1]);
            case "addToWeeklySchedule":
                return addOfferToSch(cmdp[1]);
            case "removeFromWeeklySchedule":
                return removeOfferFromSch(cmdp[1]);
            case "getWeeklySchedule":
                return getWeeklySch(cmdp[1]);
            case "finalize":
                return finalize(cmdp[1]);
            default:
                throw new Exceptions.UnknownCommand(cmdp[0]);
        }
    }
    //
    // static String createOutputJson(boolean b, String data, String s) throws
    // JsonProcessingException {
    // // ObjectMapper mapper = new ObjectMapper();
    // // ObjectNode message = mapper.createObjectNode();
    // // message.put(data, s);
    // // String json = mapper.writeValueAsString(message);
    // // System.out.println(json);
    // return s;
    // }

    static String addOffer(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Offer newOffer = mapper.readValue(js, Offer.class);
        allOffers.put(newOffer.getCode(), newOffer);
        if (courseOffersMap.get(newOffer.getName()) != null) {
            courseOffersMap.get(newOffer.getName()).add(newOffer);
        } else {
            courseOffersMap.put(newOffer.getName(), new ArrayList<Offer>());
            courseOffersMap.get(newOffer.getName()).add(newOffer);

        }
        return "offer added";
    }

    static String addStudent(String js) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student newStd = mapper.readValue(js, Student.class);
        allStds.put(newStd.getStudentId(), newStd);
        return "student added";
    }

    static String addOfferToSch(String js) throws IOException, Exceptions.StudentNotFound, Exceptions.OfferingNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String offerCode = jn.get("code").asText();

        if (allOffers.get(offerCode) != null) {
            if (allStds.get(stdId) != null) {
                allStds.get(stdId).addOfferToList(allOffers.get(offerCode));
                return "offer added for student";
            } else {
                throw new Exceptions.StudentNotFound();
            }
        }
        throw new Exceptions.OfferingNotFound();
    }

    static String removeOfferFromSch(String js) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String offerCode = jn.get("code").asText();

        if (allOffers.get(offerCode) != null) {
            if (allStds.get(stdId) != null) {
                if (allStds.get(stdId) != null) {
                    if ((allStds.get(stdId).removeOfferFromList(offerCode)) != null) {
                        return "offering removed for student";
                    } else {
                        throw new Exceptions.OfferingNotFound();
                    }
                } else {
                    throw new Exceptions.StudentNotFound();
                }
            }
        }
        throw new Exceptions.OfferingNotFound();
        // return createOutputJson(false, "error", "OfferingNotFound");
    }

    static String getWeeklySch(String js) throws JsonProcessingException, Exceptions.StudentNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        if (allStds.get(stdId) != null) {
            String message = "";
            message = mapper.writerWithView(View.weeklySch.class)
                    .writeValueAsString(allStds.get(stdId).getOffers().values());
            return message;
        } else {
            throw new Exceptions.StudentNotFound();
        }

    }

    static String getOffer(String js) throws IOException, Exceptions.StudentNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = mapper.readTree(js);
        String stdId = jn.get("StudentId").asText();
        String offerCode = jn.get("code").asText();
        if (allStds.get(stdId) != null) {
            String message = "";
            if (allOffers.get(offerCode) != null) {
                message = mapper.writerWithView(View.normal.class).writeValueAsString(allOffers.get(offerCode));
                // message += "\n}";
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
            message = mapper.writerWithView(View.offerings.class).writeValueAsString(courseOffersMap.values());
            // message += "\n}";
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

        // Validate Offer Times Collision
        student.validateExamClassTimes();

        // Check Offer Capacity
        student.validateOfferCapacities();

        return "finalized successfully";
    }

}
