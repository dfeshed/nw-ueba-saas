package fortscale.services.impl;

import fortscale.services.ApplicationConfigurationService;
import fortscale.services.EmailService;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
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

    private static final String CONFIGURATION_NAMESPACE = "system.email";
    private static final String USERNAME_KEY = CONFIGURATION_NAMESPACE + ".username";
    private static final String PASSWORD_KEY = CONFIGURATION_NAMESPACE + ".password";
    private static final String PORT_KEY = CONFIGURATION_NAMESPACE + ".port";
    private static final String HOST_KEY = CONFIGURATION_NAMESPACE + ".host";
    private static final String AUTH_KEY = CONFIGURATION_NAMESPACE + ".auth";

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    private String host;
    private String username;
    private String password;
    private String auth;
    private String port;

	/**
     *
     * This method checks if email is configured in the system
     *
     * @return
     */
    @Override
    public boolean isEmailConfigured() {
        return !StringUtils.isBlank(host);
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
                    return new PasswordAuthentication(username, password);
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
        switch (auth) {
            case "tls": {
                props.put("mail.smtp.starttls.enable", "true");
                break;
            } case "ssl": {
                props.put("mail.smtp.socketFactory.port", port);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                break;
            } default: {
                break;
            }
        }
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        return props;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadEmailConfiguration();
    }

    /**
     *
     * This method loads the email configuration from the database
     *
     * @throws IOException
     */
    private void loadEmailConfiguration() {
        Map<String, String> applicationConfiguration = applicationConfigurationService.
                getApplicationConfigurationByNamespace(CONFIGURATION_NAMESPACE);
        if (applicationConfiguration != null && !applicationConfiguration.isEmpty()) {
            username = applicationConfiguration.get(USERNAME_KEY);
            password = applicationConfiguration.get(PASSWORD_KEY);
            host = applicationConfiguration.get(HOST_KEY);
            port = applicationConfiguration.get(PORT_KEY);
            auth = applicationConfiguration.get(AUTH_KEY);
        }
    }

}