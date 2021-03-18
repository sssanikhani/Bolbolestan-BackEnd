<%@ page import="java.util.HashMap" %>
<!DOCTYPE html>
<html lang="en">
    <%HashMap<String, Object> result = (HashMap<String, Object>) request.getAttribute("result"); %>
    <head>
        <meta charset="UTF-8">
        <title><%= result.get("short") %></title>
    </head>
    <body>
        <a href="/">Home</a>
        <h1> <%= result.get("status")%> </h1>
        <br>
        <%= result.get("message")%>
    </body>
</html>