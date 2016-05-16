package fortscale.domain.ad.dao;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by rafis on 16/05/16.
 */
public interface ActiveDirectoryResultHandler {
    void handleAttributes(BufferedWriter fileWriter, Attributes attributes) throws NamingException,IOException;
}
