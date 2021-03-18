package servlets;

import models.logic.DataBase;
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
        if (DataBase.getLoggedInUserId() == null) {
            response.sendRedirect(request.getContextPath()+ "/login");
        } else {
            HashMap<String, Object> student = Handlers.getStudentData(DataBase.getLoggedInUserId());
            request.setAttribute("student", student);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/profile.jsp");
            requestDispatcher.forward(request, response);
        }
    }
}
