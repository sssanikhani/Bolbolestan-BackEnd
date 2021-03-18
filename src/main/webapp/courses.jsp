<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Courses</title>
    <style>
        .course_table {
            width: 100%;
            text-align: center;
        }
        .search_form {
            text-align: center;
        }
    </style>
</head>
<body>
<% HashMap<String , Object> student = (HashMap<String, Object>) request.getAttribute("student");%>
<ul>
    <li>
        <a href="/">Home</a>
    </li>
    <li id="code">Student Id: <%= student.get("id")%> </li>
    <li id="units">Total Selected Units: <%= student.get("numberChosenUnits")%></li>
</ul>
<br>
<% ArrayList<HashMap<String, Object>> selectedCourses = (ArrayList<HashMap<String, Object>>) student.get("chosenOfferings");%>
<table>
    <tr>
        <th>Code</th>
        <th>Class Code</th>
        <th>Name</th>
        <th>Units</th>
        <th></th>
    </tr>
    <% for (HashMap<String, Object> entry : selectedCourses){ %>
        <tr>
            <td> <%= entry.get("code")%> </td>
            <td> <%= entry.get("classCode")%> </td>
            <td> <%= entry.get("name")%> </td>
            <td> <%= entry.get("units")%> </td>
            <td>
                <form action="courses" method="POST" >
                    <input type="hidden" name="code" value= <%= entry.get("code")%>>
                    <input type="hidden" name="classCode" value= <%= entry.get("classCode")%>>
                    <button type="submit" name="action" value="remove" >Remove</button>
                </form>
            </td>
        </tr>
    <% } %>
</table>
<br>

<form action="courses" method="POST">
    <button type="submit" name="action" value="submit">Submit Plan</button>
    <button type="submit" name="action" value="reset">Reset</button>
    <button type="submit" name="action" value="plan">Plan</button>
</form>


<br>

<form class="search_form" action="courses" method="POST">
    <label>Search:</label>
    <label>
        <input type="text" name="searchBox" value= <%=request.getAttribute("searchBox")%>>
    </label>
    <button type="submit" name="action" value="search">Search</button>
    <button type="submit" name="action" value="clear">Clear Search</button>
</form>


<% ArrayList<HashMap<String, Object>> courses = (ArrayList<HashMap<String, Object>>) request.getAttribute("courses"); %>
<br>
<table class="course_table">
    <tr>
        <th>Code</th>
        <th>Class Code</th>
        <th>Name</th>
        <th>Units</th>
        <th>Signed Up</th>
        <th>Capacity</th>
        <th>Type</th>
        <th>Days</th>
        <th>Time</th>
        <th>Exam Start</th>
        <th>Exam End</th>
        <th>Prerequisites</th>
        <th></th>
    </tr>
    <% for (HashMap<String, Object> entry : courses) { %>
        <tr>
            <td> <%= entry.get("code")%> </td>
            <td> <%= entry.get("classCode")%> </td>
            <td> <%= entry.get("name")%> </td>
            <td> <%= entry.get("units")%> </td>
            <td> <%= entry.get("numRegisteredStudents")%> </td>
            <td> <%= entry.get("capacity")%> </td>
            <td> <%= entry.get("type")%> </td>
            <%
                HashMap<String, Object> classTimeData = (HashMap<String, Object>) entry.get("classTime");
                HashMap<String, String> examTimeData = (HashMap<String, String>) entry.get("examTime");
            %>
            <td> <%= String.join("|", (ArrayList<String>) classTimeData.get("days"))%> </td>
            <td><%= (String) classTimeData.get("time") %></td>
            <td><%= examTimeData.get("start") %></td>
            <td><%= examTimeData.get("end") %></td>
            <td><%= String.join("|", (ArrayList<String>) entry.get("prerequisites")) %></td>
            <td>
                <form action="courses" method="POST" >
                    <input id="form_action" type="hidden" name="action" value="add">
                    <input id="form_code" type="hidden" name="code" value= <%= entry.get("code")%> >
                    <input id="form_class_code" type="hidden" name="classCode" value= <%= entry.get("classCode")%> >
                    <button type="submit">Add</button>
                </form>
            </td>
        </tr>
    <% } %>
</table>
</body>
</html>