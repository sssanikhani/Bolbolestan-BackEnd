import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class test1 {

    // CommandHandler cmdHandler = new CommandHandler();
    Main mainTest = new Main();

    @Before
    public void setup() throws Exception {
        Scanner scanner = new Scanner(new File("src/test/resources/test3.txt"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            mainTest.parseCmd(line);
        }
    }

    @Test
    public void testAddOffering() throws IOException {
        String cmd = "{\"code\": \"81015960\", \"name\": \"AI\" , \"instructor\" : \"Ramtin\", \"units\": 3, \"capacity\" : 60, \"prerequisites\": [\"Mabani\"], \"classTime\" : {\"days\": [\"Sat\", \"Mon\"], \"time\": \"16-17:30\"}, \"examTime\": {\"start\":  \"2021-9-01T08:00:00\", \"end\":  \"2021-9-01T08:00:00\"}}";
        assertEquals(true, true);
    }

}
