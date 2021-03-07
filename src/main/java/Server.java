import io.javalin.Javalin;

public class Server {

    private static Javalin app;

    public static final String COURSES_URL = "/courses";
    public static final String STUDENT_PROFILE_URL_PREFIX = "/profile";
    public static final String COURSE_URL_PREFIX = "/course";
    public static final String CHANGE_PLAN_URL_PREFIX = "/change_plan";
    public static final String PLAN_URL_PREFIX = "/plan";
    public static final String SUBMIT_URL_PREFIX = "/submit";
    public static final String SUBMIT_OK_URL = "/submit_ok";
    public static final String SUBMIT_FAILED_URL = "/submit_failed";
    public static final String ADD_COURSE_URL_PREFIX = "/addCourse";
    public static final String REMOVE_COURSE_URL_PREFIX = "/removeCourse";
    public static final String SUBMIT_PLAN_URL = "/submitPlan";

    public static void main(String[] args) {
        System.out.println("Server Started Running...");
        try {
            System.out.println("Trying to retrieve data from external DataBase...");
            DataBase.OfferingManager.updateFromExternalServer();
            DataBase.StudentManager.updateFromExternalServer();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: failed to connect with external server");
            return;
        }

        app = Javalin.create().start(Constants.SERVER_PORT);
        addUrls();
    }

    public static void addUrls() {
        // GET methods
        app.get(COURSES_URL, ctx -> {Handlers.courses(ctx);});
        app.get(STUDENT_PROFILE_URL_PREFIX + "/:studentId", ctx -> {Handlers.studentProfile(ctx);});
        app.get(COURSE_URL_PREFIX + "/:code/:classCode", ctx -> {Handlers.singleCourse(ctx);});
        app.get(CHANGE_PLAN_URL_PREFIX + "/:studentId", ctx -> {Handlers.changePlan(ctx);});
        app.get(PLAN_URL_PREFIX + "/:studentId", ctx -> {Handlers.plan(ctx);});
        app.get(SUBMIT_URL_PREFIX + "/:studentId", ctx -> {Handlers.submit(ctx);});
        app.get(SUBMIT_OK_URL, ctx -> {Handlers.okSubmit(ctx);});
        app.get(SUBMIT_FAILED_URL, ctx -> {Handlers.failSubmit(ctx);});

        // POST methods
        app.post(ADD_COURSE_URL_PREFIX + "/:code/:classCode", ctx -> {Handlers.addCourse(ctx);});
        app.post(REMOVE_COURSE_URL_PREFIX + "/:code/:classCode", ctx -> {Handlers.removeCourse(ctx);});
        app.post(SUBMIT_PLAN_URL, ctx -> {Handlers.submitPlan(ctx);});
    }
}

