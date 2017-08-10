package presidio.config.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * Created by efratn on 09/08/2017.
 */
public class ConfigurationServerClientServiceImpl implements ConfigurationServerClientService{

    @Value("${spring.cloud.config.uri}")
    private static String configServerUri;

    @Value("${spring.cloud.config.username}")
    private static String configServerUserName;

    @Value("${spring.cloud.config.password}")
    private static String configServerPassword;

    @Autowired
    private RestTemplate restTemplate;

    public ConfigurationServerClientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void storeConfigurationFile(String fileName, JsonNode configFileContent) {
        // set headers with basic auth to config server
//        HttpHeaders headers = createHeaders(configServerUserName, configServerPassword); //TODO take from properties
        String username = "config";
        String password = "secure";
        HttpHeaders headers = createHeaders(username, password);
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity<>(configFileContent.toString(), headers);

//        String url = configServerUri + "/" + fileName; //TODO take from properties
        String uri = "http://localhost:8888" + "/" + fileName;

        ResponseEntity<String> loginResponse = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
    }

    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }
}
