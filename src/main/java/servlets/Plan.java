package servlets;

import models.statics.Constants;
import models.logic.Handlers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@WebServlet(name = "PlanServlet", value = "/plan")
public class Plan extends HttpServlet {

    public String createBody(ArrayList<HashMap<String, Object>> courses) throws IOException {
        Document reportDoc;
        // TODO remove this constant and add plan.txt file path
        reportDoc = Jsoup.parse(Constants.planbody, "UTF-8");
        Element table = reportDoc.getElementsByTag("table").last();
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Handlers.getInstance().getLoginUserId() == null) {
            response.sendRedirect(request.getContextPath()+ "/login");
        } else {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/plan.jsp");
            HashMap<String, Object> studentHashMap = Handlers.getInstance().selectStd(Handlers.getInstance().getLoginUserId());
            HashMap<String , Object> std = (HashMap<String, Object>) studentHashMap.get("student");
            ArrayList<HashMap<String, Object>> selectedCourses =
                    (ArrayList<HashMap<String, Object>>) std.get("lastPlan");
            request.setAttribute("planBody", createBody(selectedCourses));
            request.setAttribute("std", std);
            requestDispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
