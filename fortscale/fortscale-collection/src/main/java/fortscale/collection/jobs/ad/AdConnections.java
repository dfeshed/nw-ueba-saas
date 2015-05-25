package fortscale.collection.jobs.ad;

import fortscale.utils.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Amir Keren on 17/05/2015.
 */
public class AdConnections implements InitializingBean {

    @Value("${ad.connections}")
    private String adConnectionsFile;

    private static Logger logger = Logger.getLogger(AdFetchJob.class);

    private List<AdConnection> adConnections;

    public AdConnections() {}

    public AdConnections(String adConnectionsFile) throws Exception {
        this.adConnectionsFile = adConnectionsFile;
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile;
        try {
            jsonFile = new File(adConnectionsFile);
            if (!jsonFile.exists()) {
                throw new Exception();
            }
        } catch (Exception ex) {
            logger.error("Error - Active Directory Json connection file does not exist or unreadable");
            throw new Exception();
        }
        try {
            adConnections = mapper.readValue(jsonFile, new TypeReference<List<AdConnection>>(){});
        } catch (Exception ex) {
            logger.error("Error - Bad Active Directory Json connection file");
            throw new Exception();
        }
    }

    public List<AdConnection> getAdConnections() {
        return adConnections;
    }

}