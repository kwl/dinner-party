package dp.guests;

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

import dp.util.ActionUtil;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Form Handling Servlet
 */
public class AddGuestServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    String eventName = req.getParameter("eventName");
    String event = req.getParameter("eventKey");
    String guestEmail = req.getParameter("guest");
    //TODO fix eventKey... where to get this?
    //Key eventKey = KeyFactory.createKey("Event", event);
    Key eventKey = KeyFactory.stringToKey(event);
    addGuest(eventKey, guestEmail);

    // Send email to invite guest
    emailInvite(guestEmail, event, eventName);
    //resp.sendRedirect("/event.jsp?eventKey="+event); // "...?" + event id 
    ActionUtil.gotoEvent(this, resp, event, req.getParameter("eventName"));
  }

  /**
   * Add a guest to the datastore with event as entity parent
   * Also check if Person exists and create if not, with event
   * key as property
   */
  public static void addGuest(Key eventKey, String guestEmail) {
    Entity guest = new Entity("Guest", eventKey); // event: parent
    guest.setProperty("user", guestEmail);
    guest.setProperty("bringing", "");
    // Set repeated property listing post IDs

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(guest);

    // Add guest to repeated prop "attendees" in Event entity
    // TODO make this a transaction
    try {
      Entity event = datastore.get(eventKey);
      @SuppressWarnings("unchecked")
      ArrayList<String> attendees = (ArrayList<String>) event.getProperty("attendees");
      attendees.add(guestEmail);
      event.setProperty("attendees", attendees);
      datastore.put(event);
    } catch (EntityNotFoundException e) {
      System.out.println("AddGuestServlet event entity not found");
      e.printStackTrace();
    }

    // Check if Person exists, create if not, add event property
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    Transaction txn = datastore.beginTransaction();
    Entity person;
    Key personKey;
    try {
      personKey = KeyFactory.createKey("Person", guestEmail);
      person = datastore.get(personKey);
      ArrayList<Key> events = (ArrayList<Key>) person.getProperty("events");
      events.add(eventKey);
      person.setProperty("events", events);
      datastore.put(txn, person);
      txn.commit();
    } catch (EntityNotFoundException e) {
      person = new Entity("Person", guestEmail);
      ArrayList<Key> events = new ArrayList<Key>();
      events.add(eventKey);
      person.setProperty("events", events);
      datastore.put(txn, person);
      txn.commit();
    } finally {
      if (txn.isActive()) txn.rollback();
    }
  }

  private void emailInvite(String emailAddr, String eventKey, String eventName) {
    try {
      UserService userService = UserServiceFactory.getUserService();
      User user = userService.getCurrentUser();

      String subject = user.getNickname() + " invites you to a dinner party!";
      String body = "Dear " + emailAddr + ",\n\n" + user.getEmail() + " invites you to the dinner party \"" + //get groupname +
        "\" in Dinner Party Planner. See the event at " +
        "http://august-storm-139422.appspot.com/event.jsp?eventKey=" + eventKey + "&eventName=" + eventName;

      ActionUtil.sendEmail(emailAddr, subject, body);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}