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
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// for chat msgs
// import com.google.appengine.api.xmpp.JID;
// import com.google.appengine.api.xmpp.Message;
// import com.google.appengine.api.xmpp.MessageBuilder;
// import com.google.appengine.api.xmpp.SendResponse;
// import com.google.appengine.api.xmpp.XMPPService;
// import com.google.appengine.api.xmpp.XMPPServiceFactory;

// import java.util.logging.Logger;


/**
 * Form Handling Servlet
 */
public class AddGuestServlet extends HttpServlet {

  //private static final Logger log = Logger.getLogger(AddGuestServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    // begin chat section
    // JID jid = new JID(req.getParameter("guest"));
    // String msgBody = "Someone has sent you a gift on Example.com. To view: http://example.com/gifts/";
    // Message msg =
    //     new MessageBuilder()
    //         .withRecipientJids(jid)
    //         .withBody(msgBody)
    //         .build();

    // boolean messageSent = false;
    // XMPPService xmpp = XMPPServiceFactory.getXMPPService();
    // SendResponse status = xmpp.sendMessage(msg);
    // messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);

    // log.info("Message sent? " + messageSent);
    // end chat section

    String eventName = req.getParameter("eventName");
    String eventKeyStr = req.getParameter("eventKey");
    String guestEmail = req.getParameter("guest");
    Key eventKey = KeyFactory.stringToKey(eventKeyStr);
    addGuest(eventKey, guestEmail);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Entity event = datastore.get(eventKey);
      eventName = (String) event.getProperty("name");
    } catch (EntityNotFoundException e) {
      System.out.println("event entity not found on invite");
    }

    // Send email to invite guest
    emailInvite(guestEmail, eventKeyStr, eventName);
    //resp.sendRedirect("/event.jsp?eventKey="+event); // "...?" + event id 
    ActionUtil.gotoEvent(this, resp, eventKeyStr, eventName);
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
    // guest.setProperty("comment", "");
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
      String body = "Dear " + emailAddr + ",\n\n" + user.getEmail() + " invites you to the dinner party \"" + eventName +
        "\" in Potluck Planner. See the event at " +
        "http://august-storm-139422.appspot[dot]com/event.jsp?eventKey=" + eventKey + "&eventName=" + processEventName(eventName);

      ActionUtil.sendEmail(emailAddr, subject, body);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Replace spaces in event name with + for URL params
   */
  private String processEventName(String eventName) {
    String result = "";
    StringTokenizer splitter = new StringTokenizer(eventName);
    result += splitter.nextToken();
    while (splitter.hasMoreElements()) {
      result += "+";
      result += splitter.nextToken();
    }
    return result;
  }

}