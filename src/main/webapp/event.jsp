<%-- //[START all]--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.mail.Message" %>
<%@ page import="javax.mail.MessagingException" %>
<%@ page import="javax.mail.Session" %>
<%@ page import="javax.mail.Transport" %>
<%@ page import="javax.mail.internet.AddressException" %>
<%@ page import="javax.mail.internet.InternetAddress" %>
<%@ page import="javax.mail.internet.MimeMessage" %>


<html>
<head>
	<link type="text/css" rel="stylesheet" href="/stylesheets/main.css">
</head>

<body>

<%
	String name = request.getParameter("eventName");
%>

<p><h2><%=name%></h2></p>

<form action="/invite" method="post">
	<label for="invite">Invite guest (username@gmail.com):</label>
	<input type="text" name="guest" id="invite">
	<input type="hidden">
	<input type="submit" value="Share">
</form>


<p><a href="parties.jsp">Homepage</a></p>

</body>
</html>
<%-- //[END all]--%>
