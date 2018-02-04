package fortscale.utils;

import fortscale.utils.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PresidioEncryptionUtils {

    private static final Logger logger = Logger.getLogger(PresidioEncryptionUtils.class);

    public String encrypt(String plainText) throws Exception {
        StringBuilder output = new StringBuilder();
        String[] cmd = {
                "/bin/sh",
                "-c",
                "echo -n " + plainText + " | openssl enc  -aes-256-cbc  -salt -pass pass:mj23 |  openssl enc -base64 -A"
        };
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine())!= null) {
                output.append(line).append("\n");
            }

        } catch (Exception e) {
            final String msg = "Can't encrypt text using openssl enc -aes-256-cbc!";
            logger.error(msg, e);
            throw new Exception(msg, e);
        }

        return output.toString();
    }

}
