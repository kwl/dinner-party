package dp.guests;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import dp.util.ActionUtil;

import java.io.IOException;

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

    //String eventName = req.getParameter("eventName");
    String event = req.getParameter("eventKey");
    String guestEmail = req.getParameter("guest");
    //TODO fix eventKey... where to get this?
    //Key eventKey = KeyFactory.createKey("Event", event);
    Key eventKey = KeyFactory.stringToKey(event);
    addGuest(eventKey, guestEmail);

    // Send email to invite guest
    emailInvite(guestEmail);
    //resp.sendRedirect("/event.jsp?eventKey="+event); // "...?" + event id 
    ActionUtil.gotoEvent(this, resp, event, req.getParameter("eventName"));
  }

  /**
   * Add a guest to the datastore with event as entity parent
   */
  public static void addGuest(Key eventKey, String guestEmail) {
    Entity guest = new Entity("Guest", eventKey); // event: parent
    guest.setProperty("user", guestEmail);
    // Set repeated property listing post IDs

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(guest);
  }

  private void emailInvite(String emailAddr) {
    try {
      UserService userService = UserServiceFactory.getUserService();
      User user = userService.getCurrentUser();

      String subject = user.getNickname() + " invites you to a dinner party!";
      String body = "Dear " + emailAddr + ",\n\n" + user.getEmail() + " invites you to the dinner party \"" + //get groupname +
        "\" in Dinner Party Planner. See the event at " +
        "http://august-storm-139422.appspot.com/event?eventName=" // + eventName
        ;

      ActionUtil.sendEmail(emailAddr, subject, body);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}