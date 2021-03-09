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

import logic.Server;
import statics.Constants;
import utils.Utils;

public class serverTest {

	private static final String BASE_URL = "http://localhost:" + Constants.SERVER_PORT;

	private static final String TEST_FILE_SUCCESS_SUBMIT =
		"src/test/resources/serverTest/success_submit.txt";
	private static final String TEST_FILE_SUCCESS_ADD_REMOVE_COURSE =
		"src/test/resources/serverTest/success_add_remove_course.txt";

	private static final String TEST_FILE_MINIMUM_UNITS =
		"src/test/resources/serverTest/minimum_units.txt";
	private static final String TEST_FILE_MAXIMUM_UNITS =
		"src/test/resources/serverTest/maximum_units.txt";
	private static final String TEST_FILE_PLAN_VIEW =
		"src/test/resources/serverTest/plan_view.txt";

	@Before
	public void setup() {
		String[] args = {};
		Server.main(args);
	}

	@After
	public void teardown() {
		Server.stop();
	}

	@Test
	public void successSubmitTest() throws IOException, InterruptedException {
		// Correct test
		Scanner scanner = new Scanner(new File(TEST_FILE_SUCCESS_SUBMIT));
		ObjectMapper mapper = new ObjectMapper();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			HashMap<String, String> params = mapper.readValue(line, HashMap.class);
			String[] urlParts = {
				BASE_URL,
				"addCourse",
				params.get("code"),
				params.get("classCode"),
			};
			String url = String.join("/", urlParts);
			Utils.sendRequest("POST", url, null, "studentId=810195115");
		}
		HashMap<String, Object> response = Utils.sendRequest(
			"POST",
			"http://localhost:7000/submitPlan",
			null,
			"studentId=810195115"
		);
		assertEquals("Optional[/submit_ok]", response.get("location"));
	}

	@Test
	public void successAddRemoveCourseTest() throws IOException, InterruptedException {
		// Correct test add and remove
		Scanner scanner = new Scanner(new File(TEST_FILE_SUCCESS_ADD_REMOVE_COURSE));
		ObjectMapper mapper = new ObjectMapper();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			HashMap<String, String> params = mapper.readValue(line, HashMap.class);
			String[] urlParts = {
				BASE_URL,
				"addCourse",
				params.get("code"),
				params.get("classCode"),
			};
			String url = String.join("/", urlParts);
			Utils.sendRequest("POST", url, null, "studentId=810195115");
		}
		String url = "http://localhost:7000/removeCourse/8101014/01";
		HashMap<String, Object> response = Utils.sendRequest(
			"POST",
			url,
			null,
			"studentId=810195115"
		);
		assertEquals("Optional[/change_plan/810195115]", response.get("location"));
	}

	@Test
	public void minimumUnitsTest() throws IOException, InterruptedException {
		// 11 units
		Scanner scanner = new Scanner(new File(TEST_FILE_MINIMUM_UNITS));
		ObjectMapper mapper = new ObjectMapper();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			HashMap<String, String> params = mapper.readValue(line, HashMap.class);
			String[] urlParts = {
				BASE_URL,
				"addCourse",
				params.get("code"),
				params.get("classCode"),
			};
			String url = String.join("/", urlParts);
			Utils.sendRequest("POST", url, null, "studentId=810197220");
		}
		HashMap<String, Object> response = Utils.sendRequest(
			"POST",
			"http://localhost:7000/submitPlan",
			null,
			"studentId=810197220"
		);
		assertEquals("Optional[/submit_failed]", response.get("location"));
	}

	@Test
	public void maximumUnitsTest() throws IOException, InterruptedException {
		// 22 units
		Scanner scanner = new Scanner(new File(TEST_FILE_MAXIMUM_UNITS));
		ObjectMapper mapper = new ObjectMapper();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			HashMap<String, String> params = mapper.readValue(line, HashMap.class);
			String[] urlParts = {
				BASE_URL,
				"addCourse",
				params.get("code"),
				params.get("classCode"),
			};
			String url = String.join("/", urlParts);
			Utils.sendRequest("POST", url, null, "studentId=810197227");
		}
		HashMap<String, Object> response = Utils.sendRequest(
			"POST",
			"http://localhost:7000/submitPlan",
			null,
			"studentId=810197227"
		);
		assertEquals("Optional[/submit_failed]", response.get("location"));
	}

	@Test
	public void notFoundCourseTest() throws IOException, InterruptedException {
		// failed 404 course not found
		String url = "http://localhost:7000/addCourse/8101152/01";
		HashMap<String, Object> response = Utils.sendRequest(
			"POST",
			url,
			null,
			"studentId=810197227"
		);
		Document reportDoc = Jsoup.parse(new File(Constants.PAGE_NOT_FOUND_PATH), "UTF-8");
		Document res = Jsoup.parse((String) response.get("data"));
		assertEquals(reportDoc.select("title").text(), res.select("title").text());
		assertEquals(reportDoc.select("h1").text(), res.select("h1").text());
		assertEquals(reportDoc.select("br").text(), res.select("br").text());
	}

	@Test
	public void notFoundStudentTest() throws IOException, InterruptedException {
		// failed 404 student not found
		String url = "http://localhost:7000/addCourse/8101152/01";
		HashMap<String, Object> response = Utils.sendRequest(
			"POST",
			url,
			null,
			"studentId=815367227"
		);
		Document reportDoc = Jsoup.parse(new File(Constants.PAGE_NOT_FOUND_PATH), "UTF-8");
		Document res = Jsoup.parse((String) response.get("data"));
		assertEquals(reportDoc.select("title").text(), res.select("title").text());
		assertEquals(reportDoc.select("h1").text(), res.select("h1").text());
		assertEquals(reportDoc.select("br").text(), res.select("br").text());
	}

	@Test
	public void planViewTest() throws IOException, InterruptedException {
		// test plan
		Scanner scanner = new Scanner(new File(TEST_FILE_PLAN_VIEW));
		ObjectMapper mapper = new ObjectMapper();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			HashMap<String, String> params = mapper.readValue(line, HashMap.class);
			String[] urlParts = {
				BASE_URL,
				"addCourse",
				params.get("code"),
				params.get("classCode"),
			};
			String url = String.join("/", urlParts);
			Utils.sendRequest("POST", url, null, "studentId=810195115");
		}
		HashMap<String, Object> response = Utils.sendRequest(
			"GET",
			"http://localhost:7000/plan/810195115",
			null,
			null
		);

		Document res = Jsoup.parse((String) response.get("data"));
		assertEquals("Plan", res.select("title").text());

		Element resSatElem = res.getElementById("Saturday");
		assertEquals(
			"Engineering Probability and Statistics",
			resSatElem.getElementById("9:00-10:30").text()
		);
		assertEquals("Calculus 1", resSatElem.getElementById("14:00-15:30").text());

		Element resSunElem = res.getElementById("Sunday");
		assertEquals("Data Structures", resSunElem.getElementById("7:30-9:00").text());
		assertEquals("Logic Circuits", resSunElem.getElementById("9:00-10:30").text());
		assertEquals(
			"Object Oriented Systems Design",
			resSunElem.getElementById("10:30-12:00").text()
		);

		Element resMonElem = res.getElementById("Monday");
		assertEquals(
			"Engineering Probability and Statistics",
			resMonElem.getElementById("9:00-10:30").text()
		);
		assertEquals("Calculus 1", resMonElem.getElementById("14:00-15:30").text());

		Element resTueElem = res.getElementById("Tuesday");
		assertEquals("Data Structures", resTueElem.getElementById("7:30-9:00").text());
		assertEquals("Logic Circuits", resTueElem.getElementById("9:00-10:30").text());
		assertEquals(
			"Object Oriented Systems Design",
			resTueElem.getElementById("10:30-12:00").text()
		);
	}

	@Test
	public void notPassedPrerequisitesTest() throws IOException, InterruptedException {
		// prerequisites are not passed
		HashMap<String, Object> response = Utils.sendRequest(
			"POST",
			"http://localhost:7000/addCourse/8101015/01",
			null,
			"studentId=810197227"
		);
		Document res = Jsoup.parse((String) response.get("data"));
		assertEquals(
			"403 class can not be added because prerequisites are not passed",
			res.select("body").text()
		);
	}

	@Test
	public void classTimeCollisionTest() throws IOException, InterruptedException {
		// class time collision
		Utils.sendRequest(
			"POST",
			"http://localhost:7000/addCourse/8101015/01",
			null,
			"studentId=810195115"
		);
		HashMap<String, Object> response = Utils.sendRequest(
			"POST",
			"http://localhost:7000/addCourse/8101008/01",
			null,
			"studentId=810195115"
		);
		Document res = Jsoup.parse((String) response.get("data"));
		assertEquals(
			"403 class can not be added because of class time collision",
			res.select("body").text()
		);
	}
}
