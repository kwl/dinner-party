<%-- //[START all]--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>


<html>
<head>
	<link type="text/css" rel="stylesheet" href="/stylesheets/main.css">
</head>

<body>

<p>Hello!</p>

<%
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
%>
	Please <!-- {@code <a href="<%=userService.createLoginURL("/newlogin.jsp")%>">log in</a>>} --><a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a>
<%  } else {
    User user = userService.getCurrentUser();
%>
	Welcome, <%= user.getNickname() %>! </br>
	<!-- {@code (<a href="<%=userService.createLogoutURL("/")%>">log out</a>>)} -->
	<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Sign out</a>

	<h3>Your events</h3>
<%	String event = "testEvent"; 
	//TODO get actual user's events, loop through and display each
%>
	<form action="event.jsp?event="+event>
		<input type="submit" name="event" placeholder="Event" value=<%=event%> />
	</form>
<%
	//TODO (kathyli) Display events as links to individual pages
    }
%>

</br></br></br>

<form action="/event" method="post">
	<label>Create new dinner party named:</label>
	<input type="text" name="event">
	<input type="submit" value="Create event">
</form>

<form action="/rsvp" method="post">
    <div><textarea name="content" rows="3" cols="60"></textarea></div>
    <div><input type="submit" value="RSVP"/></div>
</form>

</body>

</html>
<%-- //[END all]--%>
