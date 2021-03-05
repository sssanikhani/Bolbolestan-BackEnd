import io.javalin.Javalin;

public class Server {

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
        Javalin app = Javalin.create().start(7000);
        app.get("/courses", Handlers.getCourses);
        app.get("/profile/:stdId",  Handlers.stdProfile);

    }
}