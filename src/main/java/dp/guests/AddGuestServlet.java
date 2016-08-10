package dp.guests;

import com.google.appengine.api.datastore.Key;
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
    Key key = null; //TODO fix this!!
    String guestEmail = req.getParameter("guest");
    // addGuest(key, guestEmail);

    // Send email to invite guest
    emailInvite(guestEmail);
    //TODO redirect to right event page. some kind of event key? eventName stored in http request header?
    resp.sendRedirect("/event.jsp?"); // "...?" + event id 
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