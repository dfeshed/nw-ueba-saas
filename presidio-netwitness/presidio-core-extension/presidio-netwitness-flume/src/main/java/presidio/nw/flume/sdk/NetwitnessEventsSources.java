package presidio.nw.flume.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.config.server.factory.ConfigurationServerClientServiceFactory;
import presidio.nw.flume.model.Configuration;
import presidio.nw.flume.model.DataPullingConfiguration;

import java.util.Collections;
import java.util.List;

public class NetwitnessEventsSources {

    private static Logger logger = LoggerFactory.getLogger(NetwitnessEventsStream.class);

    private static final String BROKER_END_POINT = "nw://admin:netwitness@10.25.67.33:50005";

    private ConfigurationServerClientService configurationServerClientService;

    public NetwitnessEventsSources(){
        final ConfigurationServerClientServiceFactory configurationServerClientServiceFactory = new ConfigurationServerClientServiceFactory();
        try {
            this.configurationServerClientService = configurationServerClientServiceFactory.createConfigurationServerClientService();
        } catch (Exception e) {
            logger.error("failed to start netwitness event stream", e);
        }
    }

    public List<String> getSourcesURI() {

        DataPullingConfiguration dataPullingConfiguration = null;
        try {
            dataPullingConfiguration = configurationServerClientService.readConfigurationAsJson("application-presidio", "default", Configuration.class).getDataPulling();
        } catch (Exception e) {
            String errorMessage = String.format("Failed to fetch. Failed to get configuration from config server %s", configurationServerClientService);
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }

        if(dataPullingConfiguration == null) {
            logger.error("Failed to read data pulling source from configuration server");
            throw new RuntimeException("Failed to read data pulling source from configuration server");
        }

        List sources = Collections.singletonList(dataPullingConfiguration.getSource());
        return sources;
    }
}
