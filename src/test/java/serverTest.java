import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class serverTest {

    Javalin app;

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
        app.get(Server.COURSES_URL, ctx -> {Handlers.courses(ctx);});
        app.get(Server.STUDENT_PROFILE_URL_PREFIX + "/:studentId", ctx -> {Handlers.studentProfile(ctx);});
        app.get(Server.COURSE_URL_PREFIX + "/:code/:classCode", ctx -> {Handlers.singleCourse(ctx);});
        app.get(Server.CHANGE_PLAN_URL_PREFIX + "/:studentId", ctx -> {Handlers.changePlan(ctx);});
        app.get(Server.PLAN_URL_PREFIX + "/:studentId", ctx -> {Handlers.plan(ctx);});
        app.get(Server.SUBMIT_URL_PREFIX + "/:studentId", ctx -> {Handlers.submit(ctx);});
        app.get(Server.SUBMIT_OK_URL, ctx -> {Handlers.okSubmit(ctx);});
        app.get(Server.SUBMIT_FAILED_URL, ctx -> {Handlers.failSubmit(ctx);});

        // POST methods
        app.post(Server.ADD_COURSE_URL_PREFIX + "/:code/:classCode", ctx -> {Handlers.addCourse(ctx);});
        app.post(Server.REMOVE_COURSE_URL_PREFIX + "/:code/:classCode", ctx -> {Handlers.removeCourse(ctx);});
        app.post(Server.SUBMIT_PLAN_URL, ctx -> {Handlers.submitPlan(ctx);});
    }

    @After
    public void teardown() {
        app.stop();
    }
    @Test
    public void Test1() throws IOException, InterruptedException {
        //True test
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test1.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String url = "http://localhost:7000/addCourse/" + params.get("code") + "/" + params.get("classCode");
            Utils.sendRequest("POST", url, null, "studentId=810195115");
        }
        HashMap<String, Object> response =
                Utils.sendRequest("POST", "http://localhost:7000/submitPlan",
                                    null, "studentId=810195115");
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit_ok.html"), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));
        assertTrue(res.select("title").text().equals(reportDoc.select("title").text()));
        assertTrue(res.select("body").text().equals(reportDoc.select("body").text()));
    }

    @Test
    public void Test2() throws IOException, InterruptedException {
        //True test add and remove
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test2.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String url = "http://localhost:7000/addCourse/" + params.get("code") + "/" + params.get("classCode");
            Utils.sendRequest("POST", url,
                                null, "studentId=810195115");
        }
        String url = "http://localhost:7000/removeCourse/8101014/01";
        Utils.sendRequest("POST", url, null, "studentId=810195115");
        HashMap<String, Object> response =
                Utils.sendRequest("POST", "http://localhost:7000/submitPlan", null, "studentId=810195115");
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit_ok.html"), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));
        assertTrue(res.select("title").text().equals(reportDoc.select("title").text()));
        assertTrue(res.select("body").text().equals(reportDoc.select("body").text()));
    }

    @Test
    public void Test3() throws IOException, InterruptedException {
        //failed with 13 units
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test3.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String url = "http://localhost:7000/addCourse/" + params.get("code") + "/" + params.get("classCode");
            Utils.sendRequest("POST", url,
                                null, "studentId=810197220");
        }
        HashMap<String, Object> response =
                Utils.sendRequest("POST", "http://localhost:7000/submitPlan",
                                    null, "studentId=810197220");
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit_failed.html"), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));
        assertTrue(res.select("title").text().equals(reportDoc.select("title").text()));
        assertTrue(res.select("body").text().equals(reportDoc.select("body").text()));
    }

    @Test
    public void Test4() throws IOException, InterruptedException {
        //failed with 22 units
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test4.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String url = "http://localhost:7000/addCourse/" + params.get("code") + "/" + params.get("classCode");
            Utils.sendRequest("POST", url,
                                    null, "studentId=810197227");
        }
        HashMap<String, Object> response =
                Utils.sendRequest("POST", "http://localhost:7000/submitPlan",
                                    null, "studentId=810197227");
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit_failed.html"), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));
        assertTrue(res.select("title").text().equals(reportDoc.select("title").text()));
        assertTrue(res.select("body").text().equals(reportDoc.select("body").text()));
    }

    @Test
    public void Test5() throws IOException, InterruptedException {
        //true test with conflict 15/08
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test5.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String url = "http://localhost:7000/addCourse/" + params.get("code") + "/" + params.get("classCode");
            Utils.sendRequest("POST", url,
                                null, "studentId=810197227");
        }
        HashMap<String, Object> response =
                Utils.sendRequest("POST", "http://localhost:7000/submitPlan",
                                    null, "studentId=810197227");
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit_ok.html"), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));
        assertTrue(res.select("title").text().equals(reportDoc.select("title").text()));
        assertTrue(res.select("body").text().equals(reportDoc.select("body").text()));
    }

    @Test
    public void Test6() throws IOException, InterruptedException {
        //failed 404 course not found
        String url = "http://localhost:7000/addCourse/8101152/01";
        HashMap<String, Object> response =
                Utils.sendRequest("POST", url,
                                    null, "studentId=810197227");
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/404.html"), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));
        assertTrue(res.select("title").text().equals(reportDoc.select("title").text()));
        assertTrue(res.select("h1").text().equals(reportDoc.select("h1").text()));
        assertTrue(res.select("br").text().equals(reportDoc.select("br").text()));
    }

    @Test
    public void Test7() throws IOException, InterruptedException {
        //failed 404 student not found
        String url = "http://localhost:7000/addCourse/8101152/01";
        HashMap<String, Object> response =
                Utils.sendRequest("POST", url,
                                        null, "studentId=815367227");
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/404.html"), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));
        assertTrue(res.select("title").text().equals(reportDoc.select("title").text()));
        assertTrue(res.select("h1").text().equals(reportDoc.select("h1").text()));
        assertTrue(res.select("br").text().equals(reportDoc.select("br").text()));
    }

    @Test
    public void Test8() throws IOException, InterruptedException {
        //test plan
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test8.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, String> params = mapper.readValue(line, HashMap.class);
            String url = "http://localhost:7000/addCourse/" + params.get("code") + "/" + params.get("classCode");
            Utils.sendRequest("POST", url,
                                null, "studentId=810195115");
        }
        HashMap<String, Object> response =
                Utils.sendRequest("GET", "http://localhost:7000/plan/810195115",
                                    null, null);
        Document reportDoc = Jsoup.parse(new File("src/test/resources/serverTest/test8R.html"), "UTF-8");
        Document res = Jsoup.parse((String) response.get("data"));

        assertTrue(res.select("title").text().equals(reportDoc.select("title").text()));
        assertTrue(res.getElementById("Saturday").getElementById("9:00-10:30").text().
                        equals(reportDoc.getElementById("Saturday").getElementById("9:00-10:30").text()));
        assertTrue(res.getElementById("Saturday").getElementById("14:00-15:30").text().
                equals(reportDoc.getElementById("Saturday").getElementById("14:00-15:30").text()));

        assertTrue(res.getElementById("Sunday").getElementById("7:30-9:00").text().
                equals(reportDoc.getElementById("Sunday").getElementById("7:30-9:00").text()));
        assertTrue(res.getElementById("Sunday").getElementById("9:00-10:30").text().
                equals(reportDoc.getElementById("Sunday").getElementById("9:00-10:30").text()));
        assertTrue(res.getElementById("Sunday").getElementById("10:30-12:00").text().
                equals(reportDoc.getElementById("Sunday").getElementById("10:30-12:00").text()));

        assertTrue(res.getElementById("Monday").getElementById("9:00-10:30").text().
                equals(reportDoc.getElementById("Monday").getElementById("9:00-10:30").text()));
        assertTrue(res.getElementById("Monday").getElementById("14:00-15:30").text().
                equals(reportDoc.getElementById("Monday").getElementById("14:00-15:30").text()));

        assertTrue(res.getElementById("Tuesday").getElementById("7:30-9:00").text().
                equals(reportDoc.getElementById("Tuesday").getElementById("7:30-9:00").text()));
        assertTrue(res.getElementById("Tuesday").getElementById("9:00-10:30").text().
                equals(reportDoc.getElementById("Tuesday").getElementById("9:00-10:30").text()));
        assertTrue(res.getElementById("Tuesday").getElementById("10:30-12:00").text().
                equals(reportDoc.getElementById("Tuesday").getElementById("10:30-12:00").text()));

    }

    @Test
    public void Test9() throws IOException, InterruptedException {
        //prerequisites are not passed
        HashMap<String, Object> response =
                Utils.sendRequest("POST", "http://localhost:7000/addCourse/8101015/01",
                                    null, "studentId=810197227");
        Document res = Jsoup.parse((String) response.get("data"));
        assertTrue(res.select("body").text().equals("403 class can not be added because prerequisites are not passed"));
    }

    @Test
    public void Test10() throws IOException, InterruptedException {
        //class time collision
        Utils.sendRequest("POST", "http://localhost:7000/addCourse/8101015/01",
                            null, "studentId=810195115");
        HashMap<String, Object> response =
                Utils.sendRequest("POST", "http://localhost:7000/addCourse/8101008/01", null, "studentId=810195115");
        Document res = Jsoup.parse((String) response.get("data"));
        assertTrue(res.select("body").text().equals("403 class can not be added because of class time collision"));
    }
}
