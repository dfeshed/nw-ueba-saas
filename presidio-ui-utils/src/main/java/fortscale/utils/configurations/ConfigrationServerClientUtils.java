package fortscale.utils.configurations;

import fortscale.utils.spring.SpringPropertiesUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by shays on 28/09/2017.
 */
public class ConfigrationServerClientUtils {

    //One time instance for rest template
    private RestTemplate restTemplate = new RestTemplate();


    /**
     * This method use to read single configuration file froms server and return the properties
     * @return Properties container
     * @throws Exception
     */
    public Properties readConfigurationAsProperties() throws Exception {
        //Read the configuration server settings
        String moduleName = SpringPropertiesUtil.getProperty("cawebapp.module.name");
        String profile =SpringPropertiesUtil.getProperty("cawebapp.profile.name");
        ConfigurationServcerClientSettings conf = readSettings(moduleName,profile);
        return readConfigurationInternal(conf);


    }

    /**
     * This method use to read single configuration file froms server and return the properties
     * @return Properties container
     * @throws Exception
     */
    public Properties readConfigurationAsProperties(String moduleName, String profile) throws Exception {

        ConfigurationServcerClientSettings conf = readSettings(moduleName,profile);
        return readConfigurationInternal(conf);


    }

    private Properties readConfigurationInternal(ConfigurationServcerClientSettings conf) throws IOException {
        //Build the URL path
        String path = "/" + conf.getModuleName();
        String profile = "default";
        if (conf.getProfile() != null && conf.getProfile().length() > 0) {
            profile = conf.getProfile();
        }
        path += "-" + profile + ".properties";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(conf.getServerUrl())
                .path(path);

        //Build the rest request
        HttpHeaders headers = createBasicAuthenticationHeaders(conf.getServerUserName(), conf.getServerUserNamePassword());
        HttpEntity<?> entity = new HttpEntity<>(headers);


        String stringOfPropertiesFile = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class).getBody();
        //Convert the result into properties file
        final Properties p = new Properties();
        p.load(new StringReader(stringOfPropertiesFile));

        return p;
    }

    /**
     * Define the header for the configuration server request
     * @param username
     * @param password
     * @return
     */
    private HttpHeaders createBasicAuthenticationHeaders(String username, String password){
        final HttpHeaders httpHeaders = new HttpHeaders();
        String auth = String.format("%s:%s", username, password);
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF8")));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);

        return httpHeaders;
    }

    /**
     * Extract configuration server settings from properties file
     * @return
     */
    private ConfigurationServcerClientSettings readSettings(String moduleName, String profile){

        if (StringUtils.isBlank(profile)){
            profile=null;
        }
        String serverUrl = SpringPropertiesUtil.getProperty("spring.cloud.config.uri");
        String serverUserName = SpringPropertiesUtil.getProperty("spring.cloud.config.username");
        String serverUserNamePassword = SpringPropertiesUtil.getProperty("spring.cloud.config.password");
        return  new ConfigurationServcerClientSettings(moduleName,profile,serverUrl,serverUserName,serverUserNamePassword);
    }

    public static class ConfigurationServcerClientSettings{
        private String moduleName;
        private String profile;
        private String serverUrl;
        private String serverUserName;
        private String serverUserNamePassword;

        protected ConfigurationServcerClientSettings(String moduleName, String profile, String serverUrl, String serverUserName, String serverUserNamePassword) {
            this.moduleName = moduleName;
            this.profile = profile;
            this.serverUrl = serverUrl;
            this.serverUserName = serverUserName;
            this.serverUserNamePassword = serverUserNamePassword;
        }

        public String getModuleName() {
            return moduleName;
        }

        public String getProfile() {
            return profile;
        }

        public String getServerUrl() {
            return serverUrl;
        }

        public String getServerUserName() {
            return serverUserName;
        }

        public String getServerUserNamePassword() {
            return serverUserNamePassword;
        }
    }
}
