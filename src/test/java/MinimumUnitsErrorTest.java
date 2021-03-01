import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MinimumUnitsErrorTest {

//    CommandHandler cmdHandler = new CommandHandler();
    Main mainTest = new Main();

    @Before
    public void setup() throws Exception {
        Scanner scanner = new Scanner(new File("src/test/resources/test1.txt"));
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
        String cmd = "finalize {\"StudentId\": \"810197559\"}";
        String act = "{\n\t\"success\" : false,\n\t\"error\" : \"MinimumUnitsError\"\n}";
        assertEquals(act.equals(mainTest.parseCmd(cmd)), true);
    }


}
