import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Main {
    public Main() {
    }

    public static void parseCmd(String cmd) throws JsonProcessingException {
        String[] cmdParts = cmd.split("\\s+", 2);
        ObjectMapper mapper = new ObjectMapper();
//        HashMap<String, Object> res = new HashMap<String, Object>();
//         ObjectMapper mapper = new ObjectMapper();
//         ObjectNode res = mapper.createObjectNode();
        String result = "{\n\t";
        try {
            String data = CommandHandler.performCommand(cmdParts);
            result += "\"success\" : true\n\t";
            result += "\"data\" : " + data ;
            result += "\n}";
//            res.put("data", data);
        } catch (Exception e) {
            String error = e.getMessage();
            result += "\"success\" : false\n\t";
            result += "\"error\" : " + error ;
            result += "\n}";
//            res.put("success", false);
//            res.put("error", error);
        }
//        String response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(res);
        System.out.println(result);
    }


    public static void main(String[] args) throws IOException, ParseException {
        while (true) {
            String cmd = CommandHandler.readCommand();
            String[] cmdParts = cmd.split("\\s+", 2);
            if (cmdParts.length < 2) {
                System.out.println("Usage: <Command> <JSON Form Data>");
                continue;
            }
            parseCmd(cmd);
        }
    }
}
