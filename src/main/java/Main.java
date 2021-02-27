import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        while (true) {
            String cmd = CommandHandler.readCommand();
            String[] cmdParts = cmd.split("\\s+", 2);
            if (cmdParts.length < 2) {
                System.out.println("Usage: <Command> <JSON Form Data>");
                continue;
            }
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> res = new HashMap<String, Object>();
            try {
                String data = CommandHandler.performCommand(cmdParts);
                res.put("success", true);
                res.put("data", data);
            } catch (Exception e) {
                String error = e.getMessage();
                res.put("success", false);
                res.put("error", error);
            }
            String response = mapper.writeValueAsString(res);
            System.out.println(response);
        }
    }
}
