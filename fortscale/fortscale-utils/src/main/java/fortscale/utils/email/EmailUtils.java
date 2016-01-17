package fortscale.utils.email;

import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Amir Keren on 15/01/2016.
 */
public class EmailUtils {

    private static Logger logger = Logger.getLogger(EmailUtils.class);

    @Value("${smtp.username}")
    private String username;
    @Value("${smtp.password}")
    private String password;
    @Value("${smtp.host}")
    private String host;
    @Value("${smtp.port}")
    private String port;
    @Value("${smtp.auth}")
    private EmailAuth auth;

    private enum EmailAuth { tls, ssl, none }

    public void sendEmail(String from, String to, String cc, String bcc, String subject, String body,
            String[] attachFiles) throws MessagingException, IOException {
        logger.info("Preparing to send email");
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        switch (auth) {
        case tls: {
            props.put("mail.smtp.starttls.enable", "true");
            break;
        } case ssl: {
            props.put("mail.smtp.socketFactory.port", port);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            break;
        } case none: {
            break;
        }
        }
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        if (!StringUtils.isBlank(to)) {
            InternetAddress[] toAddresses = { new InternetAddress(to) };
            message.setRecipients(Message.RecipientType.TO, toAddresses);
        }
        if (!StringUtils.isBlank(cc)) {
            InternetAddress[] toAddresses = { new InternetAddress(to) };
            message.setRecipients(Message.RecipientType.CC, toAddresses);
        }
        if (!StringUtils.isBlank(bcc)) {
            InternetAddress[] toAddresses = { new InternetAddress(to) };
            message.setRecipients(Message.RecipientType.BCC, toAddresses);
        }
        message.setSubject(subject);
        message.setSentDate(new Date());
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        if (attachFiles != null && attachFiles.length > 0) {
            for (String filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(filePath);
                multipart.addBodyPart(attachPart);
            }
        }
        message.setContent(multipart);
        Transport.send(message);
        logger.info("Email sent");
    }

}