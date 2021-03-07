import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

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
        Element table = reportDoc.getElementsByTag("table").last();
        HashMap<String, Object>[] courses = (HashMap<String, Object>[]) data.get("courses");
        for (HashMap<String, Object> entry : courses) {
            HashMap<String, Object> classTimeData = (HashMap<String, Object>) entry.get("classTime");
            HashMap<String, String> examTimeData = (HashMap<String, String>) entry.get("examTime");
            Element row = new Element(Tag.valueOf("tr"), "");
            Element link = new Element(Tag.valueOf("a"), "");
            link.attr("href", (String) entry.get("link"));
            link.text("Link");
            String[] rowValues = {
                (String) entry.get("code"),
                (String) entry.get("classCode"),
                (String) entry.get("name"),
                (String) entry.get("instructor"),
                (String) entry.get("units"),
                (String) entry.get("capacity"),
                (String) entry.get("type"),
                String.join("|", (String[]) classTimeData.get("days")),
                (String) classTimeData.get("time"),
                examTimeData.get("start"),
                examTimeData.get("end"),
                String.join("|", (String[]) entry.get("prerequisites")),
                link.html()
            };
            for (String value : rowValues) {
                Element cell = new Element(Tag.valueOf("td"), "");
                cell.text(value);
                row.appendChild(cell);
            }

            table.appendChild(row);
            // ele.append("       <tr>\n" +
            //         "           <td>" + entry.get("code") + "</td>\n" +
            //         "           <td>" + entry.get("classCode") + "</td>\n" +
            //         "           <td>" + entry.get("name") + "</td>\n" +
            //         "           <td>" + entry.get("units") + "</td>\n" +
            //         "           <td>" + entry.get("capacity") + "</td>\n" +
            //         "           <td>" + entry.get("type")+ "</td>\n" +
            //         "           <td>" + joinString((ArrayList<String>) classTimeData.get("days"), '|') + "</td>\n" +
            //         "           <td>" + classTimeData.get("time") + "</td>\n" +
            //         "           <td>" + examTimeData.get("start") + "</td>\n" +
            //         "           <td>" + examTimeData.get("end") + "</td>\n" +
            //         "           <td>" + joinString((ArrayList<String>) classTimeData.get("prerequisites"), '|') + "</td>\n" +
            //         "           <td>" + "<a href=/course/" + entry.get("code") + "/" + entry.get("classCode") + ">Link</a>" + "</td>\n" +
            //         "       </tr>\n");
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
        Element ulElem = reportDoc.getElementsByTag("ul").last();
        String[] studentValues = {
            "Student ID:" + data.get("id"),
            "First Name: " + data.get("name"),
            "Last Name: " + data.get("secondName"),
            "Birthdate: " + data.get("birthDate"),
            "GPA: " + data.get("gpa"),
            "Total Passed Units: " + data.get("totalPassedUnits"),
        };

        for (String value : studentValues) {
            Element field = new Element(Tag.valueOf("li"), "");
            field.text(value);
            ulElem.appendChild(field);
        }

        // ulElem.append("<li id=\"std_id\">Student ID: " + data.get("id") + "</li>\n" +
        //             "        <li id=\"first_name\">First Name: " + data.get("name") + "</li>\n" +
        //             "        <li id=\"last_name\">Last Name: " + data.get("secondName") + "</li>\n" +
        //             "        <li id=\"birthdate\">Birthdate: " + data.get("birthDate") + "</li>\n" +
        //             "        <li id=\"gpa\">GPA: " + data.get("gpa") + "</li>\n" +
        //             "        <li id=\"tpu\">Total Passed Units: " + data.get("totalPassedUnits") + "</li>\n");
        
        ArrayList<HashMap<String, Object>> passedGrades = (ArrayList<HashMap<String, Object>>) data.get("passedCoursesGrades");
        Element table = reportDoc.getElementsByTag("table").last();
        for (HashMap<String, Object> entry : passedGrades) {
            Element row = new Element(Tag.valueOf("tr"), "");
            String[] rowValues = {
                (String) entry.get("code"),
                (String) entry.get("name"),
                (String) entry.get("grade")
            };
            for (String value : rowValues) {
                Element cell = new Element(Tag.valueOf("td"), "");
                cell.text(value);
                row.appendChild(cell);
            }
            table.appendChild(row);
            // table.append("<tr>\n" +
            //         "            <th>" + entry.get("code") + "</th>\n" +
            //         "            <th>" + entry.get("grade") + "</th> \n" +
            //         "        </tr>");
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
        Element bodyElem = reportDoc.getElementsByTag("body").last();
        
        Element ulElem = new Element(Tag.valueOf("ul"), "");
        HashMap<String, Object> classTimeData = (HashMap<String, Object>) data.get("classTime");
        String[] courseValues = {
            "Code: " + data.get("code"),
            "Class Code: " + data.get("classCode"),
            "Name: " + data.get("name"),
            "Instructor" + data.get("instructor"),
            "Units: "  + data.get("units"),
            "Days: " + String.join(",", (String[]) classTimeData.get("days")),
            "Time: " + classTimeData.get("time"),
        };
        for (String value : courseValues) {
            Element field = new Element(Tag.valueOf("li"), "");
            field.text(value);
            ulElem.appendChild(field);
        }

        Element form = new Element(Tag.valueOf("form"), "");
        form.attr("action", (String) data.get("addLink"));
        form.attr("method", "post");

        Element studentIdInputElem = new Element(Tag.valueOf("input"), "");
        studentIdInputElem.attr("id", "studentId");
        studentIdInputElem.attr("name", "studentId");
        studentIdInputElem.attr("type", "text");
        studentIdInputElem.attr("value", "");
        
        Element submitElem = new Element(Tag.valueOf("button"), "");
        submitElem.attr("type", "submit");
        submitElem.text("Add");

        form.append("<label>Student ID:</label>");
        form.appendChild(studentIdInputElem);
        form.appendChild(submitElem);
        
        bodyElem.appendChild(ulElem);
        bodyElem.appendChild(form);

        // bodyElem.append("<ul>\n" +
        //         "        <li id=\"code\">Code: " + data.get("code") + "</li>\n" +
        //         "        <li id=\"class_code\">Class Code: " + data.get("classCode") + "</li>\n" +
        //         "        <li id=\"units\">units: " +  data.get("units") + "</li>\n" +
        //         "        <li id=\"days\">Days: " + joinString((ArrayList<String>) classTimeData.get("days"), ',')  + "</li>\n" +
        //         "        <li id=\"time\">Time: " + classTimeData.get("time") + "</li>\n" +
        //         "        <form action=\"/addCourse\" method=\"post\" >\n" +
        //         "            <label>Student ID:</label>\n" +
        //         "            <input id=\"courseId\" type=\"hidden\" name=\"course_code\" value=\"" + data.get("code") + "\">\n" +
        //         "            <input id=\"courseClassCode\" type=\"hidden\" name=\"course_class_code\" value=\"" + data.get("classCode") + "\">\n" +
        //         "            <input id=\"stdId\" type=\"text\" name=\"std_id\" value=\"\"/>\n" +
        //         "            <button type=\"submit\">Add</button>\n" +
        //         "        </form>\n" +
        //         "    </ul>\n");

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
        Element table = reportDoc.getElementsByTag("table").last();

        ArrayList<HashMap<String, Object>> chosenOfferings = (ArrayList<HashMap<String, Object>>) data.get("chosenOfferings");
        for (HashMap<String, Object> entry : chosenOfferings) {
            Element row = new Element(Tag.valueOf("tr"), "");
            String[] rowValues = {
                (String) entry.get("code"),
                (String) entry.get("classCode"),
                (String) entry.get("name"),
                (String) entry.get("instructor"),
                (String) entry.get("units")
            };
            for (String value : rowValues) {
                Element cell = new Element(Tag.valueOf("td"), "");
                cell.text(value);
                row.appendChild(cell);
            }

            Element formCell = new Element(Tag.valueOf("td"), "");
            
            Element form = new Element(Tag.valueOf("form"), "");
            form.attr("action", (String) entry.get("removeLink"));
            form.attr("method", "post");

            Element studentIdInputElem = new Element(Tag.valueOf("input"), "");
            studentIdInputElem.attr("id", "studentId");
            studentIdInputElem.attr("type", "hidden");
            studentIdInputElem.attr("name", "studentId");
            studentIdInputElem.attr("value", (String) data.get("id"));

            Element submit = new Element(Tag.valueOf("button"), "");
            submit.attr("type", "submit");
            submit.text("Remove");

            form.appendChild(studentIdInputElem);
            form.appendChild(submit);
            
            formCell.appendChild(form);

            row.appendChild(formCell);

            table.appendChild(row);
            
            
            // table.append("<tr>\n" +
            //         "            <td>" + entry.get("code") + "</td>\n" +
            //         "            <td>" + entry.get("classCode") + "</td> \n" +
            //         "            <td>" + entry.get("name") + "</td>\n" +
            //         "            <td>" + entry.get("units") + "</td>\n" +
            //         "            <td>        \n" +
            //         "                <form action=\"/remove\" method=\"post\" >\n" +
            //         "                    <input id=\"courseId\" type=\"hidden\" name=\"course_code\" value=\"" + entry.get("code") + "\">\n" +
            //         "                    <input id=\"classCode\" type=\"hidden\" name=\"class_code\" value=\"" + entry.get("classCode") + "\">\n" +
            //         "                    <input id=\"stdId\" type=\"hidden\" name=\"std_id\" value=\"" + data.get("id") + "\">\n" +
            //         "                    <button type=\"submit\">Remove</button>\n" +
            //         "                </form>\n" +
            //         "            </td>\n" +
            //         "        </tr>\n");
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
        Element table = reportDoc.getElementsByTag("table").last();
        ArrayList<HashMap<String, Object>> courses = (ArrayList<HashMap<String, Object>>) data.get("courses");
        String[] weekDays = { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday" };
        for (HashMap<String, Object> entry : courses) {
            HashMap<String, Object> classTimeData = (HashMap<String, Object>) entry.get("classTime");
            String name = (String) entry.get("name");
            ArrayList<String> days = (ArrayList<String>) classTimeData.get("days");
            String time = (String) classTimeData.get("time");
            
            for (String weekDay : weekDays) {
                if (days.contains(weekDay)) {
                    Element dayElem = table.getElementById(weekDay);
                    Element cell = dayElem.getElementById(time);
                    cell.text(name);
                }
            }
            
            // if(days.contains("Saturday")){
            //     table.getElementById("Sat").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            // }
            // if(days.contains("Sunday")){
            //     table.getElementById("Sun").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            // }
            // if(days.contains("Monday")){
            //     table.getElementById("Mon").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            // }
            // if(days.contains("Tuesday")){
            //     table.getElementById("Tue").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            // }
            // if(days.contains("Wednesday")){
            //     table.getElementById("Wen").getElementById((String) classTimeData.get("time")).text((String) entry.get("name"));
            // }
        }
        return reportDoc.html();
    }
    
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
        // 
        //      "submitLink": Submit Link
        // }
        // 

        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit.html"), "UTF-8");
        Element ulElem = reportDoc.getElementsByTag("ul").last();
        String[] values = {
            "Student ID: " + data.get("id"),
            "Total Units: " + data.get("numberChosenUnits")
        };
        for (String value : values) {
            Element field = new Element(Tag.valueOf("li"), "");
            field.text(value);
            ulElem.appendChild(field);
        }

        Element form = new Element(Tag.valueOf("form"), "");
        form.attr("action", (String) data.get("submitLink"));
        form.attr("method", "post");

        Element studentIdInputElem = new Element(Tag.valueOf("input"), "");
        studentIdInputElem.attr("id", "studentId");
        studentIdInputElem.attr("type", "hidden");
        studentIdInputElem.attr("name", "studentId");
        studentIdInputElem.attr("value", (String) data.get("id"));

        Element submit = new Element(Tag.valueOf("button"), "");
        submit.attr("type", "submit");
        submit.text("submit");

        form.appendChild(studentIdInputElem);
        form.appendChild(submit);

        ulElem.appendChild(form);
        
        // ulElem.append("  <li id=\"code\">Student Id: " + data.get("id") + "</li>\n" +
        //         "        <li id=\"units\">Total Units: " + data.get("numberChosenUnits") + "</li>\n" +
        //         "        <form action=\"/submit\" method=\"post\" >\n" +
        //         "            <input id=\"stdId\" type=\"hidden\" name=\"std_id\" value=\"" + data.get("id") + "\">\n" +
        //         "            <button type=\"submit\">submit</button>\n" +
        //         "        </form>\n");
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

    public static String renderPage(HashMap<String, Object> data) throws IOException {
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/blank.html"), "UTF-8");
        Element title = reportDoc.getElementsByTag("title").first();

        title.text((String) data.get("short"));

        Element h1 = reportDoc.getElementsByTag("h1").last();
        h1.text(String.valueOf(data.get("status")));

        Element bodyElem = reportDoc.getElementsByTag("body").last();
        bodyElem.append((String) data.get("message"));

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
