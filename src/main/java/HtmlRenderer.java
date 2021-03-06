import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import io.javalin.Javalin;

public class HtmlRenderer {
    // Render every page in seperate methods in this module

    // NOTE: this method is same as 'String.join("|", li)'
    public static String changeListFormat(ArrayList<String> li) {
        String s = "";
        for(int i = 0; i< li.size(); i++){
            if(i == 0) {
                s += li.get(i);
            } else {
                s += "|" + li.get(i);
            }
        }
        return s;
    }

    public static String renderCoursesPage(HashMap<String, Object> data) throws IOException {
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
                    "           <td>" + changeListFormat(o.getClassTime().getDays()) + "</td>\n" +
                    "           <td>" + o.getClassTime().getTime() + "</td>\n" +
                    "           <td>" + o.getExamTime().getStart() + "</td>\n" +
                    "           <td>" + o.getExamTime().getEnd() + "</td>\n" +
                    "           <td>" + changeListFormat(o.getPrerequisites()) + "</td>\n" +
                    "           <td>" + "<a href=/course/" + o.getCode() + "/01" + ">Link</a>" + "</td>\n" +
                    "       </tr>\n");
        }
        return reportDoc.html();
    }

    public static String renderStudentProfilePage(HashMap<String, Object> data) throws IOException {
        Document reportDoc = Jsoup.parse(new File("src/main/resources/templates/profile.html"), "UTF-8");
        Element ele = reportDoc.getElementsByTag("ul").last();
        Student s = (Student) data.values();
        ele.append("<li id=\"std_id\">Student Id: " + s.getId() + "</li>\n" +
                    "        <li id=\"first_name\">First Name: " + s.getName() + "</li>\n" +
                    "        <li id=\"last_name\">Last Name: " + s.getSecondName() + "</li>\n" +
                    "        <li id=\"birthdate\">Birthdate: " + s.getBirthDate() + "</li>\n" +
                    "        <li id=\"gpa\">GPA: " + "15" + "</li>\n" +
                    "        <li id=\"tpu\">Total Passed Units: 70</li>\n");
        return reportDoc.html();
    }


    public static String renderSingleCoursePage(HashMap<String, Object> data) {
        return "";
    }

    public static String renderChangePlanPage(HashMap<String, Object> data) {
        return "";
    }

    public static String renderPlanPage(HashMap<String, Object> data) {
        return "";
    }

    public static String renderSubmitPage(HashMap<String, Object> data) {
        return "";
    }

    public static String renderOkSubmitPage(HashMap<String, Object> data) {
        return "";
    }

    public static String renderFailSubmitPage(HashMap<String, Object> data) {
        return "";
    }

    public static String renderNotFoundPage() {
        return "";
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        Javalin app = Javalin.create().start(7000);
        ArrayList<Offering> l = DataBase.OfferingManager.getAllFromWebServer();
        HashMap<String, Object> hashMap = new HashMap<>();
        for (Offering str : l) {
            hashMap.put(str.getCode(), str);
        }
        System.out.println(renderCoursesPage(hashMap));
        app.get("/courses", ctx -> ctx.html(renderCoursesPage(hashMap)));
    }
}
