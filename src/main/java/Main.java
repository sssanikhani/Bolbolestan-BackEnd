import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;


public class Main {


    static String commandReader() {
        Scanner reader = new Scanner(System.in);
        return reader.nextLine();
    }
    static void commandHandler(String[] cmdp) throws IOException {
        switch (cmdp[0]) {
            case "addOffering" :
                Commands.addCourse(cmdp[1]);
                break;
            case "addStudent":
                Commands.addStudent(cmdp[1]);
                break;
            case "getOffering":
                Commands.getOffer(cmdp[1]);
                break;
            case "getOfferings":
                Commands.getOffers(cmdp[1]);
                break;
            case "addToWeeklySchedule":
                Commands.addCourseToSch(cmdp[1]);
                break;
            case "removeFromWeeklySchedule":
                Commands.removeCourseFromSch(cmdp[1]);
                break;
            case "getWeeklySchedule":
                // TODO
                break;
            case "finalize":
                // TODO
                break;
        }

    }

    public static void main(String[] args) throws IOException {
        while (true) {
            String cmd = commandReader();
            String[] cmdParts = cmd.split(" ", 2);
            commandHandler(cmdParts);
        }
    }
}
