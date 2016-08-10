package dp.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utilities for the dinner party planner app
 */
public class ActionUtil {

  public static void sendEmail(String receiveAddr, String subject, String body) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress("kathyli@google.com", "Dinner Party Planner"));
      msg.addRecipient(Message.RecipientType.TO, 
        new InternetAddress(receiveAddr, receiveAddr));
      msg.setSubject(subject);

      body += "\n\n\nOK";
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