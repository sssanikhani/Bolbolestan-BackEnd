package servlets;

import models.logic.Handlers;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "ProfileServlet", value = "/profile")
public class Profile extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO pass a hashmap form model include
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
        //TODO set request.setAttribute("data", profileData);
        if (Handlers.getInstance().getLoginUserId() == null) {
            response.sendRedirect(request.getContextPath()+ "/login");
        } else {
            HashMap<String, Object> student = Handlers.getInstance().selectStd(Handlers.getInstance().getLoginUserId());
            request.setAttribute("std", student.get("student"));
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/profile.jsp");
            requestDispatcher.forward(request, response);
        }
    }
}
