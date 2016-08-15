<%-- //[START all]--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.EntityNotFoundException" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.ArrayList" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<html>
<head>
  <link type="text/css" rel="stylesheet" href="/stylesheets/main.css">
</head>

<body>

<p><h2>Potluck Planner</h2></p>
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

  <!-- Show signed-in user's events as clickable list -->
  <h3>Your events</h3>
<%
  String event = "testEvent";
  String eventKey;
  //TODO get all of user's events, not just ones he's hosting
  Key personKey = KeyFactory.createKey("Person", user.getEmail());
  try {
    Entity person = datastore.get(personKey);
    ArrayList<Key> events = (ArrayList<Key>) person.getProperty("events");
    for (Key ek: events) {
      eventKey = KeyFactory.keyToString(ek);
      event = (String) datastore.get(ek).getProperty("name");
%>
  <form action="event.jsp"+event method="get">
      <input type="hidden" name="eventKey" value=<%=eventKey%> />
      <input type="submit" name="eventName" value="<%=event%>" />
  </form>
<%
    } // end-for looping through Person's events
  } catch (EntityNotFoundException e) {
    System.out.println("Signed-in user's Person entity not found");
    e.printStackTrace();
  }
%>

</br></br>

<form action="/event" method="post">
  <label>Create new dinner party named:</label>
  <input type="text" name="eventName">
  </br><label>Optional description:</label>
  <div><textarea name="description" rows="3" cols="60"></textarea></div>
  <input type="submit" value="Create event">
</form>

<%
  } // end-else (user is logged in)
%>

</body>

</html>
<%-- //[END all]--%>
