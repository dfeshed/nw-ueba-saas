package presidio.config.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;


public interface ConfigurationServerClientService {

    ResponseEntity<Void> storeConfigurationFile(String fileName, JsonNode configFileContent);

    ResponseEntity<?> readConfiguration(Class<?> responseEntityType, String moduleName, String profile);
}
