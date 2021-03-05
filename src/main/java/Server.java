import io.javalin.Javalin;

public class Server {

    private static Javalin app;
    public static void main(String[] args) {
        System.out.println("Server Started Running...");
        try {
            System.out.println("Trying to retrieve data from external DataBase...");
            DataBase.OfferingManager.updateFromExternalServer();
            DataBase.StudentManager.updateFromExternalServer();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: failed to connect with external server");
        }
        
        app = Javalin.create().start(Constants.SERVER_PORT);
        addUrls();
    }

    public static void addUrls() {
        app.get("/courses", Handlers.courses);
        app.get("/profile/:studentId",  Handlers.studentProfile);
        app.get("/course/:courseId/:classCode", Handlers.singleCourse);
        app.get("/change_plan/:studentId", Handlers.changePlan);
        app.get("/plan/:studentId", Handlers.plan);
        app.get("/submit/:studentId", Handlers.submit);
        app.get("/submit_ok", Handlers.okSubmit);
        app.get("/submit_failed", Handlers.failSubmit);
    }
}