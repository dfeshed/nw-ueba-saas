package presidio.config.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

/**
 * Created by efratn on 09/08/2017.
 */
public class ConfigurationServerClientServiceImpl implements ConfigurationServerClientService{


    private String configServerUri;
    private String configServerUserName;
    private String configServerPassword;
    private RestTemplate restTemplate;

    public ConfigurationServerClientServiceImpl(RestTemplate restTemplate, String configServerUri, String configServerUserName, String configServerPassword) {
        this.restTemplate = restTemplate;
        this.configServerUri = configServerUri;
        this.configServerUserName = configServerUserName;
        this.configServerPassword = configServerPassword;
    }

    @Override
    public void storeConfigurationFile(String fileName, JsonNode configFileContent) {
        // set headers with basic auth to config server
        HttpHeaders headers = createHeaders(configServerUserName, configServerPassword);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(configFileContent.toString().getBytes()));
        HttpEntity<Resource> entity = new HttpEntity<>(resource, headers);

        String url = String.format("%s/%s",configServerUri ,fileName);
        ResponseEntity<Void> loginResponse = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = String.format("%s:%s",username,password);
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }
}