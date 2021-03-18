package models.statics;

public class Exceptions extends Throwable {

	public static class UnknownCommand extends Exception {

		public UnknownCommand(String cmd) {
			super("Unknown Command: '" + cmd + "'");
		}
	}

	public static class MinimumUnits extends Exception {

		public MinimumUnits() {
			super("MinimumUnitsError");
		}
	}

	public static class MaximumUnits extends Exception {

		public MaximumUnits() {
			super("MaximumUnitsError");
		}
	}

	public static class ClassTimeCollision extends Exception {

		public ClassTimeCollision(String c1, String c2) {
			super("ClassTimeCollisionError " + c1 + " " + c2);
		}
	}

	public static class ExamTimeCollision extends Exception {

		public ExamTimeCollision(String c1, String c2) {
			super("ExamTimeCollisionError " + c1 + " " + c2);
		}
	}

	public static class OfferingCapacity extends Exception {

		public OfferingCapacity(String c) {
			super("CapacityError " + c);
		}
	}

	public static class StudentNotFound extends Exception {

		public StudentNotFound() {
			super("StudentNotFound");
		}
	}

	public static class offeringNotFound extends Exception {

		public offeringNotFound() {
			super("OfferingNotFound");
		}
	}
}
