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

@WebServlet(name = "CoursesServlet", value = "/courses")
public class Courses extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Handlers.getInstance().getLoginUserId() == null) {
            response.sendRedirect(request.getContextPath()+ "/login");
        } else {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/courses.jsp");
            HashMap<String, Object> student = Handlers.getInstance().selectStd(Handlers.getInstance().getLoginUserId());
            HashMap<String, Object> courses = Handlers.getInstance().search(Handlers.getInstance().getLastSearchFilter());
            request.setAttribute("courses", courses.get("courses"));
            request.setAttribute("searchBox", Handlers.getInstance().getLastSearchFilter());
            request.setAttribute("std", student.get("student"));
            requestDispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action =  request.getParameter("action");
        String studentId = Handlers.getInstance().getLoginUserId();
        RequestDispatcher requestDispatcher;
        HashMap<String, Object> result;
        switch (action) {
            case "remove":
                String code_ = request.getParameter("code");
                String classCode_ = request.getParameter("classCode");
                result = Handlers.removeCourse(studentId, code_, classCode_);
                if(result == null) {
                    response.sendRedirect(request.getContextPath() + "/courses");
                } else {
                    request.setAttribute("result", result);
                    requestDispatcher = request.getRequestDispatcher("/error.jsp");
                    requestDispatcher.forward(request, response);
                }
                break;
            case "submit":
                result = Handlers.submitPlan(studentId);
                if(result == null) {
                    response.sendRedirect(request.getContextPath() + "/plan");
                } else {
                    request.setAttribute("result", result);
                    requestDispatcher = request.getRequestDispatcher("/error.jsp");
                    requestDispatcher.forward(request, response);
                }
                break;
            case "reset":
                result = Handlers.getInstance().reset(studentId);
                if(result == null) {
                    response.sendRedirect(request.getContextPath() + "/courses");
                } else {
                    request.setAttribute("result", result);
                    requestDispatcher = request.getRequestDispatcher("/error.jsp");
                    requestDispatcher.forward(request, response);
                }
                break;
            case "search":
                Handlers.getInstance().setLastSearchFilter(request.getParameter("searchBox"));
                response.sendRedirect(request.getContextPath() + "/courses");
                break;
            case "clear":
                Handlers.getInstance().setLastSearchFilter("");
                response.sendRedirect(request.getContextPath() + "/courses");
                break;
            case "add":
                String code = request.getParameter("code");
                String classCode = request.getParameter("classCode");
                // TODO we need add
                result = Handlers.addCourse(studentId, code, classCode);
                if(result == null) {
                    response.sendRedirect(request.getContextPath() + "/courses");
                } else {
                    request.setAttribute("result", result);
                    requestDispatcher = request.getRequestDispatcher("/error.jsp");
                    requestDispatcher.forward(request, response);
                }
                break;
            case "plan":
                response.sendRedirect(request.getContextPath() + "/plan");
                break;
            default:
                requestDispatcher = request.getRequestDispatcher("/courses.jsp");
                requestDispatcher.forward(request, response);

        }
        //TODO get action parameter form page and choose a routine
        // to do from models package { remove, submit, reset, search, clear, add}
    }
}
