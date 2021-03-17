package servlets;

import models.logic.Handlers;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "HomeServlet", value = "")
public class Home extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Handlers.getInstance().getLoginUserId() == null) {
            response.sendRedirect(request.getContextPath()+ "/login");
        } else {
            request.setAttribute("stdId", Handlers.getInstance().getLoginUserId());
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/home.jsp");
            requestDispatcher.forward(request, response);
        }
    }
}

