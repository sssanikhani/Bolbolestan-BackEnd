<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Plan</title>
    <style>
        table{
            width: 100%;
            text-align: center;

        }
        table, th, td{
            border: 1px solid #000000;
            border-collapse: collapse;
        }
    </style>
</head>
<body>
    <% HashMap<String , Object> student = (HashMap<String, Object>) request.getAttribute("student");%>
    <ul>
        <a href="/">Home</a>
        <li id="code">Student Id: <%= student.get("id") %> </li>
    </ul>
    <br>
    <table>
        <% 
            String[] weekDays = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday"}; 
            String[] times = {"7:30-9:00", "9:00-10:30", "10:30-12:00", "14:00-15:30", "16:00-17:30"};
            HashMap<String, Object> plan = (HashMap<String, Object>) request.getAttribute("plan");
        %>
        <tr>
            <th></th>
            <% for (String time : times) { %>
                <th>
                    <%= time %>
                </th>
            <% } %>
        </tr>

        <%  %>
        <% for (String day : weekDays) { %>
            <tr>
                <% HashMap<String, String> dayMap = (HashMap<String, String>) plan.get(day); %>
                <td><%= day %></td>
                <% for (String time : times) { %>
                    <td>
                        <% 
                            String courseName = dayMap.get(time);
                            if (courseName != null) {
                        %>
                            <%= courseName %>
                        <%  } %>
                    </td>
                <% } %>
            
            </tr>
        <% } %>

    </table>
</body>
</html>