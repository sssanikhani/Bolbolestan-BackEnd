import java.util.ArrayList;

import io.javalin.http.Handler;

public class Handlers {

    public static Handler courses = ctx -> {
        String html = "";
        ctx.html(html);
    };

    public static Handler studentProfile = ctx -> {
        String html = "";
        ctx.html(html);
    };
    // app.get("/courses", Handlers.getCourses);
    // app.get("/profile/:studentId",  Handlers.stdProfile);
    // app.get("/course/:courseId/:classCode", Handlers.);
    // app.get("/change_plan/:studentId", Handlers.);
    // app.get("/plan/:studentId", Handlers.);
    // app.get("/submit/:studentId", Handlers.);
    // app.get("/submit_ok", Handlers.);
    // app.get("/submit_failed", Handlers.);

    public static Handler singleCourse = ctx -> {

    };

    public static Handler changePlan = ctx -> {

    };

    public static Handler plan = ctx -> {

    };

    public static Handler submit = ctx -> {

    };

    public static Handler okSubmit = ctx -> {

    };

    public static Handler failSubmit = ctx -> {

    };

}
