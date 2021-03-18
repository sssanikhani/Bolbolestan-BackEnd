package models.statics;

public class Exceptions extends Throwable {

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
