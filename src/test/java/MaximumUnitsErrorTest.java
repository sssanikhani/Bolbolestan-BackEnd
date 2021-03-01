import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class MaximumUnitsErrorTest {

    Main mainTest = new Main();

    @Before
    public void setup() throws Exception {
        Scanner scanner = new Scanner(new File("src/test/resources/test2.txt"));
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
    public void testMaximumUnitsError() throws IOException {
        String cmd = "finalize {\"StudentId\": \"810197559\"}";
        String act = "{\n\t\"success\" : false,\n\t\"error\" : \"MaximumUnitsError\"\n}";
        assertTrue(act.equals(mainTest.parseCmd(cmd)));
    }
}
