package dp.guests;

import com.google.appengine.api.datastore.Key;

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
    addGuest(key, guestEmail);

    // Send email to invite guest
    //TODO redirect to right event page. some kind of event key? eventName stored in http request header?
    resp.sendRedirect("/event.jsp?"); // "...?" + event id 
  }

}