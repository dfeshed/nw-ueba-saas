package fortscale.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.email.EmailConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.EmailService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Amir Keren on 15/01/2016.
 */
@Service("emailService")
public class EmailServiceImpl implements EmailService, InitializingBean {

    private static Logger logger = Logger.getLogger(EmailServiceImpl.class);

    private static final String CONFIGURATION_KEY = "system.email.settings";

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    private EmailConfiguration emailConfiguration;

	/**
     *
     * This method checks if email is configured in the system
     *
     * @return
     */
    @Override
    public boolean isEmailConfigured() {
        return emailConfiguration != null;
    }

	/**
     *
     * This method sends an email
     *
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param body
     * @param cidToFilePath
     * @param isHTML
     * @throws MessagingException
     * @throws IOException
	 */
    @Override
    public void sendEmail(String[] to, String[] cc, String[] bcc, String subject, String body, Map<String, String>
            cidToFilePath, boolean isHTML) throws MessagingException, IOException {
        logger.info("Preparing to send email");
        Session session = Session.getInstance(createProperties(),
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailConfiguration.getUser(), emailConfiguration.getPassword());
                }
            });
        Message message = new MimeMessage(session);
        addRecipients(to, cc, bcc, message);
        message.setSubject(subject);
        message.setSentDate(new Date());
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        if (isHTML) {
            messageBodyPart.setContent(body, "text/html");
        } else {
            messageBodyPart.setText(body);
        }
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        addAttachments(cidToFilePath, multipart);
        message.setContent(multipart);
        Transport.send(message);
        logger.info("Email sent");
    }

	/**
     *
     * This method adds attachments if such an argument is passed to the function
     *
     * @param cidToFilePath
     * @param multipart
     * @throws IOException
     * @throws MessagingException
     */
    private void addAttachments(Map<String, String> cidToFilePath, Multipart multipart)
            throws IOException, MessagingException {
        if (cidToFilePath != null && cidToFilePath.size() > 0) {
            for (Map.Entry<String, String> entry: cidToFilePath.entrySet()) {
                String cid = entry.getKey();
                String filePath = entry.getValue();
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(filePath);
                attachPart.setHeader("Content-ID", "<" + cid + ">");
                multipart.addBodyPart(attachPart);
            }
        }
    }

    /**
     *
     * This method adds recipients to the message
     *
     * @param to
     * @param cc
     * @param bcc
     * @param message
     * @throws MessagingException
     */
    private void addRecipients(String[] to, String[] cc, String[] bcc, Message message) throws MessagingException {
        if (to != null && to.length > 0) {
            for (String _to: to) {
                InternetAddress[] toAddresses = { new InternetAddress(_to) };
                message.setRecipients(Message.RecipientType.TO, toAddresses);
            }
        }
        if (cc != null && cc.length > 0) {
            for (String _cc: cc) {
                InternetAddress[] toAddresses = { new InternetAddress(_cc) };
                message.setRecipients(Message.RecipientType.CC, toAddresses);
            }
        }
        if (bcc != null && bcc.length > 0) {
            for (String _bcc: bcc) {
                InternetAddress[] toAddresses = { new InternetAddress(_bcc) };
                message.setRecipients(Message.RecipientType.BCC, toAddresses);
            }
        }
    }

    /**
     *
     * This method creates the properties necessary for sending emails
     *
     * @return
     */
    private Properties createProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        switch (emailConfiguration.getAuth()) {
            case "tls": {
                props.put("mail.smtp.starttls.enable", "true");
                break;
            } case "ssl": {
                props.put("mail.smtp.socketFactory.port", emailConfiguration.getPort());
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                break;
            } default: {
                break;
            }
        }
        props.put("mail.smtp.host", emailConfiguration.getHost());
        props.put("mail.smtp.port", emailConfiguration.getPort());
        return props;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        emailConfiguration = loadEmailConfiguration();
    }

    /**
     *
     * This method loads the email configuration from the database
     *
     * @throws IOException
     */
    private EmailConfiguration loadEmailConfiguration() {
        EmailConfiguration emailConfiguration = null;
        ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
                getApplicationConfigurationByKey(CONFIGURATION_KEY);
        if (applicationConfiguration != null) {
            String config = applicationConfiguration.getValue();
            try {
                emailConfiguration = new ObjectMapper().readValue(config, EmailConfiguration.class);
            } catch (Exception ex) {
                logger.error("failed to load email configuration from database - {}", ex);
            }
        }
        return emailConfiguration;
    }

}