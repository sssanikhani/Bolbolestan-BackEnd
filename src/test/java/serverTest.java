import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.javalin.Javalin;
import logic.DataBase;
import logic.Handlers;
import logic.Server;
import statics.Constants;
import utils.Utils;

public class serverTest {

    Javalin app;
    private static final String BASE_URL = "http://localhost:" + Constants.SERVER_PORT;

    @Before
    public void setup() {
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
        app.get(Server.COURSES_URL, ctx -> {
            Handlers.courses(ctx);
        });
        app.get(Server.STUDENT_PROFILE_URL_PREFIX + "/:studentId", ctx -> {
            Handlers.studentProfile(ctx);
        });
        app.get(Server.COURSE_URL_PREFIX + "/:code/:classCode", ctx -> {
            Handlers.singleCourse(ctx);
        });
        app.get(Server.CHANGE_PLAN_URL_PREFIX + "/:studentId", ctx -> {
            Handlers.changePlan(ctx);
        });
        app.get(Server.PLAN_URL_PREFIX + "/:studentId", ctx -> {
            Handlers.plan(ctx);
        });
        app.get(Server.SUBMIT_URL_PREFIX + "/:studentId", ctx -> {
            Handlers.submit(ctx);
        });
        app.get(Server.SUBMIT_OK_URL, ctx -> {
            Handlers.okSubmit(ctx);
        });
        app.get(Server.SUBMIT_FAILED_URL, ctx -> {
            Handlers.failSubmit(ctx);
        });

        // POST methods
        app.post(Server.ADD_COURSE_URL_PREFIX + "/:code/:classCode", ctx -> {
            Handlers.addCourse(ctx);
        });
        app.post(Server.REMOVE_COURSE_URL_PREFIX + "/:code/:classCode", ctx -> {
            Handlers.removeCourse(ctx);
        });
        app.post(Server.SUBMIT_PLAN_URL, ctx -> {
            Handlers.submitPlan(ctx);
        });
    }

    @After
    public void teardown() {
        app.stop();
    }

    @Test
    public void correctTest() throws IOException, InterruptedException {
        // Correct test
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test1.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String[] urlParts = { BASE_URL, "addCourse", params.get("code"), params.get("classCode") };
            String url = String.join("/", urlParts);
            Utils.sendRequest("POST", url, null, "studentId=810195115");
        }
        HashMap<String, Object> response = Utils.sendRequest("POST", "http://localhost:7000/submitPlan", null,
                "studentId=810195115");
        assertEquals("Optional[/submit_ok]", response.get("location"));
    }

    @Test
    public void correctAddAndRemoveCourseTest() throws IOException, InterruptedException {
        // Correct test add and remove
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test2.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String[] urlParts = { BASE_URL, "addCourse", params.get("code"), params.get("classCode") };
            String url = String.join("/", urlParts);
            Utils.sendRequest("POST", url, null, "studentId=810195115");
        }
        String url = "http://localhost:7000/removeCourse/8101014/01";
        HashMap<String, Object> response = Utils.sendRequest("POST", url, null, "studentId=810195115");
        assertEquals("Optional[/change_plan/810195115]", response.get("location"));
    }

    @Test
    public void minimumUnitsTest() throws IOException, InterruptedException {
        // 11 units
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test3.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String[] urlParts = { BASE_URL, "addCourse", params.get("code"), params.get("classCode") };
            String url = String.join("/", urlParts);
            Utils.sendRequest("POST", url, null, "studentId=810197220");
        }
        HashMap<String, Object> response = Utils.sendRequest("POST", "http://localhost:7000/submitPlan", null,
                "studentId=810197220");
        assertEquals("Optional[/submit_failed]", response.get("location"));
    }

    @Test
    public void maximumUnitsTest() throws IOException, InterruptedException {
        // 22 units
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test4.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String[] urlParts = { BASE_URL, "addCourse", params.get("code"), params.get("classCode") };
            String url = String.join("/", urlParts);
            Utils.sendRequest("POST", url, null, "studentId=810197227");
        }
        HashMap<String, Object> response = Utils.sendRequest("POST", "http://localhost:7000/submitPlan", null,
                "studentId=810197227");
        assertEquals("Optional[/submit_failed]", response.get("location"));
    }

    @Test
    public void notFoundCourse() throws IOException, InterruptedException {
        // failed 404 course not found
        String url = "http://localhost:7000/addCourse/8101152/01";
        HashMap<String, Object> response = Utils.sendRequest("POST", url, null, "studentId=810197227");
        Document reportDoc = Jsoup.parse(new File(Constants.PAGE_NOT_FOUND_PATH), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));
        assertEquals(res.select("title").text(), reportDoc.select("title").text());
        assertEquals(res.select("h1").text(), reportDoc.select("h1").text());
        assertEquals(res.select("br").text(), reportDoc.select("br").text());
    }

    @Test
    public void notFoundStudent() throws IOException, InterruptedException {
        // failed 404 student not found
        String url = "http://localhost:7000/addCourse/8101152/01";
        HashMap<String, Object> response = Utils.sendRequest("POST", url, null, "studentId=815367227");
        Document reportDoc = Jsoup.parse(new File(Constants.PAGE_NOT_FOUND_PATH), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));
        assertEquals(res.select("title").text(), reportDoc.select("title").text());
        assertEquals(res.select("h1").text(), reportDoc.select("h1").text());
        assertEquals(res.select("br").text(), reportDoc.select("br").text());
    }

    @Test
    public void planView() throws IOException, InterruptedException {
        // test plan
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test8.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String[] urlParts = { BASE_URL, "addCourse", params.get("code"), params.get("classCode") };
            String url = String.join("/", urlParts);
            Utils.sendRequest("POST", url, null, "studentId=810195115");
        }
        HashMap<String, Object> response = Utils.sendRequest("GET", "http://localhost:7000/plan/810195115", null, null);
        Document reportDoc = Jsoup.parse(new File("src/test/resources/serverTest/test8R.html"), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));

        assertEquals(res.select("title").text(), reportDoc.select("title").text());

        Element resSatElem = res.getElementById("Saturday");
        Element expSatElem = reportDoc.getElementById("Saturday");
        assertEquals(resSatElem.getElementById("9:00-10:30").text(), expSatElem.getElementById("9:00-10:30").text());
        assertEquals(resSatElem.getElementById("14:00-15:30").text(), expSatElem.getElementById("14:00-15:30").text());

        Element resSunElem = res.getElementById("Sunday");
        Element expSunElem = reportDoc.getElementById("Sunday");
        assertEquals(resSunElem.getElementById("7:30-9:00").text(), expSunElem.getElementById("7:30-9:00").text());
        assertEquals(resSunElem.getElementById("9:00-10:30").text(), expSunElem.getElementById("9:00-10:30").text());
        assertEquals(resSunElem.getElementById("10:30-12:00").text(), expSunElem.getElementById("10:30-12:00").text());

        Element resMonElem = res.getElementById("Monday");
        Element expMonElem = reportDoc.getElementById("Monday");
        assertEquals(resMonElem.getElementById("9:00-10:30").text(), expMonElem.getElementById("9:00-10:30").text());
        assertEquals(resMonElem.getElementById("14:00-15:30").text(), expMonElem.getElementById("14:00-15:30").text());

        Element resTueElem = res.getElementById("Tuesday");
        Element expTueElem = reportDoc.getElementById("Tuesday");
        assertEquals(resTueElem.getElementById("7:30-9:00").text(), expTueElem.getElementById("7:30-9:00").text());
        assertEquals(resTueElem.getElementById("9:00-10:30").text(), expTueElem.getElementById("9:00-10:30").text());
        assertEquals(resTueElem.getElementById("10:30-12:00").text(), expTueElem.getElementById("10:30-12:00").text());
    }

    @Test
    public void notPassedPrerequisites() throws IOException, InterruptedException {
        // prerequisites are not passed
        HashMap<String, Object> response = Utils.sendRequest("POST", "http://localhost:7000/addCourse/8101015/01", null,
                "studentId=810197227");
        Document res = Jsoup.parse((String) response.get("data"));
        assertEquals(res.select("body").text(), "403 class can not be added because prerequisites are not passed");
    }

    @Test
    public void classTimeCollision() throws IOException, InterruptedException {
        // class time collision
        Utils.sendRequest("POST", "http://localhost:7000/addCourse/8101015/01", null, "studentId=810195115");
        HashMap<String, Object> response = Utils.sendRequest("POST", "http://localhost:7000/addCourse/8101008/01", null,
                "studentId=810195115");
        Document res = Jsoup.parse((String) response.get("data"));
        assertEquals(res.select("body").text(), "403 class can not be added because of class time collision");
    }
}
