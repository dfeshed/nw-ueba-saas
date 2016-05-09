package fortscale.services.impl;

import fortscale.services.ApplicationConfigurationService;
import fortscale.services.EmailService;
import fortscale.utils.EncryptionUtils;
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

    public static final String CONFIGURATION_NAMESPACE = "system.email";
    public static final String FROM_KEY = CONFIGURATION_NAMESPACE + ".from";
    public static final String USERNAME_KEY = CONFIGURATION_NAMESPACE + ".username";
    public static final String PASSWORD_KEY = CONFIGURATION_NAMESPACE + ".password";
    public static final String PORT_KEY = CONFIGURATION_NAMESPACE + ".port";
    public static final String HOST_KEY = CONFIGURATION_NAMESPACE + ".host";
    public static final String AUTH_KEY = CONFIGURATION_NAMESPACE + ".auth";

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    private String host;
    private String username;
    private String password;
    private String auth;
    private String port;
    private String from;

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
    public boolean sendEmail(String[] to, String[] cc, String[] bcc, String subject, String body, Map<String, String>
            cidToFilePath, boolean isHTML) throws MessagingException, IOException {
        //sanity check
        if (auth == null) {
            logger.error("Email server not configured");
            return false;
        }
        logger.info("Preparing to send email");
        Session session;
        if (auth.equals("none")) {
            session = Session.getInstance(createProperties());
        } else {
            session = Session.getInstance(createProperties(), new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
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
        return true;
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
            InternetAddress[] toAddresses = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                toAddresses[i] = new InternetAddress(to[i]);
            }
            message.setRecipients(Message.RecipientType.TO, toAddresses);
        }
        if (cc != null && cc.length > 0) {
            InternetAddress[] ccAddresses = new InternetAddress[cc.length];
            for (int i = 0; i < cc.length; i++) {
                ccAddresses[i] = new InternetAddress(to[i]);
            }
            message.setRecipients(Message.RecipientType.CC, ccAddresses);
        }
        if (bcc != null && bcc.length > 0) {
            InternetAddress[] bccAddresses = new InternetAddress[bcc.length];
            for (int i = 0; i < bcc.length; i++) {
                bccAddresses[i] = new InternetAddress(bcc[i]);
            }
            message.setRecipients(Message.RecipientType.BCC, bccAddresses);
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
        switch (auth) {
            case "tls": {
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                break;
            } case "ssl": {
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.socketFactory.port", port);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                break;
            } case "none": {
                props.put("mail.smtp.auth", "false");
                break;
            } default: {
                throw new UnsupportedOperationException();
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
     * This method is a helper method used by the two methods below to set the config values
     *
     * @param applicationConfiguration
     */
    private void setConfigurationValues(Map<String, String> applicationConfiguration) {
        username = applicationConfiguration.get(USERNAME_KEY);
        String pass = applicationConfiguration.get(PASSWORD_KEY);
        try {
            password = EncryptionUtils.decrypt(pass);
        } catch (Exception ex) {
            logger.warn("Failed to decrypt password, using password as is");
            password = pass;
        }
        host = applicationConfiguration.get(HOST_KEY);
        port = applicationConfiguration.get(PORT_KEY);
        auth = applicationConfiguration.get(AUTH_KEY);
        from = applicationConfiguration.get(FROM_KEY);
    }

    /**
     *
     * This method loads the email configuration from the database
     *
     * @throws IOException
     */
    @Override
    public void loadEmailConfiguration() {
        Map<String, String> applicationConfiguration = applicationConfigurationService.
                getApplicationConfigurationByNamespace(CONFIGURATION_NAMESPACE);
        if (applicationConfiguration != null && !applicationConfiguration.isEmpty()) {
            setConfigurationValues(applicationConfiguration);
        }
    }

    /**
     *
     * This is a method for testing purposes, it loads the configuration from an external source
     *
     * @param applicationConfiguration
     */
    public void loadEmailConfiguration(Map<String, String> applicationConfiguration) {
        if (applicationConfiguration != null && !applicationConfiguration.isEmpty()) {
            setConfigurationValues(applicationConfiguration);
        }
    }

}