package dp.event;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import dp.guests.AddGuestServlet;
import dp.util.ActionUtil;

import java.io.BufferedReader;
import java.io.IOException;

import java.lang.StringBuilder;

import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Form Handling Servlet
 */
public class NewEventServlet extends HttpServlet {

  // Create a new event with given name
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();  // Find whom user is

    // Put new event into datastore
    Entity event = new Entity("Event");
    String eventName = req.getParameter("eventName");
    event.setProperty("name", eventName);
    event.setProperty("host", user.getEmail());
    event.setProperty("description", req.getParameter("description"));
    ArrayList<String> attendees = new ArrayList<String>();
    attendees.add(user.getEmail()); // add host to attending
    event.setProperty("attendees", attendees);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(event);

    // Check if Person exists, create if not, add event property
    Key eventKey = event.getKey();
    Transaction txn = datastore.beginTransaction();
    Entity person;
    Key personKey;
    try {
      personKey = KeyFactory.createKey("Person", user.getEmail());
      person = datastore.get(personKey);
      ArrayList<Key> events = (ArrayList<Key>) person.getProperty("events");
      events.add(eventKey);
      person.setProperty("events", events);
      datastore.put(txn, person);
      txn.commit();
    } catch (EntityNotFoundException e) {
      person = new Entity("Person", user.getEmail());
      ArrayList<Key> events = new ArrayList<Key>();
      events.add(eventKey);
      person.setProperty("events", events);
      datastore.put(txn, person);
      txn.commit();
    } finally {
      if (txn.isActive()) txn.rollback();
    }

    // Put host as Guest entity for this Event
    Entity host = new Entity("Guest", eventKey); // event: parent
    host.setProperty("user", user.getEmail());
    host.setProperty("bringing", "");
    datastore.put(host);

    //resp.sendRedirect("/event.jsp?eventName="+eventName); //TODO update this
    ActionUtil.gotoEvent(this, resp, KeyFactory.keyToString(eventKey), eventName);
  }


  /**
   * Utility method to get text from a form's POST request
   */
  public String getBody(HttpServletRequest request) throws IOException {
    // Read from request
    StringBuilder buffer = new StringBuilder();
    BufferedReader reader = request.getReader();
    String line;
    while ((line = reader.readLine()) != null) {
        buffer.append(line);
    }
    return buffer.toString();
  }

} // end class

//[END all]
