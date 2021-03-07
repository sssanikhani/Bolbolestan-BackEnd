import java.util.HashMap;

public class Responses {
    
    public static HashMap<String, Object> CourseTimeCollision = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "CourseTimeCollision");
        put("message", "class can not be added because of class time collision");
    }};

    public static HashMap<String, Object> ExamTimeCollision = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "ExamTimeCollision");
        put("message", "class can not be added because of exam time collision");
    }};

    public static HashMap<String, Object> NotPassedPrerequisites = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "NotPassedPrerequisites");
        put("message", "class can not be added because prerequisites are not passed");
    }};

    public static HashMap<String, Object> BadRequest = new HashMap<String, Object>() {{
        put("status", 400);
        put("short", "BadRequest");
        put("message", "your request input is incorrect");
    }};

    public static HashMap<String, Object> ServerError = new HashMap<String, Object>() {{
        put("status", 500);
        put("short", "ServerError");
        put("message", "sorry! a server error occured");
    }};
}
