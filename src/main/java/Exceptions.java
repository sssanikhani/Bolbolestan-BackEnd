import java.lang.Exception;

public class Exceptions {
    static class UnknownCommand extends Exception {
        public UnknownCommand(String cmd) {
            super("Unknown Command: '" + cmd + "'");
        }
    };

    static class MinimumUnits extends Exception {
        public MinimumUnits() {
            super("MinimumUnitsError");
        }
    };

    static class MaximumUnits extends Exception {
        public MaximumUnits() {
            super("MaximumUnitsError");
        }
    };

    static class ClassTimeCollision extends Exception {
        public ClassTimeCollision(String c1, String c2) {
            super("ClassTimeCollision " + c1 + " " + c2);
        }
    };

    static class ExamTimeCollision extends Exception {
        public ExamTimeCollision(String c1, String c2) {
            super("ExamTimeCollision " + c1 + " " + c2);
        }
    };

    static class OfferCapacity extends Exception {
        public OfferCapacity(String c) {
            super("CapacityError " + c);
        }
    };
}
