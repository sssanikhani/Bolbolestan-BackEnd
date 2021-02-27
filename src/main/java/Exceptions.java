import java.lang.Exception;

public class Exceptions {
    static class UnknownCommand extends Exception {
        public UnknownCommand(String cmd) {
            super("Unknown Command: '" + cmd + "'");
        }
    };

    static class MinimumUnits extends Exception {
        public MinimumUnits(String std_id) {
            super("Student with ID: " + std_id + "must have at least 12 units");
        }
    };

    static class MaximumUnits extends Exception {
        public MaximumUnits(String std_id) {
            super("Student with ID: " + std_id + "can not get more than 20 units");
        }
    };

    static class ClassTimeCollision extends Exception {
        public ClassTimeCollision(String c1, String c2) {
            super("Classes " + c1 + " and " + c2 + " have collision in time");
        }
    };

    static class ExamTimeCollision extends Exception {
        public ExamTimeCollision(String c1, String c2) {
            super("Exam of courses " + c1 + " and " + c2 + " have collision in time");
        }
    };

    static class OfferCapacity extends Exception {
        public OfferCapacity(String c) {
            super("Offer " + c + " Capacity is full");
        }
    };
}
