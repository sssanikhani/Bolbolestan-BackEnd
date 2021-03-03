import io.javalin.Javalin;

public class server {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.get("/courses", Handlers.getCourses);
        app.get("/profile/:stdId",  Handlers.stdProfile);

    }
}