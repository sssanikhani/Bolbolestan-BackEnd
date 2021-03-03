import io.javalin.http.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handlers {

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

    public static Handler getCourses = ctx -> {
        IO apis = new IO();
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Courses</title>\n" +
                "    <style>\n" +
                "        table{\n" +
                "            width: 100%;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <table>\n" +
                "        <tr>\n" +
                "            <th>Code</th>\n" +
                "            <th>Class Code</th> \n" +
                "            <th>Name</th>\n" +
                "            <th>Units</th>\n" +
                "            <th>Capacity</th>\n" +
                "            <th>Type</th>\n" +
                "            <th>Days</th>\n" +
                "            <th>Time</th>\n" +
                "            <th>Exam Start</th>\n" +
                "            <th>Exam End</th>\n" +
                "            <th>Prerequisites</th>\n" +
                "            <th>Links</th>\n" +
                "        </tr>\n";

        for (Map.Entry<String,Offering> entry : apis.GETOfferings().entrySet()) {
            Offering o = entry.getValue();
            html += "       <tr>\n" +
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
                    "       </tr>\n";
        }
        html += "    </table>\n" +
                "</body>\n" +
                "</html>";
        ctx.html(html);
    };

    public static Handler stdProfile = ctx -> {
        IO apis = new IO();
        String stdId = ctx.pathParam("stdId");
        List<Grade> grades = apis.GETGrades(stdId);
        HashMap<String, Student> stds = apis.GETStds();
        HashMap<String, Offering> offerings = apis.GETOfferings();

        System.out.println(stds.size());
        System.out.println(grades.size());
        System.out.println(stdId);

        Student s = new Student();
        if(stds.get(stdId) != null) {
            s = stds.get(stdId);
        } else {
            String html = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>404 Error</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h1>404<br>Page Not Found</h1>\n" +
                    "</body>\n" +
                    "</html>";
            ctx.html(html);
            return;
        }

        int mean = 0;
        int totalPassedUnits = 0;
        for (Grade g : grades) {
            if(g.getGrade() >= 10) {
                totalPassedUnits += offerings.get(g.getCode()).getUnits();
            }
            mean += g.getGrade();
        }
        mean /= grades.size();
        String html = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Profile</title>\n" +
                    "    <style>\n" +
                    "        li {\n" +
                    "        \tpadding: 5px\n" +
                    "        }\n" +
                    "        table{\n" +
                    "            width: 10%;\n" +
                    "            text-align: center;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>";
            html += "<ul>\n" +
                    "        <li id=\"std_id\">Student Id: " + s.getId() + "</li>\n" +
                    "        <li id=\"first_name\">First Name: " + s.getName() + "</li>\n" +
                    "        <li id=\"last_name\">Last Name: " + s.getSecondName() + "</li>\n" +
                    "        <li id=\"birthdate\">Birthdate: " + s.getBirthDate() + "</li>\n" +
                    "        <li id=\"gpa\">GPA: "+ mean + "</li>\n" +
                    "        <li id=\"tpu\">Total Passed Units: " + totalPassedUnits + "</li>\n" +
                    "</ul>";
            html += "<table>\n" +
                    "        <tr>\n" +
                    "            <th>Code</th>\n" +
                    "            <th>Grade</th> \n" +
                    "        </tr>";
            for (Grade g : grades) {
                if (g.getGrade() >= 10) {
                    html += "<tr>\n" +
                            "            <td>" + g.getCode() + " </td>\n" +
                            "            <td>" + g.getGrade() + "</td> \n" +
                            "</tr>";
                }
            }
            html += "</table>\n" +
                    "</body>\n" +
                    "</html>";
            ctx.html(html);
        };

}
