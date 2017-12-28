package presidio.adapter.util;

import fortscale.domain.adapter.CollectorProperties;
import fortscale.domain.adapter.SchemaMapping;
import presidio.config.server.client.ConfigurationServerClientService;

/**
 * Created by tomerd on 12/27/2017.
 */
public class AdapterConfigurationUtil {

    public static final String MOUDLE_NAME = "collector-properties";
    public static final String PROFILE = "";
    private final ConfigurationServerClientService configurationServerClientService;

    private String collectionName;
    private String timestampField;
    private int numberOfRetainedDays;

    public AdapterConfigurationUtil(ConfigurationServerClientService configurationServerClientService) {
        this.configurationServerClientService = configurationServerClientService;
    }

    public void loadConfiguration(String schemaName) throws Exception {
        final CollectorProperties properties = (CollectorProperties) configurationServerClientService.readConfiguration(CollectorProperties.class, MOUDLE_NAME, PROFILE).getBody();
        final SchemaMapping schema = properties.getSchema(schemaName);
        if (schema != null) {
            this.collectionName = schema.getCollectionName();
            this.timestampField = schema.getTimeFieldName();
            this.numberOfRetainedDays = schema.getNumberOfRetainedDays();
        }
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
