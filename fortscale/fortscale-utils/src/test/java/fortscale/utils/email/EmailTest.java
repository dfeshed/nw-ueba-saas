package fortscale.utils.email;

import junitparams.JUnitParamsRunner;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Amir Keren on 17/01/16.
 */
@RunWith(JUnitParamsRunner.class)
public class EmailTest {

	private String username = "ak091283@gmail.com";
	private String password = "Kman2k16";
	private String host = "smtp.gmail.com";
	private String port = "587";
	private EmailAuth auth = EmailAuth.tls;

	private enum EmailAuth { tls, ssl, none }

	@Test
	public void test() {
		try {
			sendEmail("webadmin@fortscale.com", "amirk@fortscale.com", null, null, "test", "<html>test</html>",
					new String[] { "/home/amirk/Downloads/SSH_1453027890901.csv" });
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendEmail(String from, String to, String cc, String bcc, String subject, String body,
			String[] attachFiles) throws MessagingException, IOException {
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
	}

}