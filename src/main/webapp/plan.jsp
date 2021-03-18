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
    <%= request.getAttribute("planBody")%>
</body>
</html>