package fortscale.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Created by Amir Keren on 15/01/2016.
 */
public class MailSenderImpl implements MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String[] to, String[] cc, String[] bcc, String subject, String body) {
        SimpleMailMessage email = new SimpleMailMessage();
        if (to != null && to.length > 0) {
            email.setTo(to);
        }
        if (cc != null && cc.length > 0) {
            email.setCc(cc);
        }
        if (bcc != null && bcc.length > 0) {
            email.setBcc(bcc);
        }
        email.setSubject(subject);
        email.setText(body);
        mailSender.send(email);
    }

}