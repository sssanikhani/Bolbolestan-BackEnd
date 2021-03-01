import java.io.IOException;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {
    public Main() {
    }

    public static String deployCommand(String cmd) throws JsonProcessingException {
        String[] cmdParts = cmd.split("\\s+", 2);
        String result = "{\n\t";
        try {
            String data = CommandHandler.performCommand(cmdParts);
            if (!data.contains("{") && !data.contains("[")) {
                data = "\"" + data + "\"";
            }
            result += "\"success\" : true,\n\t";
            result += "\"data\" : " + data;
            result += "\n}";
        } catch (Exception e) {
            String error = e.getMessage();
            result += "\"success\" : false,\n\t";
            result += "\"error\" : \"" + error + "\"";
            result += "\n}";
        }
        return result;
    }

    public static void main(String[] args) throws IOException, ParseException {
        while (true) {
            String cmd = CommandHandler.readCommand();
            String[] cmdParts = cmd.split("\\s+", 2);
            if (cmdParts.length < 2) {
                System.out.println("Usage: <Command> <JSON Form Data>");
                continue;
            }
            String result = deployCommand(cmd);
            System.out.println(result);
        }
    }
}
