<%@ page import="java.util.HashMap" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Error</title>
    </head>
    <%HashMap<String, Object> result = (HashMap<String, Object>) request.getAttribute("result"); %>
    <body>
        <a href="/">Home</a>
        <h1> <%= result.get("status")%> </h1>
        <br>
        <%= result.get("message")%>
    </body>
</html>