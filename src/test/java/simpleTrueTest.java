import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class simpleTrueTest {
    Main mainTest = new Main();

    @Before
    public void setup() throws Exception {
        Scanner scanner = new Scanner(new File("src/test/resources/test7.txt"));
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
    public void testsimpleTrue() throws IOException {
        String cmd = "finalize {\"StudentId\": \"810197559\"}";
        String act = "{\n\t\"success\" : true,\n\t\"data\" : \"finalized successfully\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }

}

