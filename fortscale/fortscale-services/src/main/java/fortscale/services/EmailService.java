package fortscale.services;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Amir Keren on 25/01/2016.
 */
public interface EmailService {

    boolean isEmailConfigured();
    void loadEmailConfiguration();
    boolean sendEmail(String[] to, String[] cc, String[] bcc, String subject, String body,
                   Map<String, String> cidToFilePath, boolean isHTML) throws MessagingException, IOException;

}