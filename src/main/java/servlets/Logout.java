package servlets;

import models.logic.Handlers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", value = "/logout")
public class Logout extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO erase std form dataBase
        // String stdId = (String) request.getAttribute("stdId");
        // call remove function
        Handlers.getInstance().setLoginUserId(null);
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
