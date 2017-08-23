package presidio.config.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import java.util.Map;


public interface ConfigurationServerClientService {

    ResponseEntity<Void> storeConfigurationFile(String fileName, JsonNode configFileContent);

    ResponseEntity<?> readConfiguration(Class<?> responseEntityType, String moduleName, String profile);

    /**
     * This method the properties as a key-value map
     *
     * @param moduleName
     * @param profile
     * @return properties and values
     * @throws Exception
     */
    Map<String, String> readConfigurationAsProperties(String moduleName, String profile) throws Exception;

    <T> T readConfigurationAsJson(String moduleName, String profile, Class<T> clazz) throws Exception;
}
