package fortscale.utils.email;

import javax.mail.MessagingException;

/**
 * Created by Amir Keren on 15/01/2016.
 */
public interface EmailService {

    void sendEmail(String[] to, String[] cc, String[] bcc, String subject, String body, boolean isHTML)
            throws MessagingException;

}