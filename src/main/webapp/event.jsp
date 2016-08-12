<%-- //[START all]--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="java.util.ArrayList" %>


<html>
<head>
  <link type="text/css" rel="stylesheet" href="/stylesheets/main.css">
</head>

<body>

<%
  // Set up for accessing data
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  String eventKeyStr = request.getParameter("eventKey");
  String name = request.getParameter("eventName");
  Key eventKey = KeyFactory.stringToKey(eventKeyStr);
  //String name = (String) datastore.get(eventKey).getProperty("name");
%>

<p><h2><%=name%></h2></p>
<!-- <form action="/event" method="get">
  <input type="hidden" name="eventKey" 
</form> -->

<form action="/invite" method="post">
  <label for="invite">Invite guest (username@gmail.com):</label>
  <input type="text" name="guest" id="invite">
  <input type="hidden" name="eventName" value=<%=name%>>
  <input type="hidden" name="eventKey" value=<%=eventKeyStr%>>
  <input type="submit" value="Share">
</form>


<p>
<h4>Host</h4>
<%= datastore.get(eventKey).getProperty("host") %>
</p>

<p><h4>Guests</h4></p>
<!-- TODO list guests, from datastore -->
<%
  @SuppressWarnings("unchecked") // Cast can't verify generic type
  ArrayList<String> attendees = (ArrayList<String>) datastore.get(eventKey).getProperty("attendees");
  if (attendees == null) {
    System.out.println("     NULL attendees!!\n");
  }
  String hostEmail = (String) datastore.get(eventKey).getProperty("host");
  for (String email: attendees) {
    if (! email.equals(hostEmail)) {
%>
    <%=email%></br>
<%
    } // end-if email comparison
  } // end-for all guest emails
%>

</br></br></br>

<form action="/rsvp" method="post">
  <label>What are you bringing?</label>
  <input type="text" name="bring">
  <input type="hidden" name="eventName" value=<%=name%>>
  <input type="hidden" name="eventKey" value=<%=eventKeyStr%>>
  <input type="submit" value="Go">
</form>

<p><a href="parties.jsp">Homepage</a></p>

</body>
</html>
<%-- //[END all]--%>
