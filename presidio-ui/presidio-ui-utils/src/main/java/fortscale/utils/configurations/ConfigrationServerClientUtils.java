package fortscale.utils.configurations;

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
public interface ConfigrationServerClientUtils {

    /**
     * This method use to read single configuration file froms server and return the properties
     * @return Properties container
     * @throws Exception
     */
    Properties readConfigurationAsProperties(String moduleName, String profile) throws Exception;
}