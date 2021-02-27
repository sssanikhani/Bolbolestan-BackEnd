import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CommandsRunTest {

    Commands commadsTester = new Commands();

    @Test
    public void testAddOffer() throws IOException {
        String cmd = "{\"code\": \"81015960\", \"name\": \"AI\" , \"instructor\" : \"Ramtin\", \"units\": 3, \"capacity\" : 60, \"prerequisites\": [\"Mabani\"], \"classTime\" : {\"days\": [\"Sat\", \"Mon\"], \"time\": \"16-17:30\"}, \"examTime\": {\"start\":  \"2021-9-01T08:00:00\", \"end\":  \"2021-9-01T08:00:00\"}}";
        assertEquals(commadsTester.addCourse(cmd), commadsTester.createOutputJson(true, "data", "OfferingAdded"));
    }

}
