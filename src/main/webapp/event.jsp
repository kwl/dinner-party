<%-- //[START all]--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>


<html>
<head>
  <link type="text/css" rel="stylesheet" href="/stylesheets/main.css">
</head>

<body>

<%
  //String name = request.getParameter("eventName");
  String event = request.getParameter("eventKey");
  System.out.println("event.jsp event param: " + event);
  String name;
  if (event != null) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key eventKey = KeyFactory.stringToKey(event);
    name = (String) datastore.get(eventKey).getProperty("name");
  } else {
    name = request.getParameter("eventName");
  }
%>

<p><h2><%=name%></h2></p>
<!-- <form action="/event" method="get">
  <input type="hidden" name="eventKey" 
</form> -->

<form action="/invite" method="post">
  <label for="invite">Invite guest (username@gmail.com):</label>
  <input type="text" name="guest" id="invite">
  <input type="hidden" name="eventName" value=<%=name%>>
  <input type="hidden" name="eventKey" value=<%=event%>>
  <input type="submit" value="Share">
</form>


<p><h4>Guests</h4></p>
<!-- TODO list guests, from datastore -->
<%
  
%>

<form action="/rsvp" method="post">
  <label>What are you bringing?</label>
  <input type="text" name="bring">
  <input type="hidden" name="eventName" value=<%=name%>>
  <input type="hidden" name="eventKey" value=<%=event%>>
  <input type="submit" value="Go">
</form>

<p><a href="parties.jsp">Homepage</a></p>

</body>
</html>
<%-- //[END all]--%>
