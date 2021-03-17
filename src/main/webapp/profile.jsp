<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Profile</title>
    <style>
        li {
            padding: 5px
        }
        table{
            width: 10%;
            text-align: center;
        }
    </style>
</head>
<% HashMap<String, Object> data = (HashMap<String, Object>) request.getAttribute("std");%>
<body>
    <a href="/">Home</a>
    <ul>
        <li id="std_id">Student Id: <%= data.get("id")%></li>
        <li id="first_name">First Name: <%= data.get("name")%></li>
        <li id="last_name">Last Name: <%= data.get("secondName")%></li>
        <li id="birthdate">Birthdate: <%= data.get("birthDate")%></li>
        <li id="gpa">GPA: <%= data.get("gpa")%></li>
        <li id="tpu">Total Passed Units: <%= data.get("totalPassedUnits")%></li>
    </ul>
    <table>
        <tr>
            <th>Code</th>
            <th>Grade</th>
        </tr>
        <%
            ArrayList<HashMap<String, Object>> passedGrades = null;
                if(data.get("passedCoursesGrades") instanceof ArrayList) {
                passedGrades = (ArrayList<HashMap<String, Object>>) data.get("passedCoursesGrades");
            }
            assert passedGrades != null;
            for (HashMap<String, Object> entry : passedGrades) {
        %>
                <tr>
                    <td> <%= entry.get("code") %></td>
                    <td> <%= entry.get("grade")%></td>
                </tr>
        <%
            }
        %>
    </table>
</body>
</html>