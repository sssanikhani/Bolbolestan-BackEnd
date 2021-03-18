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
            ArrayList<HashMap<String, Object>> courses = (ArrayList<HashMap<String, Object>>) request.getAttribute("courses");
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
                <td><%= day %></td>
                <% for (String time : times) { %>
                    <td>
                        <% for (HashMap<String, Object> c : courses) {
                                HashMap<String, Object> cClassTime = (HashMap<String, Object>) c.get("classTime");
                                ArrayList<String> cDays = (ArrayList<String>) cClassTime.get("days");
                                String cTime = (String) cClassTime.get("time");
                                if (cDays.contains(day) && cTime.equals(time)) { %>
                                    <%= c.get("name") %>
                                <% }%>
                        <% } %>
                    </td>
                <% } %>
            
            </tr>
        <% } %>

    </table>
</body>
</html>