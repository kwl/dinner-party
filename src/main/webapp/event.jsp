<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.EntityNotFoundException" %>
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
try { // Gets full event name; fixes first-word bug
  name = (String) datastore.get(eventKey).getProperty("name");
} catch (EntityNotFoundException e) {
  e.printStackTrace();
}

UserService userService = UserServiceFactory.getUserService();
if (!userService.isUserLoggedIn()) {
%>
  <!-- Please <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a> -->
  <p>Please go to <a href="parties.jsp">homepage</a> and log in.</p>
<%
} else { // User is logged in
  User user = userService.getCurrentUser();
  String userEmail = user.getEmail();
  // Check for user being event host or guest to display event
  Entity event;
  try {
    event = datastore.get(eventKey);
  } catch (EntityNotFoundException e) {
    event = null;
    System.out.println("Entity for given event not found");
    e.printStackTrace();
  }
  boolean isGuest = false;
  ArrayList<String> attendees = (ArrayList<String>) event.getProperty("attendees");
  for (String email: attendees) {
    if (email.equals(userEmail)) {
      isGuest = true;
      break;
    }
  }
  if (! (((String)event.getProperty("host")).equals(userEmail) ||
    isGuest)) { // User not event guest or host, can't RSVP
%>
<p> Sorry, you do not have permission to see this event. </p>
<%
  } // end-if no permissions for event
  else {
%>

<!-- Show event name and description at top of page -->
<p><h2><%=name%></h2></p>
<%
  String description = "";
  try {
    description = (String) datastore.get(eventKey).getProperty("description");
  } catch (EntityNotFoundException e) {
    e.printStackTrace();
  }
%>
<p>Details: <%=description%></p>
<p><hr width="50%" align="left" noshade></p>

<!-- Invite guests to event via email -->
<form action="/invite" method="post">
  <label for="invite">Invite guest (username@gmail.com):</label>
  <input type="text" name="guest" id="invite">
  <input type="hidden" name="eventKey" value=<%=eventKeyStr%>>
  <input type="hidden" name="eventName" value=<%=name%>>
  <input type="submit" value="Share">
</form>


<div>
<h4>Host</h4>
<%= datastore.get(eventKey).getProperty("host") %>
<%
  Entity host = datastore.prepare(new Query("Guest", eventKey).setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, ((String)event.getProperty("host"))))).asSingleEntity();
  String hostBringing = (String) host.getProperty("bringing");
  String hostComment = (String) host.getProperty("comment");
  if (hostBringing != null && hostBringing.length()>0) {
%>
is providing <%=hostBringing%>
<%
  }
  if (hostComment != null && hostComment.length()>0) {
%>
<br/><br/>Note: <%=hostComment%>
<%}%>
</div>

<!-- List guests and what they will bring to event in table -->
<p><h4>Guests</h4></p>
<table class="myTable">
<th>Email</th>
<th>Bringing</th>
<th>Comments</th>
<%
  //@SuppressWarnings("unchecked") // Cast can't verify generic type
  //ArrayList<String> attendees = (ArrayList<String>) datastore.get(eventKey).getProperty("attendees");
  if (attendees == null) {
    System.out.println("     NULL attendees!!\n");
  }
  String hostEmail = (String) datastore.get(eventKey).getProperty("host");
  Query query;
  Entity guest;
  String bringing = "";
  String comment = "";
  for (String email: attendees) {
    if (! email.equals(hostEmail)) {
      query = new Query("Guest").setAncestor(eventKey).setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, email));
      Iterable<Entity> guests = datastore.prepare(query).asIterable();
      guest = guests.iterator().next();
      // guest = datastore.prepare(query).asSingleEntity();
      bringing = (String) guest.getProperty("bringing");
      if (bringing == null) {
        bringing = "";
        System.out.println("\n   Bringing was null");
      }
      comment = (String) guest.getProperty("comment");
      if (comment == null) {
        comment = "";
      }
%>
    <tr>
    <td><%=email%></td>
    <td><%=bringing%></td>
    <td><%=comment%></td>
    </tr>
<%
    } // end-if email comparison
  } // end-for all guest emails
%>
</table>

<br/><br/><br/>

<!-- Submit what signed-in user is bringing to event -->
<form action="/rsvp" method="post">
  <label>What are you bringing?</label>
  <input type="text" name="bring">
  <br/><label>Any comments?</label>
  <div><textarea name="comment" rows="3" cols="60"></textarea></div>
  <input type="hidden" name="eventKey" value=<%=eventKeyStr%>>
  <input type="hidden" name="eventName" value=<%=name%>>
  <input type="submit" value="Go">
</form>

<p><a href="parties.jsp">Homepage</a></p>

<% 
  } // end-else, content for invited user
} // end-else, content for logged-in user
%>

</body>
</html>
