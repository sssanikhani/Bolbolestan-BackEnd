import java.util.HashMap;

public class Responses {
    
    public static HashMap<String, Object> CourseTimeCollision = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "CourseTimeCollision");
        put("message", "class can not be added because of class collision time");
    }};

    public static HashMap<String, Object> ExamTimeCollision = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "ExamTimeCollision");
        put("message", "class can not be added because of exam time collsion");
    }};

    public static HashMap<String, Object> NotPassedPrerequisites = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "NotPassedPrerequisites");
        put("message", "class can not be added because prerequisites are not passed");
    }};

    public static HashMap<String, Object> BadRequest = new HashMap<String, Object>() {{
        put("status", 400);
        put("short", "BadRequest");
        put("message", "your input is incorrect");
    }};

    public static HashMap<String, Object> ServerError = new HashMap<String, Object>() {{
        put("status", 500);
        put("short", "ServerError");
        put("message", "sorry! a server error occured");
    }};

    public static HashMap<String, Object> SubmitOk = new HashMap<String, Object>() {{
        put("status", 200);
        put("short", "SubmitOk");
        put("message", "submit ok");
    }};

    public static HashMap<String, Object> SubmitFailed = new HashMap<String, Object>() {{
        put("status", 403);
        put("short", "SubmitFailed");
        put("message", "submit failed");
    }};
}
