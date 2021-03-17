package models.statics;

import java.util.HashMap;

public class Responses {

    public static HashMap<String, Object> CourseTimeCollision = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "CourseTimeCollision");
        put("message", "class can not be added because of class time collision.");
    }};

    public static HashMap<String, Object> OfferingNotFound = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "OfferingNotFound");
        put("message", "this offering not found.");
    }};

    public static HashMap<String, Object> StudentNotFound = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "StudentNotFound");
        put("message", "student not found.");
    }};

    public static HashMap<String, Object> TakePassedCourse = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "TakePassedCourse");
        put("message", "you passed this course before.");
    }};

    public static HashMap<String, Object> ExamTimeCollision = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "ExamTimeCollision");
        put("message", "class can not be added because of exam time collision.");
    }};

    public static HashMap<String, Object> NotPassedPrerequisites = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "NotPassedPrerequisites");
        put("message", "class can not be added because prerequisites are not passed.");
    }};

    public static HashMap<String, Object> UnitsError = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "UnitsError");
        put("message", "you get minimum/maximum get units error.");
    }};

    public static HashMap<String, Object> Error(String s)  {{
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("status", 403);
        response.put("message", s);
        return response;
    }};
}
