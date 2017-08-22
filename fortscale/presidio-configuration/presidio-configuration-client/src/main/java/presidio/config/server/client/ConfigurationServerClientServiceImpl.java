package presidio.config.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

public class ConfigurationServerClientServiceImpl implements ConfigurationServerClientService {

    private final String configServerUri;
    private final String configServerUserName;
    private final String configServerPassword;
    private final RestTemplate restTemplate;

    public ConfigurationServerClientServiceImpl(RestTemplate restTemplate, String configServerUri, String configServerUserName, String configServerPassword) {
        this.restTemplate = restTemplate;
        this.configServerUri = configServerUri;
        this.configServerUserName = configServerUserName;
        this.configServerPassword = configServerPassword;
    }

    @Override
    public ResponseEntity<Void> storeConfigurationFile(String fileName, JsonNode configFileContent) {
        // set headers with basic auth to config server
        HttpHeaders headers = createBasicAuthenticationHeaders(configServerUserName, configServerPassword);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Resource resource = new InputStreamResource(new ByteArrayInputStream(configFileContent.toString().getBytes()));
        HttpEntity<Resource> entity = new HttpEntity<>(resource, headers);

        String url = String.format("%s/%s", configServerUri , fileName);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

    @Override
    public ResponseEntity<?> readConfiguration(Class<?> responseEntityType, String moduleName, String profile) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(configServerUri)
                .path("/" + moduleName)
                .path("/" + profile);

        HttpHeaders headers = createBasicAuthenticationHeaders(configServerUserName, configServerPassword);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, responseEntityType);
    }

    private HttpHeaders createBasicAuthenticationHeaders(String username, String password){
        final HttpHeaders httpHeaders = new HttpHeaders();
        String auth = String.format("%s:%s", username, password);
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF8")));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);

        return httpHeaders;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("configServerUri", configServerUri)
                .append("configServerUserName", configServerUserName)
                .append("configServerPassword", Base64.encodeBase64(configServerPassword.getBytes()))
                .toString();
    }
}