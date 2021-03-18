<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home Page</title>
</head>
<body>
    <ul>
        <% String id = (String) request.getAttribute("studentId");%>
        <li id="std_id">Student Id: <%= id%> </li>
        <li>
            <a href="courses">Select Courses</a>
        </li>
        <li>
            <a href="plan">Submited plan</a>
        </li>
        <li>
            <a href="profile">Profile</a>
        </li>
        <li>
            <a href="logout">Log Out</a>
        </li>
    </ul>
</body>
</html>