/**
 * Created by vincekyi on 6/25/15.
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendEmail {

    private static String username = "";
    private static String password = "";
    private static String email = "";
    private static String host = "";
    private static List<String> sendEmails;

    private static boolean loadCredentials(String filename){
        Properties prop = new Properties();

        InputStream input = null;
        try {
            input = new FileInputStream(filename);

            // load a properties file
            prop.load(input);

            // get the property value and print it out

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        username = prop.getProperty("username");
        password = prop.getProperty("password");
        email = prop.getProperty("email");
        host = prop.getProperty("host");
        sendEmails = new ArrayList<>();
        String tokens[] = prop.getProperty("sendEmails").split(";");
        for (String token : tokens) {
            sendEmails.add(token);
        }

        if(username.isEmpty() || password.isEmpty() || email.isEmpty() || sendEmails.isEmpty())
            return false;

        return true;

    }


    public static boolean send(String credentials, String output){

        if(!loadCredentials(credentials))
            return false;

        // Sender's email ID needs to be mentioned
        String from = email;


        // Assuming you are sending email through relay.jangosmtp.net

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "25");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            for (String sendEmail: sendEmails){
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(sendEmail));
            }

            String currentTime = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
            // Set Subject: header field
            message.setSubject("Weekly Digest: "+ currentTime);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("Hello,\n\nThis email contains a digest that was completed by the crawler at "+ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)+".");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = output;
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            return false;
        }
        return true;
    }

}
