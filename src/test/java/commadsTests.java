import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;
public class commadsTests {
    Main mainTest = new Main();

    @Before
    public void setup() throws JsonProcessingException, FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/test/resources/commandsTestInit.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
    }

    @Test
    public void testAddOffering() throws JsonProcessingException {
        String cmd = "addOffering {\"code\": \"810150100\", \"name\": \"Course1\" , \"instructor\" : \"tea1\", \"units\": 3, \"capacity\" : 60, \"prerequisites\": [\"AP\"], \"classTime\" : {\"days\": [\"Sat\", \"Mon\"], \"time\": \"7:30-9\"}, \"examTime\": {\"start\":  \"2022-9-01T08:00:00\", \"end\":  \"2022-9-01T11:00:00\"}}\n";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"offering added\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

    @Test
    public void testAddStudent() throws JsonProcessingException {
        String cmd = "addStudent {\"studentId\": \"810197560\", \"name\": \"Sani\", \"enteredAt\": \"1397\"}";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"student added\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

    @Test
    public void testAddOfferingToSch() throws JsonProcessingException {
        String cmd = "addToWeeklySchedule {\"StudentId\": \"810197559\", \"code\" : \"810150109\"}";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"offering added for student\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

    @Test
    public void testRemoveOfferingFromSch() throws JsonProcessingException {
        String cmd = "removeFromWeeklySchedule {\"StudentId\": \"810197559\", \"code\" : \"810150111\"}\n";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"offering removed for student\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }


    @Test
    public void testGetWeeklySch() throws JsonProcessingException {
        String cmd = "getWeeklySchedule {\"StudentId\": \"810197559\"}\n";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"[{\"code\":\"810150111\",\"name\":\"Course11\",\"instructor\":\"tea12\",\"classTime\":{\"days\":[\"Sun\",\"Tue\"],\"time\":\"18-19:30\"},\"examTime\":{\"start\":\"2022-9-12T08:00:00\",\"end\":\"2022-9-12T11:00:00\"},\"status\":\"non-finalize\",\"numRegisteredStudents\":0}]\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }


    @Test
    public void testGetOffering() throws JsonProcessingException {
        String cmd = "getOffering {\"StudentId\": \"810197559\", \"code\" : \"810150111\"}\n";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"{\"code\":\"810150111\",\"name\":\"Course11\",\"instructor\":\"tea12\",\"units\":3,\"classTime\":{\"days\":[\"Sun\",\"Tue\"],\"time\":\"18-19:30\"},\"examTime\":{\"start\":\"2022-9-12T08:00:00\",\"end\":\"2022-9-12T11:00:00\"},\"capacity\":60,\"prerequisites\":[\"MABANI\"],\"numRegisteredStudents\":0}\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }
    @Test
    public void testGetOfferings() throws JsonProcessingException {
        String cmd = "getOfferings {\"StudentId\": \"810197559\"}\n";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"[[{\"code\":\"810150110\",\"name\":\"Course11\",\"instructor\":\"tea11\",\"numRegisteredStudents\":0},{\"code\":\"810150111\",\"name\":\"Course11\",\"instructor\":\"tea12\",\"numRegisteredStudents\":0}],[{\"code\":\"810150109\",\"name\":\"Course10\",\"instructor\":\"tea10\",\"numRegisteredStudents\":0}]]\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }
}
