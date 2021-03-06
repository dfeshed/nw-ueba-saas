package presidio.config.server.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class ConfigurationServerClientServiceImpl implements ConfigurationServerClientService {

    private final String configServerUri;
    private final String configServerUserName;
    private final String configServerPassword;
    private final RestTemplate restTemplate;

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

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

        HttpEntity<String> entity = new HttpEntity<>(configFileContent.toString(), headers);
        String url = String.format("%s/%s", configServerUri, fileName);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

    @Override
    public ResponseEntity<?> readConfiguration(Class<?> responseEntityType, String moduleName, String profile) {
        UriComponentsBuilder builder = getConfigServerUriInJsonFormat(moduleName, profile);

        HttpHeaders headers = createBasicAuthenticationHeaders(configServerUserName, configServerPassword);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, responseEntityType);
    }

    private UriComponentsBuilder getConfigServerUriInJsonFormat(String moduleName, String profile) {
        return getConfigServerUri(moduleName, profile).path(".json");
    }

    private UriComponentsBuilder getConfigServerUri(String moduleName, String profile) {
        return UriComponentsBuilder.fromHttpUrl(configServerUri)
                .path("/" + moduleName)
                .path("-" + profile);
    }

    @Override
    public Properties readConfigurationAsProperties(String moduleName) throws Exception {
        return readConfigurationAsProperties(moduleName, null);
    }

    @Override
    public Properties readConfigurationAsProperties(String moduleName, String profile) throws Exception {

        String path = ".properties";

        UriComponentsBuilder builder = getConfigServerUri(moduleName, profile)
                .path(path);

        HttpHeaders headers = createBasicAuthenticationHeaders(configServerUserName, configServerPassword);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String stringOfPropertiesFile = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class).getBody();
        final Properties p = new Properties();
        p.load(new StringReader(stringOfPropertiesFile));

        return p;
    }

    @Override
    public <T> T readConfigurationAsJson(String moduleName, String profile, Class<T> clazz) throws Exception {

        UriComponentsBuilder builder = getConfigServerUriInJsonFormat(moduleName, profile);

        HttpHeaders headers = createBasicAuthenticationHeaders(configServerUserName, configServerPassword);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);



        T response = OBJECT_MAPPER.readValue(responseEntity.getBody(), clazz);
        return response;

    }


    @Override
    public String readConfigurationAsJsonString(String moduleName, String profile) throws Exception {

        UriComponentsBuilder builder = getConfigServerUriInJsonFormat(moduleName, profile);

        HttpHeaders headers = createBasicAuthenticationHeaders(configServerUserName, configServerPassword);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

        return responseEntity.getBody();

    }

    private HttpHeaders createBasicAuthenticationHeaders(String username, String password) {
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