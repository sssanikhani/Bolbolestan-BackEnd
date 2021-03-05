import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class serverTest {



    @Before
    public void setup() {
        Javalin testServer = Javalin.create().start(7000);
    }

    @Test
    public void Test1() throws IOException, InterruptedException {
        //True test
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test1.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, Object> reqBody = mapper.readValue(line, HashMap.class);
            Utils.sendRequest("POST", "/addCourse", null, reqBody);
        }
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("stdId", "810195115");
        Utils.sendRequest("POST", "/submit", null, body);
    }

    @Test
    public void Test2() throws IOException, InterruptedException {
        //True test add and remove
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test2.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, Object> reqBody = mapper.readValue(line, HashMap.class);
            Utils.sendRequest("POST", "/addCourse", null, reqBody);
        }
        HashMap<String, Object> removeBody = new HashMap<String, Object>();
        removeBody.put("stdId", "810195115");
        removeBody.put("courseId", "8101014");
        removeBody.put("classCode", "01");
        Utils.sendRequest("POST", "/remove", null, removeBody);
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("stdId", "810195115");
        Utils.sendRequest("POST", "/submit", null, body);
    }

    @Test
    public void Test3() throws IOException, InterruptedException {
        //failed with 13 units
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test3.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, Object> reqBody = mapper.readValue(line, HashMap.class);
            Utils.sendRequest("POST", "/addCourse", null, reqBody);
        }
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("stdId", "810197220");
        Utils.sendRequest("POST", "/submit", null, body);
    }
    @Test
    public void Test4() throws IOException, InterruptedException {
        //failed with 22 units
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test4.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, Object> reqBody = mapper.readValue(line, HashMap.class);
            Utils.sendRequest("POST", "/addCourse", null, reqBody);
        }
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("stdId", "810197227");
        Utils.sendRequest("POST", "/submit", null, body);
    }

    @Test
    public void Test5() throws IOException, InterruptedException {
        //true test with conflict 15/08
        Scanner scanner = new Scanner(new File("src/test/resources/serverTest/test5.txt"));
        ObjectMapper mapper = new ObjectMapper();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            HashMap<String, Object> reqBody = mapper.readValue(line, HashMap.class);
            Utils.sendRequest("POST", "/addCourse", null, reqBody);
        }
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("stdId", "810197227");
        Utils.sendRequest("POST", "/submit", null, body);
    }

    @Test
    public void Test6() throws IOException, InterruptedException {
        //failed 404 course not found
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("stdId", "810197227");
        body.put("courseId", "8101152");
        body.put("classCode", "01");
        Utils.sendRequest("POST", "/addCourse", null, body);
    }

    @Test
    public void Test7() throws IOException, InterruptedException {
        //failed 404 student not found
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("stdId", "815367227");
        body.put("courseId", "8101002");
        body.put("classCode", "01");
        Utils.sendRequest("POST", "/addCourse", null, body);
    }
}
