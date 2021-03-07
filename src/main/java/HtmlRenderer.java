import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import io.javalin.Javalin;

public class HtmlRenderer {
    // Render every page in separate methods in this module

    // ! NOTE: this method is same as 'String.join("|", li)'
    public static String joinString(ArrayList<String> list, char ch) {
        String s = "";
        for(int i = 0; i< list.size(); i++){
            if(i == 0)
                s += list.get(i);
            else
                s += ch + list.get(i);
        }
        return s;
    }

    public static String renderCoursesPage(HashMap<String, Object> data) throws IOException {
        
        // data format: {
        //      "courses": ArrayList<HashMap<String, Object>>
        // }
        // 
        // 
        // each entry in data.get("courses"): {
        //      "code": Code
        //      "classCode": Class Code 
        //      "name": Name
        //      "instructor": Instructor
        //      "units": Units
        //      "capacity": Capacity
        //      "numRegisteredStudents": Registered
        //      "type": Type
        //      "classTime": {
        //          "days": []  Days
        //          "time": Time
        //      }
        //      "examTime": {
        //          "start": Exam Start
        //          "end": Exam End
        //      }
        //      "prerequisites": []  Prerequisites
        //      "link": Link
        //      "addLink": Add Link
        //      "removeLink": Remove Link
        // }
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/courses.html"), "UTF-8");
        Element ele = reportDoc.getElementsByTag("table").last();
        for (HashMap<String, Object> entry : (ArrayList<HashMap<String, Object>>)data.get("courses")) {
            HashMap<String, Object> classTimeData = (HashMap<String, Object>) entry.get("classTime");
            HashMap<String, Object> examTime = (HashMap<String, Object>) entry.get("examTime");
            ele.append("       <tr>\n" +
                    "           <td>" + entry.get("code") + "</td>\n" +
                    "           <td>" + entry.get("classCode") + "</td>\n" +
                    "           <td>" + entry.get("name") + "</td>\n" +
                    "           <td>" + entry.get("units") + "</td>\n" +
                    "           <td>" + entry.get("capacity") + "</td>\n" +
                    "           <td>" + entry.get("type")+ "</td>\n" +
                    "           <td>" + joinString((ArrayList<String>) classTimeData.get("days"), '|') + "</td>\n" +
                    "           <td>" + classTimeData.get("time") + "</td>\n" +
                    "           <td>" + examTime.get("start") + "</td>\n" +
                    "           <td>" + examTime.get("end") + "</td>\n" +
                    "           <td>" + joinString((ArrayList<String>) classTimeData.get("prerequisites"), '|') + "</td>\n" +
                    "           <td>" + "<a href=/course/" + entry.get("code") + "/" + entry.get("classCode") + ">Link</a>" + "</td>\n" +
                    "       </tr>\n");
        }
        return reportDoc.html();
    }

    public static String renderStudentProfilePage(HashMap<String, Object> data) throws IOException {

        // data format: {
        //      "id": Student Id
        //      "name": First Name
        //      "secondName": Last Name
        //      "birthDate": Birth Date
        //      "gpa": GPA
        //      "totalPassedUnits": Total Passed Units
        //      "numberChosenUnits": Total Chosen Units
        //      "chosenOfferings": ArrayList<HashMap<String, Object>>
        //      "passedCoursesGrades": ArrayList<HashMap<String, Object>>
        //      "profileLink": Profile Link
        // }
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/profile.html"), "UTF-8");
        Element eleUl = reportDoc.getElementsByTag("ul").last();
        eleUl.append("<li id=\"std_id\">Student Id: " + data.get("id") + "</li>\n" +
                    "        <li id=\"first_name\">First Name: " + data.get("name") + "</li>\n" +
                    "        <li id=\"last_name\">Last Name: " + data.get("secondName") + "</li>\n" +
                    "        <li id=\"birthdate\">Birthdate: " + data.get("birthDate") + "</li>\n" +
                    "        <li id=\"gpa\">GPA: " + data.get("gpa") + "</li>\n" +
                    "        <li id=\"tpu\">Total Passed Units: " + data.get("totalPassedUnits") + "</li>\n");
        Element eleTable = reportDoc.getElementsByTag("table").last();
        for (HashMap<String, Object> entry : (ArrayList<HashMap<String, Object>>)data.get("passedCoursesGrades")) {
            eleTable.append("<tr>\n" +
                    "            <th>" + entry.get("code") + "</th>\n" +
                    "            <th>" + entry.get("grade") + "</th> \n" +
                    "        </tr>");
        }
        return reportDoc.html();
    }

    public static String renderSingleCoursePage(HashMap<String, Object> data) throws IOException {

        // data format: {
        //      "code": Code
        //      "classCode": Class Code 
        //      "name": Name
        //      "instructor": Instructor
        //      "units": Units
        //      "capacity": Capacity
        //      "numRegisteredStudents": Registered
        //      "type": Type
        //      "classTime": {
        //          "days": []  Days
        //          "time": Time
        //      }
        //      "examTime": {
        //          "start": Exam Start
        //          "end": Exam End
        //      }
        //      "prerequisites": []  Prerequisites
        //      "link": Link
        //      "addLink": Add Link
        //      "removeLink": Remove Link
        // }
        // 

        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/course.html"), "UTF-8");
        Element eleBody = reportDoc.getElementsByTag("body").last();
        HashMap<String, Object> classTimeData = (HashMap<String, Object>) data.get("classTime");
        eleBody.append("<ul>\n" +
                "        <li id=\"code\">Code: " + data.get("code") + "</li>\n" +
                "        <li id=\"class_code\">Class Code: " + data.get("classCode") + "</li>\n" +
                "        <li id=\"units\">units: " +  data.get("units") + "</li>\n" +
                "        <li id=\"days\">Days: " + joinString((ArrayList<String>) classTimeData.get("days"), ',')  + "</li>\n" +
                "        <li id=\"time\">Time: " + classTimeData.get("time") + "</li>\n" +
                "        <form action=\"/addCourse\" method=\"POST\" >\n" +
                "            <label>Student ID:</label>\n" +
                "            <input id=\"courseId\" type=\"hidden\" name=\"course_code\" value=\"" + data.get("code") + "\">\n" +
                "            <input id=\"courseClassCode\" type=\"hidden\" name=\"course_class_code\" value=\"" + data.get("classCode") + "\">\n" +
                "            <input id=\"stdId\" type=\"text\" name=\"std_id\" value=\"\"/>\n" +
                "            <button type=\"submit\">Add</button>\n" +
                "        </form>\n" +
                "    </ul>\n");

        return reportDoc.html();

    }

    public static String renderChangePlanPage(HashMap<String, Object> data) throws IOException {
        // data format: {
        //      "id": Student Id
        //      "name": First Name
        //      "secondName": Last Name
        //      "birthDate": Birth Date
        //      "gpa": GPA
        //      "totalPassedUnits": Total Passed Units
        //      "numberChosenUnits": Total Chosen Units
        //      "chosenOfferings": ArrayList<HashMap<String, Object>>
        //      "passedCoursesGrades": ArrayList<HashMap<String, Object>>
        //      "profileLink": Profile Link
        // }
        // 
        // 
        // each entry in data.get("chosenOfferings"): {
        //      "code": Code
        //      "classCode": Class Code 
        //      "name": Name
        //      "instructor": Instructor
        //      "units": Units
        //      "capacity": Capacity
        //      "numRegisteredStudents": Registered
        //      "type": Type
        //      "classTime": {
        //          "days": []  Days
        //          "time": Time
        //      }
        //      "examTime": {
        //          "start": Exam Start
        //          "end": Exam End
        //      }
        //      "prerequisites": []  Prerequisites
        //      "link": Link
        //      "addLink": Add Link
        //      "removeLink": Remove Link
        // }
        // 
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/change_plan.html"), "UTF-8");
        Element ele = reportDoc.getElementsByTag("table").last();
        for (HashMap<String, Object> entry : (ArrayList<HashMap<String, Object>>)data.get("chosenOfferings")) {
            ele.append("<tr>\n" +
                    "            <td>" + entry.get("code") + "</td>\n" +
                    "            <td>" + entry.get("classCode") + "</td> \n" +
                    "            <td>" + entry.get("name") + "</td>\n" +
                    "            <td>" + entry.get("units") + "</td>\n" +
                    "            <td>        \n" +
                    "                <form action=\"/remove\" method=\"POST\" >\n" +
                    "                    <input id=\"courseId\" type=\"hidden\" name=\"course_code\" value=\"" + entry.get("code") + "\">\n" +
                    "                    <input id=\"classCode\" type=\"hidden\" name=\"class_code\" value=\"" + entry.get("classCode") + "\">\n" +
                    "                    <input id=\"stdId\" type=\"hidden\" name=\"std_id\" value=\"" + entry.get("studentId") + "\">\n" +
                    "                    <button type=\"submit\">Remove</button>\n" +
                    "                </form>\n" +
                    "            </td>\n" +
                    "        </tr>\n");
        }
        return reportDoc.html();
    }

    public static String renderPlanPage(HashMap<String, Object> data) throws IOException {

        // data format: {
        //      "courses": ArrayList<HashMap<String, Object>>
        // }
        // 
        // 
        // each entry in data.get("courses"): {
        //      "code": Code
        //      "classCode": Class Code 
        //      "name": Name
        //      "instructor": Instructor
        //      "units": Units
        //      "capacity": Capacity
        //      "numRegisteredStudents": Registered
        //      "type": Type
        //      "classTime": {
        //          "days": []  Days
        //          "time": Time
        //      }
        //      "examTime": {
        //          "start": Exam Start
        //          "end": Exam End
        //      }
        //      "prerequisites": []  Prerequisites
        //      "link": Link
        //      "addLink": Add Link
        //      "removeLink": Remove Link
        // }
        //
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/plan.html"), "UTF-8");
        Element ele = reportDoc.getElementsByTag("table").last();
        for (HashMap<String, Object> entry : (ArrayList<HashMap<String, Object>>)data.get("courses")) {
            HashMap<String, Object> classTimeData = (HashMap<String, Object>) entry.get("classTime");
            if(((ArrayList<String>) classTimeData.get("days")).contains("Saturday")){
                ele.getElementById("Sat").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            }
            if(((ArrayList<String>) classTimeData.get("days")).contains("Sunday")){
                ele.getElementById("Sun").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            }
            if(((ArrayList<String>) classTimeData.get("days")).contains("Monday")){
                ele.getElementById("Mon").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            }
            if(((ArrayList<String>) classTimeData.get("days")).contains("Tuesday")){
                ele.getElementById("Tue").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            }
            if(((ArrayList<String>) classTimeData.get("days")).contains("Wednesday")){
                ele.getElementById("Wen").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            }
        }
        return reportDoc.html();
    }
    
    //HashMap <"student", student send request Object>
    public static String renderSubmitPage(HashMap<String, Object> data) throws IOException {

        // data format: {
        //      "id": Student Id
        //      "name": First Name
        //      "secondName": Last Name
        //      "birthDate": Birth Date
        //      "gpa": GPA
        //      "totalPassedUnits": Total Passed Units
        //      "numberChosenUnits": Total Chosen Units
        //      "chosenOfferings": ArrayList<HashMap<String, Object>>
        //      "passedCoursesGrades": ArrayList<HashMap<String, Object>>
        //      "profileLink": Profile Link
        // }
        // 

        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit.html"), "UTF-8");
        Element eleBody = reportDoc.getElementsByTag("body").last();
        eleBody.append(" <ul>\n" +
                "        <li id=\"code\">Student Id: " + data.get("id") + "</li>\n" +
                "        <li id=\"units\">Total Units: " + data.get("numberChosenUnits") + "</li>\n" +
                "        <form action=\"/submit\" method=\"POST\" >\n" +
                "            <input id=\"stdId\" type=\"hidden\" name=\"std_id\" value=\"" + data.get("id") + "\">\n" +
                "            <button type=\"submit\">submit</button>\n" +
                "        </form>\n" +
                "    </ul>\n");
        return reportDoc.html();
    }

    public static String renderOkSubmitPage() throws IOException {
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit_ok.html"), "UTF-8");
        return reportDoc.html();
    }

    public static String renderFailSubmitPage() throws IOException {
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit_failed.html"), "UTF-8");
        return reportDoc.html();
    }

    public static String renderNotFoundPage() throws IOException {
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/404.html"), "UTF-8");
        return reportDoc.html();
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        Javalin app = Javalin.create().start(7000);
//        ArrayList<Offering> l = DataBase.OfferingManager.getAllFromWebServer();
//        HashMap<String, Object> hashMap = new HashMap<>();
//        for (Offering str : l) {
//            hashMap.put(str.getCode(), str);
//        }
//        System.out.println(renderCoursesPage(hashMap));
//        app.get("/courses", ctx -> ctx.html(renderCoursesPage(hashMap)));
    }
}
