package presidio.config.server.client;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by efratn on 09/08/2017.
 */
public interface ConfigurationServerClientService {

    void storeConfigurationFile(String fileName, JsonNode configFileContent);
}
