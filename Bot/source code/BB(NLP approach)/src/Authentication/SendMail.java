/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 *
 * @author Kiner Shah
 */
public class SendMail {
    private final String username = "kdm.noreply@gmail.com";
    private final String password = "iAMu^&*(";
    /*
    * @param OTP otp generated
    * @param To receiver's mail address
    */
    public void sendOTP(String OTP, String To) {
        //OTP otp = new OTP();
	Properties props = new Properties();
	props.put("mail.smtp.auth", "true");
	props.put("mail.smtp.starttls.enable", "true");
	props.put("mail.smtp.host", "smtp.gmail.com");
	props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
		}
            });

	try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("kdm.noreply@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(To));
            //message.setSubject("Testing Subject");
            message.setText("Your OTP is: " + OTP);
            Transport.send(message);
            System.out.println("Done");
	} 
        catch (MessagingException e) {
            throw new RuntimeException(e);
	}
    }
    /*public static void main(String[] args) {
        SendMail sm = new SendMail();
        sm.sendOTP("56AD24BBV", "darshan.ps@somaiya.edu");
    }*/
}
