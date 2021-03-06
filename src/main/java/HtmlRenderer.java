import java.io.File;
import java.io.IOException;
import java.lang.invoke.SwitchPoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import io.javalin.Javalin;

public class HtmlRenderer {
    // Render every page in separate methods in this module

    // ! NOTE: this method is same as 'String.join("|", li)'
    public static String changeListFormat(ArrayList<String> list, char ch) {
        String s = "";
        for(int i = 0; i< list.size(); i++){
            if(i == 0)
                s += list.get(i);
            else
                s += ch + list.get(i);
        }
        return s;
    }

    //HashMap <"courses" , allCourses get form api>
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
        // }
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/courses.html"), "UTF-8");
        Element ele = reportDoc.getElementsByTag("table").last();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Offering o = (Offering) entry.getValue();
            ele.append("       <tr>\n" +
                    "           <td>" + o.getCode() + "</td>\n" +
                    "           <td>" + "01" + "</td>\n" +
                    "           <td>" + o.getName() + "</td>\n" +
                    "           <td>" + o.getUnits() + "</td>\n" +
                    "           <td>" + o.getCapacity() + "</td>\n" +
                    "           <td>" + o.getType() + "</td>\n" +
                    "           <td>" + changeListFormat(o.getClassTime().getDays(), '|') + "</td>\n" +
                    "           <td>" + o.getClassTime().getTime() + "</td>\n" +
                    "           <td>" + o.getExamTime().getStart() + "</td>\n" +
                    "           <td>" + o.getExamTime().getEnd() + "</td>\n" +
                    "           <td>" + changeListFormat(o.getPrerequisites(), '|') + "</td>\n" +
                    "           <td>" + "<a href=/course/" + o.getCode() + "/01" + ">Link</a>" + "</td>\n" +
                    "       </tr>\n");
        }
        return reportDoc.html();
    }

    //HashMap <"courses" , allCourses get form api>
    //          <"student", student send request Object>
    public static String renderStudentProfilePage(HashMap<String, Object> data) throws IOException {

        // data format: {
        //      "id": Student Id
        //      "name": First Name
        //      "secondName": Last Name
        //      "birthDate": Birth Date
        //      "gpa": GPA
        //      "totalPassedUnits": Total Passed Units
        //      "numberChosenUnits": Total Chosen Units
        // }
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/profile.html"), "UTF-8");
        Element eleUl = reportDoc.getElementsByTag("ul").last();
        //need here an item with key "courses" and value Hashmap of allCourses
        HashMap<String, Offering> o = (HashMap<String, Offering>) data.get("courses");
        //-------------------------------
        Student s = (Student) data.get("student");
        int mean = 0;
        int totalPassedUnits = 0;
        ArrayList<Grade> passedCourses = null;
        for(Map.Entry<String, Grade> entry : s.getGrades().entrySet()) {
            Grade g = (Grade)entry;
            mean += g.getGrade();
            if(g.getGrade() >= 10) {
                totalPassedUnits += o.get(g.getCode()).getUnits();
                passedCourses.add(g);
            }
        }
        mean /= s.getGrades().size();
        eleUl.append("<li id=\"std_id\">Student Id: " + s.getId() + "</li>\n" +
                    "        <li id=\"first_name\">First Name: " + s.getName() + "</li>\n" +
                    "        <li id=\"last_name\">Last Name: " + s.getSecondName() + "</li>\n" +
                    "        <li id=\"birthdate\">Birthdate: " + s.getBirthDate() + "</li>\n" +
                    "        <li id=\"gpa\">GPA: " + mean + "</li>\n" +
                    "        <li id=\"tpu\">Total Passed Units: " + totalPassedUnits + "</li>\n");
        Element eleTable = reportDoc.getElementsByTag("table").last();
        for (Grade g : passedCourses) {
            eleTable.append("<tr>\n" +
                    "            <th>" + g.getCode() + "</th>\n" +
                    "            <th>" + g.getGrade() + "</th> \n" +
                    "        </tr>");
        }
        return reportDoc.html();
    }

    //HashMap <"course" ,  course send request Object>
    //          <"student", student send request Object>
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
        // }
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/course.html"), "UTF-8");
        Element eleBody = reportDoc.getElementsByTag("body").last();
        Offering o = (Offering) data.get("course");
        Student s = (Student) data.get("student");
        eleBody.append("<ul>\n" +
                "        <li id=\"code\">Code: " + o.getCode() + "</li>\n" +
                "        <li id=\"class_code\">Class Code: " + o.getClassCode() + "</li>\n" +
                "        <li id=\"units\">units: " + o.getUnits() + "</li>\n" +
                "        <li id=\"days\">Days: " + changeListFormat(o.getClassTime().getDays(), ',')  + "</li>\n" +
                "        <li id=\"time\">Time: " + o.getClassTime().getTime() + "</li>\n" +
                "        <form action=\"/addCourse\" method=\"POST\" >\n" +
                "            <label>Student ID:</label>\n" +
                "            <input id=\"courseId\" type=\"hidden\" name=\"course_code\" value=\"" + o.getCode() + "\">\n" +
                "            <input id=\"courseClassCode\" type=\"hidden\" name=\"course_class_code\" value=\"" + o.getClassCode() + "\">\n" +
                "            <input id=\"stdId\" type=\"text\" name=\"std_id\" value=\"" + s.getId() + "\"/>\n" +
                "            <button type=\"submit\">Add</button>\n" +
                "        </form>\n" +
                "    </ul>\n");

        return reportDoc.html();

    }

    //HashMap <"student", student send request Object>
    public static String renderChangePlanPage(HashMap<String, Object> data) throws IOException {

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
        // }
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/change_plan.html"), "UTF-8");
        Element ele = reportDoc.getElementsByTag("table").last();
        //need this key and value here
        Student s =  (Student) data.get("student");
        //------------------------------
        for (Map.Entry<String, Offering> entry : s.getOfferings().entrySet()) {
            Offering o = entry.getValue();
            ele.append("<tr>\n" +
                    "            <td>" + o.getCode() + "</td>\n" +
                    "            <td>" + o.getClassCode() + "</td> \n" +
                    "            <td>" + o.getName() + "</td>\n" +
                    "            <td>" + o.getUnits() + "</td>\n" +
                    "            <td>        \n" +
                    "                <form action=\"/remove\" method=\"POST\" >\n" +
                    "                    <input id=\"courseId\" type=\"hidden\" name=\"course_code\" value=\"" + o.getCode() + "\">\n" +
                    "                    <input id=\"classCode\" type=\"hidden\" name=\"class_code\" value=\"" + o.getClassCode() + "\">\n" +
                    "                    <input id=\"stdId\" type=\"hidden\" name=\"std_id\" value=\"" + s.getId() + "\">\n" +
                    "                    <button type=\"submit\">Remove</button>\n" +
                    "                </form>\n" +
                    "            </td>\n" +
                    "        </tr>\n");
        }
        return reportDoc.html();
    }

    //HashMap <"student", student send request Object>
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
        // }
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/plan.html"), "UTF-8");
        Element ele = reportDoc.getElementsByTag("table").last();
        //need this key and value here
        Student s =  (Student) data.get("student");
        HashMap<String, Offering> offers = s.getOfferings();
        ArrayList<ArrayList<Offering>> li = sortTimes(offers);
        String[] days = new String[]{"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday"};
        for (int i = 0; i< li.size(); i++) {
            ele.append("<tr>\n" +
                    "            <td>" + days[i] + "</td>\n" +
                    "            <td>" + li.get(i).get(0) + "</td>\n" +
                    "            <td>" + li.get(i).get(1) + "</td>\n" +
                    "            <td>" + li.get(i).get(2) + "</td>\n" +
                    "            <td>" + li.get(i).get(3) + "</td>\n" +
                    "            <td>" + li.get(i).get(4) + "</td>\n" +
                    "</tr>\n");
        }
        return reportDoc.html();
    }

    private static ArrayList<ArrayList<Offering>> sortTimes(HashMap<String, Offering> offers) {
        ArrayList<ArrayList<Offering>> list = new ArrayList<ArrayList<Offering>>();
        ArrayList<ArrayList<Offering>> sortedList = new ArrayList<ArrayList<Offering>>();
        for (Map.Entry<String, Offering> entry : offers.entrySet()) {
            Offering o = (Offering) entry;
            if (o.getClassTime().getDays().contains("Saturday")) {
                list.get(0).add(o);
            }
            if (o.getClassTime().getDays().contains("Sunday")) {
                list.get(1).add(o);
            }
            if (o.getClassTime().getDays().contains("Monday")) {
                list.get(2).add(o);
            }
            if (o.getClassTime().getDays().contains("Tuesday")) {
                list.get(3).add(o);
            }
            if (o.getClassTime().getDays().contains("Wednesday")) {
                list.get(4).add(o);
            }
        }
        for (int i = 0; i< list.size(); i++) {
            for (int j = 0; j< list.get(i).size(); j++) {
                switch (list.get(i).get(j).getClassTime().getTime()) {
                    case "7:30-9:00":
                        sortedList.get(i).set(0, list.get(i).get(j));
                        break;
                    case "9:00-10:30":
                        sortedList.get(i).set(1, list.get(i).get(j));
                        break;
                    case "10:30-12:00":
                        sortedList.get(i).set(2, list.get(i).get(j));
                        break;
                    case "14:00-15:30":
                        sortedList.get(i).set(3, list.get(i).get(j));
                        break;
                    case "16:00-17:30":
                        sortedList.get(i).set(4, list.get(i).get(j));
                        break;
                }
            }
        }
        return sortedList;
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
        // }
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/submit.html"), "UTF-8");
        Element eleBody = reportDoc.getElementsByTag("body").last();
        Student s = (Student) data.get("student");
        int totalUnits = 0;
        for(Map.Entry<String, Offering> entry : s.getOfferings().entrySet()) {
            Offering o = entry.getValue();
            totalUnits += o.getUnits();
        }
        eleBody.append(" <ul>\n" +
                "        <li id=\"code\">Student Id: " + s.getId() + "</li>\n" +
                "        <li id=\"units\">Total Units: " + totalUnits + "</li>\n" +
                "        <form action=\"/submit\" method=\"POST\" >\n" +
                "            <input id=\"stdId\" type=\"hidden\" name=\"std_id\" value=\"" + s.getId() + "\">\n" +
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
