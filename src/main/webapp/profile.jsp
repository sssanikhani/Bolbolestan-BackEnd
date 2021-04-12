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
<% HashMap<String, Object> data = (HashMap<String, Object>) request.getAttribute("student");%>
<body>
    <a href="/">Home</a><br>
    <img style="width: 100px; height: 100px; border: 1px solid; border-radius: 20px;" src="<%= data.get("img") %>">
    <ul>
        <li id="student_id">Student Id: <%= data.get("id")%></li>
        <li id="first_name">First Name: <%= data.get("name")%></li>
        <li id="last_name">Last Name: <%= data.get("secondName")%></li>
        <li id="birthdate">Birthdate: <%= data.get("birthDate")%></li>
        <li id="field">Field: <%= data.get("field") %></li>
        <li id="faculty">Faculty: <%= data.get("faculty") %></li>
        <li id="level">Level: <%= data.get("level") %></li>
        <li id="status">Status: <%= data.get("status") %></li>
        <li id="gpa">GPA: <%= data.get("gpa")%></li>
        <li id="tpu">Total Passed Units: <%= data.get("totalPassedUnits")%></li>
    </ul>
    <table>
        <tr>
            <th>Code</th>
            <th>Grade</th>
            <th>Term</th>
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
                    <td><%= entry.get("term") %></td>
                </tr>
        <%
            }
        %>
    </table>
</body>
</html>