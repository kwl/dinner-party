<%-- //[START all]--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>


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

  UserService userService = UserServiceFactory.getUserService();
  if (!userService.isUserLoggedIn()) {
%>
  <!-- Please <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> -->
  <p>Please go to <a href="parties.jsp">homepage</a> and log in.</p>
<%
  } else { 
%>

<p><h2><%=name%></h2></p>
<!-- <form action="/event" method="get">
  <input type="hidden" name="eventKey" 
</form> -->

<!-- Invite guests to event via email -->
<form action="/invite" method="post">
  <label for="invite">Invite guest (username@gmail.com):</label>
  <input type="text" name="guest" id="invite">
  <input type="hidden" name="eventKey" value=<%=eventKeyStr%>>
  <input type="hidden" name="eventName" value=<%=name%>>
  <input type="submit" value="Share">
</form>


<p>
<h4>Host</h4>
<%= datastore.get(eventKey).getProperty("host") %>
</p>

<!-- List guests and what they're bringing to event in table -->
<p><h4>Guests</h4></p>
<table border="1">
<th>Email</th>
<th>Bringing</th>
<%
  @SuppressWarnings("unchecked") // Cast can't verify generic type
  ArrayList<String> attendees = (ArrayList<String>) datastore.get(eventKey).getProperty("attendees");
  if (attendees == null) {
    System.out.println("     NULL attendees!!\n");
  }
  String hostEmail = (String) datastore.get(eventKey).getProperty("host");
  Query query;
  Entity guest;
  String bringing = "";
  for (String email: attendees) {
    if (! email.equals(hostEmail)) {
      query = new Query("Guest").setAncestor(eventKey).setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, email));
      guest = datastore.prepare(query).asSingleEntity();
      if (guest == null) {System.out.println("\n   null guest");}
      bringing = (String) guest.getProperty("bringing");
      if (bringing == null) {
        bringing = "";
        System.out.println("\n   Bringing was null");
      }
%>
    <tr>
    <td><%=email%></td>
    <td><%=bringing%></td>
    </tr>
<%
    } // end-if email comparison
  } // end-for all guest emails
%>
</table>

</br></br></br>

<!-- Submit what signed-in user is bringing to event -->
<form action="/rsvp" method="post">
  <label>What are you bringing?</label>
  <input type="text" name="bring">
  <input type="hidden" name="eventKey" value=<%=eventKeyStr%>>
  <input type="hidden" name="eventName" value=<%=name%>>
  <input type="submit" value="Go">
</form>

<p><a href="parties.jsp">Homepage</a></p>

<% 
} // end-else, content for logged-in user
%>

</body>
</html>
<%-- //[END all]--%>
