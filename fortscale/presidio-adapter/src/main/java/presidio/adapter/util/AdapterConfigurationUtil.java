package presidio.adapter.util;

import fortscale.common.general.Schema;
import presidio.config.server.client.ConfigurationServerClientService;

import java.util.Properties;

/**
 * Created by tomerd on 12/27/2017.
 */
public class AdapterConfigurationUtil {

    private final ConfigurationServerClientService configurationServerClientService;

    private String collectionName;
    private String timestampField;
    private int numberOfRetainedDays;

    public AdapterConfigurationUtil(ConfigurationServerClientService configurationServerClientService) {
        this.configurationServerClientService = configurationServerClientService;
    }

    public void loadConfiguration(Schema schema) throws Exception {
        final Properties properties = configurationServerClientService.readConfigurationAsProperties("collector-properties");
        properties.getProperty()
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getTimestampField() {
        return timestampField;
    }

    public int getNumberOfRetainedDays() {
        return numberOfRetainedDays;
    }
}
