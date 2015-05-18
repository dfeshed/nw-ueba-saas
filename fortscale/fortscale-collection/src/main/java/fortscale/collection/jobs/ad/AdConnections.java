package fortscale.collection.jobs.ad;

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

    @Value("${ad.connections:}")
    private String adConnectionsFile;

    private List<AdConnection> adConnections;

    @Override
    public void afterPropertiesSet() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        adConnections = mapper.readValue(new File(adConnectionsFile), new TypeReference<List<AdConnection>>(){});
    }

    public List<AdConnection> getAdConnections() {
        return adConnections;
    }

}