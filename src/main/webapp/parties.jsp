<%-- //[START all]--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<html>
<head>
  <link type="text/css" rel="stylesheet" href="/stylesheets/main.css">
</head>

<body>

<p>Hello!</p>

<%
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

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
<%
  String event = "testEvent";
  String eventKey;
  //TODO get all of user's events, not just ones he's hosting
  //Query query = new Query("Event").setFilter(new Query.FilterPredicate("host", Query.FilterOperator.EQUAL, user.getEmail()));
  Query query = new Query("Event");
  query.setFilter(new Query.FilterPredicate("host", Query.FilterOperator.EQUAL, user.getEmail()));
  //query.setKeysOnly();
  Iterable<Entity> hostedEvents = datastore.prepare(query).asIterable(FetchOptions.Builder.withLimit(30));
  for (Entity hostEvent: hostedEvents) {
    event = (String) hostEvent.getProperty("name");
    eventKey = KeyFactory.keyToString(hostEvent.getKey());
%>
  <form action="event.jsp"+event method="get">
      <input type="hidden" name="eventKey" value=<%=eventKey%> />
      <input type="submit" name="eventName" placeholder="Event" value=<%=event%> />
  </form>

<%
    }
  } // end-else (user is logged in)
%>

</br></br></br>

<form action="/event" method="post">
  <label>Create new dinner party named:</label>
  <input type="text" name="eventName">
  <input type="submit" value="Create event">
</form>

<form action="/rsvp" method="post">
    <div><textarea name="content" rows="3" cols="60"></textarea></div>
    <div><input type="submit" value="RSVP"/></div>
</form>

</body>

</html>
<%-- //[END all]--%>
