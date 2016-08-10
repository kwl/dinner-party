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
</head>

<body>

<%
	String name = request.getParameter("event");
%>

<p><h2><%=name%></h2></p>

<form action="TODO" method="post">
	<label for="invite">Invite guest (username@gmail.com):</label>
	<input type="text" name="guest" id="invite">
	<input type="hidden">
	<input type="submit" value="Share">
</form>


<p><a href="parties.jsp">Homepage</a></p>

</body>
</html>
<%-- //[END all]--%>
