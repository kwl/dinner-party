package dp.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * Utilities for the dinner party planner app
 */
public class ActionUtil {

  public static void gotoEvent(HttpServlet context, HttpServletResponse resp, String event, String eventName) throws IOException {
    resp.sendRedirect("/event.jsp?eventKey=" + event + "&eventName=" + eventName);
  }

  public static void sendEmail(String receiveAddr, String subject, String body) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress("kathyli@google.com", "Dinner Party Planner"));
      //msg.setFrom(new InternetAddress("august-storm-139422.appspot.gserviceaccount.com", "Dinner Party Planner"));
      msg.addRecipient(Message.RecipientType.TO, 
        new InternetAddress(receiveAddr, receiveAddr));
      msg.setSubject(subject);

      body += "\n\nPotluck Planner is an app to help you organize";
      body += " get togethers with people you know. Create events";
      body += ", invite friends, RSVP and see everyone's comments!";
      System.out.println("ActionUtil body: " + body);
      msg.setText(body);
      Transport.send(msg);
      System.out.println("\nSent email w/ subject " + subject + " to recipient " + receiveAddr + "\n");
    } catch (AddressException e) {
      e.printStackTrace();
    } catch (MessagingException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

}