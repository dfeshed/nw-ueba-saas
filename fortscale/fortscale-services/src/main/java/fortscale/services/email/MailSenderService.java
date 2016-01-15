package fortscale.services.email;

/**
 * Created by Amir Keren on 15/01/2016.
 */
public interface MailSenderService {

    void sendEmail(String[] to, String[] cc, String[] bcc, String subject, String body);

}