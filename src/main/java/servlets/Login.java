package servlets;

import models.logic.Handlers;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher  requestDispatcher = request.getRequestDispatcher("/login.jsp");
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String stdId = request.getParameter("stdId");
        if (stdId.equals("")) {
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            Handlers.getInstance().setLoginUserId(stdId);
            Handlers.getInstance().startup();
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}