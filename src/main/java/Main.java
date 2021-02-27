import java.io.IOException;
import java.text.ParseException;


public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        while (true) {
            String cmd = CommandHandler.readCommand();
            String[] cmdParts = cmd.split("\\s+");
            CommandHandler.performCommand(cmdParts);
        }
    }
}
