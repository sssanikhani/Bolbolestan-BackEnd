import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;


public class Main {

    private static ArrayList<Course> allCourses = new ArrayList<Course>();
    private static ArrayList<Student> allStds = new ArrayList<Student>();

    static String commandReader() {
        Scanner reader = new Scanner(System.in);
        return reader.nextLine();
    }
    static void commandHandler(String[] cmdp) throws IOException {
        switch (cmdp[0]) {
            case "addOffering" :
                allCourses.add(Commands.addCourse(cmdp[1]));
                // TODO
                break;
            case "addStudent":
                allStds.add(Commands.addStudent(cmdp[1]));
                System.out.println(allStds.get(0).getStudentId());
                // TODO
                break;
            case "getOffering":
                // TODO
                break;
            case "addToWeeklySchedule":
                // TODO
                break;
            case "removeFromWeeklySchedule":
                // TODO
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
        String cmd = commandReader();
        String[] cmdParts = cmd.split(" ", 2);
        commandHandler(cmdParts);
    }
}
