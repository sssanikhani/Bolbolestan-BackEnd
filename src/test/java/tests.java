import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class tests {

    Main mainTest = new Main();

    @Before
    public void setup() throws Exception {
        Scanner scanner = new Scanner(new File("src/test/resources/setupData.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
    }

    @After
    public void teardown() {
        CommandHandler.getAllStds().clear();
        CommandHandler.getAllOfferings().clear();
        CommandHandler.getCourseOfferingsMap().clear();
    }

    @Test
    public void testMinimumUnitsError() throws IOException {
        Scanner scanner = new Scanner(new File("src/test/resources/test1.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
        String cmd = "finalize {\"StudentId\": \"810197559\"}";
        String act = "{\n\t\"success\" : false,\n\t\"error\" : \"MinimumUnitsError\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

    @Test
    public void testMaximumUnitsError() throws IOException {
        Scanner scanner = new Scanner(new File("src/test/resources/test2.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
        String cmd = "finalize {\"StudentId\": \"810197559\"}";
        String act = "{\n\t\"success\" : false,\n\t\"error\" : \"MaximumUnitsError\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

    @Test
    public void testClassTimeCollisionError() throws IOException {
        Scanner scanner = new Scanner(new File("src/test/resources/test3.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
        String cmd = "finalize {\"StudentId\": \"810197559\"}";
        String act = "{\n\t\"success\" : false,\n\t\"error\" : \"ClassTimeCollisionError 810150101 810150100\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

    @Test
    public void testExamTimeCollisionError() throws IOException {
        Scanner scanner = new Scanner(new File("src/test/resources/test4.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
        String cmd = "finalize {\"StudentId\": \"810197559\"}";
        String act = "{\n\t\"success\" : false,\n\t\"error\" : \"ExamTimeCollisionError 810150103 810150102\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

    @Test
    public void testsimpleTrue() throws IOException {
        Scanner scanner = new Scanner(new File("src/test/resources/test6.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
        String cmd = "finalize {\"StudentId\": \"810197559\"}";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"finalized successfully\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }


    @Test
    public void testsimpleTrueTest_1() throws IOException {
        Scanner scanner = new Scanner(new File("src/test/resources/test7.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
        String cmd = "finalize {\"StudentId\": \"810197559\"}";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"finalized successfully\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

    @Test
    public void testCapacityError() throws IOException {
        Scanner scanner = new Scanner(new File("src/test/resources/test5.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
        String cmd = "finalize {\"StudentId\": \"810197562\"}";
        String act = "{\n\t\"success\" : false,\n\t\"error\" : \"CapacityError 810150108\"\n}";
        System.out.println(mainTest.parseCmd(cmd));
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

}
