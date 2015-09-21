package org.heartbd2k.digester;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * SendEmail
 * Created by vincekyi on 6/25/15.
 */
public class Email {
    public static boolean send(String credentialFile, List<String> recipients, String subject, String body,
                               String attachment) throws IOException {
        // Get sender credentials
        Credentials sender = new Credentials(credentialFile);

        // Assuming you are sending email through relay.jangosmtp.net
        Properties props = new Properties();
        props.put("mail.smtp.auth", sender.getAuth());
        props.put("mail.smtp.starttls.enable", sender.getStartTLS());
        props.put("mail.smtp.host", sender.getHost());
        props.put("mail.smtp.port", sender.getPort());

        // Open authenticated session
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender.getUsername(), sender.getPassword());
            }
        });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(sender.getEmail()));

            // Set To: header field of the header.
            for (String recipient : recipients) {
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            }

            // Set Subject: header field
            message.setSubject(subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            // Add attachment if necessary
            if (attachment != null && !attachment.isEmpty()) {
                DataSource source = new FileDataSource(attachment);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(Paths.get(attachment).getFileName().toString()); // Name with filename
            }

            // Create a multipart message and add text message and attachment
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            // Send message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static class Credentials {
        private String email;
        private String auth;
        private String startTLS;
        private String host;
        private String port;
        private String username;
        private String password;

        public Credentials(String filename) throws IOException {
            Properties properties = new Properties();
            InputStream input = new FileInputStream(filename);
            properties.load(input);
            input.close();

            email = properties.getProperty("email");
            auth = properties.getProperty("auth");
            startTLS = properties.getProperty("startTLS");
            host = properties.getProperty("host");
            port = properties.getProperty("port");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
        }

        public String getEmail() {
            return email;
        }

        public String getAuth() {
            return auth;
        }

        public String getStartTLS() {
            return startTLS;
        }

        public String getHost() {
            return host;
        }

        public String getPort() {
            return port;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
