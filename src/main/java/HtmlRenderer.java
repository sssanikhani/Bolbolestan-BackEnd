import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import io.javalin.Javalin;

public class HtmlRenderer {
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
    public static String renderCoursesPage(HashMap<String, Object> data) {
        
        // data format: {
        //      "courses": ArrayList<HashMap<String, Object>>
        // }
        // 
        // 

        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/courses.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
        Element table = reportDoc.getElementsByTag("table").last();
        ArrayList<HashMap<String, Object>> courses = (ArrayList<HashMap<String, Object>>) data.get("courses");
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
                String.valueOf(entry.get("units")),
                String.valueOf(entry.get("capacity")),
                (String) entry.get("type"),
                String.join("|", (ArrayList<String>) classTimeData.get("days")),
                (String) classTimeData.get("time"),
                examTimeData.get("start"),
                examTimeData.get("end"),
                String.join("|", (ArrayList<String>) entry.get("prerequisites")),
                link.outerHtml()
            };
            for (String value : rowValues) {
                Element cell = new Element(Tag.valueOf("td"), "");
                cell.html(value);
                row.appendChild(cell);
            }

            table.appendChild(row);
        }
        return reportDoc.html();
    }

    public static String renderStudentProfilePage(HashMap<String, Object> data) {

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
        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/profile.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
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
        
        ArrayList<HashMap<String, Object>> passedGrades = (ArrayList<HashMap<String, Object>>) data.get("passedCoursesGrades");
        Element table = reportDoc.getElementsByTag("table").last();
        for (HashMap<String, Object> entry : passedGrades) {
            Element row = new Element(Tag.valueOf("tr"), "");
            String[] rowValues = {
                (String) entry.get("code"),
                String.valueOf(entry.get("grade"))
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

    public static String renderSingleCoursePage(HashMap<String, Object> data) {

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

        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/course.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
        Element bodyElem = reportDoc.getElementsByTag("body").last();
        
        Element ulElem = new Element(Tag.valueOf("ul"), "");
        HashMap<String, Object> classTimeData = (HashMap<String, Object>) data.get("classTime");
        String[] courseValues = {
            "Code: " + data.get("code"),
            "Class Code: " + data.get("classCode"),
            "Name: " + data.get("name"),
            "Instructor: " + data.get("instructor"),
            "Units: "  + data.get("units"),
            "Days: " + String.join(",", (ArrayList<String>) classTimeData.get("days")),
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

        return reportDoc.html();

    }

    public static String renderChangePlanPage(HashMap<String, Object> data) {
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
        
        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/change_plan.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
        Element table = reportDoc.getElementsByTag("table").last();

        ArrayList<HashMap<String, Object>> chosenOfferings = (ArrayList<HashMap<String, Object>>) data.get("chosenOfferings");
        for (HashMap<String, Object> entry : chosenOfferings) {
            Element row = new Element(Tag.valueOf("tr"), "");
            String[] rowValues = {
                (String) entry.get("code"),
                (String) entry.get("classCode"),
                (String) entry.get("name"),
                (String) entry.get("instructor"),
                String.valueOf(entry.get("units"))
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
        }
        return reportDoc.html();
    }

    public static String renderPlanPage(HashMap<String, Object> data) {

        // data format: {
        //      "courses": ArrayList<HashMap<String, Object>>
        // }

        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/plan.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
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
            
        }
        return reportDoc.html();
    }
    
    public static String renderSubmitPage(HashMap<String, Object> data) {

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

        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
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

    public static String renderOkSubmitPage() {
        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit_ok.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
        return reportDoc.html();
    }

    public static String renderFailSubmitPage() {
        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit_failed.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
        return reportDoc.html();
    }

    public static String renderNotFoundPage() {
        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/404.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
        return reportDoc.html();
    }

    public static String renderPage(HashMap<String, Object> data) {
        Document reportDoc;
        try {
            reportDoc = Jsoup.parse(new File("src/main/resources/templates/blank.html"), "UTF-8");
        } catch (IOException e) {
            return "";
        }
        Element title = reportDoc.getElementsByTag("title").first();

        title.text((String) data.get("short"));

        Element h1 = reportDoc.getElementsByTag("h1").last();
        h1.text(String.valueOf(data.get("status")));

        Element bodyElem = reportDoc.getElementsByTag("body").last();
        bodyElem.append((String) data.get("message"));

        return reportDoc.html();
    }

}
