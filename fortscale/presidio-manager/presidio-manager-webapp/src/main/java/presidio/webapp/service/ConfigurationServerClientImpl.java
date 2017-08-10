package presidio.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * Created by efratn on 09/08/2017.
 */
public class ConfigurationServerClientImpl implements ConfigurationServerClientService{

//    @Value("")
    private static final String configServerHost = "localhost";

    private static final String configServerPort = ":8888";
    private static final String configServerSchema = "http://";

    private static String CONFIG_SERVIER_URL_PREFIX = configServerSchema + configServerHost + configServerPort;


    @Autowired
    private RestTemplate restTemplate;

    public ConfigurationServerClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void storeFile(JsonNode requestBody) {
        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        String url = CONFIG_SERVIER_URL_PREFIX + "/store";
        ResponseEntity<String> loginResponse = restTemplate
                .exchange(url, HttpMethod.POST, entity, String.class);
    }
}
