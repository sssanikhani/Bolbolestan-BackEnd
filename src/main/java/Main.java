import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Commands command = new Commands();
        while (true) {
            String cmd = command.commandReader();
            String[] cmdParts = cmd.split("\\s+");
            command.commandHandler(cmdParts);
        }
    }
}
