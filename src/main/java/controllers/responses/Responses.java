package controllers.responses;

import java.util.HashMap;

public class Responses {

	public static HashMap<String, Object> CourseTimeCollision = new HashMap<>() {
		{
			put("status", 403);
			put("short", "CourseTimeCollision");
			put("message", "class can not be added because of class time collision.");
		}
	};

	public static HashMap<String, Object> ExamTimeCollision = new HashMap<>() {
		{
			put("status", 403);
			put("short", "ExamTimeCollision");
			put("message", "class can not be added because of exam time collision.");
		}
	};

	public static HashMap<String, Object> OfferingNotFound = new HashMap<>() {
		{
			put("status", 404);
			put("short", "OfferingNotFound");
			put("message", "this offering not found.");
		}
	};

	public static HashMap<String, Object> StudentNotFound = new HashMap<>() {
		{
			put("status", 404);
			put("short", "StudentNotFound");
			put("message", "student not found.");
		}
	};

	public static HashMap<String, Object> OfferingCapacity = new HashMap<>() {
		{
			put("status", 403);
			put("short", "OfferingCapacity");
			put("message", "offering has no enough capacity");
		}
	};

	public static HashMap<String, Object> CoursePassedBefore = new HashMap<>() {
		{
			put("status", 403);
			put("short", "Course Passed Before");
			put("message", "you passed this course before");
		}
	};

	public static HashMap<String, Object> NotPassedPrerequisites = new HashMap<>() {
		{
			put("status", 403);
			put("short", "NotPassedPrerequisites");
			put("message", "class can not be added because prerequisites are not passed");
		}
	};

	public static HashMap<String, Object> MaxMinUnits = new HashMap<>() {
		{
			put("status", 403);
			put("short", "MaxMinUnits");
			put("message", "you get minimum/maximum units error");
		}
	};

	public static HashMap<String, Object> InternalServerError = new HashMap<>() {
		{
			put("status", 500);
			put("short", "Internal Server Error");
			put("message", "sorry! server encountered an error");
		}
	};

	public static HashMap<String, Object> UnAuthorized = new HashMap<>() {
		{
			put("status", 401);
			put("short", "Not Authorized");
			put("message", "you should login first");
		}
	};

	public static HashMap<String, Object> OK = new HashMap<>() {
		{
			put("status", 200);
			put("short", "OK");
			put("message", "operation was successful");
		}
	};
}