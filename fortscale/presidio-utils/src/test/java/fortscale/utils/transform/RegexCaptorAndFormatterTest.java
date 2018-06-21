package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.Test;

import static fortscale.utils.transform.TransformerUtil.assertJsonObjectKeyNotAdded;
import static fortscale.utils.transform.TransformerUtil.assertNewJsonObjectNotContainsOriginalJsonObject;
import static fortscale.utils.transform.TransformerUtil.assertWrongValueAddedToKey;


public class RegexCaptorAndFormatterTest extends TransformerTest{

    @Test
    public void test_ldap_format_with_common_name_alone() {
        String pattern = "{\"pattern\":\"CN=([^,]+)\",\"format\":\"%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"LOWER\"}]}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "CN=Bobby", "bobby");
    }

    @Test
    public void test_ldap_format_with_common_name_and_additional_attributes() {
        String pattern = "{\"pattern\":\"CN=([^,]+),.+\",\"format\":\"%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"LOWER\"}]}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "CN=BOBBY,OU=Users,DC=Dell", "bobby");
    }

    @Test
    public void test_domain_backslash_username_at_domain_name_format() {
        String pattern = "{\"pattern\":\".+\\\\\\\\(.+)@(.+)\",\"format\":\"%s@%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"LOWER\"},{\"index\":2,\"caseFormat\":\"LOWER\"}]}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "CORP\\\\BOBBY@DELL.COM", "bobby@dell.com");
    }

    @Test
    public void test_domain_backslash_username_format() {
        String pattern = "{\"pattern\":\".+\\\\\\\\([^@]+)\",\"format\":\"%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"UPPER\"}]}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "corp\\\\bobby", "BOBBY");
    }

    @Test
    public void test_email_format() {
        String pattern = "{\"pattern\":\"(.+)@(.+)\",\"format\":\"%s@%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"UPPER\"},{\"index\":2,\"caseFormat\":\"LOWER\"}]}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "bobby@DELL.COM", "BOBBY@dell.com");
    }

    @Test
    public void test_username_or_unclassified_format() {
        String transformerConfiguration = buildTransformerConfiguration(null);

        testNormalization(transformerConfiguration, "BOBBY", "bobby");
    }

    @Test
    public void test_case_insensitive_matching() {
        String pattern = "{\"pattern\":\".*(?i:fail).*\",\"format\":\"FAILURE\"}," +
                "{\"pattern\":\".*(?i:succ).*\",\"format\":\"SUCCESS\"}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "YOU FAIL TO AMAZE ME", "FAILURE");
        testNormalization(transformerConfiguration, "I Failed Once", "FAILURE");
        testNormalization(transformerConfiguration, "failure is not an option", "FAILURE");
        testNormalization(transformerConfiguration, "IF YOU DON'T TRY, YOU WON'T SUCCEED", "SUCCESS");
        testNormalization(transformerConfiguration, "I Succeeded!", "SUCCESS");
        testNormalization(transformerConfiguration, "the key to success is...", "SUCCESS");
    }

    @Test
    public void test_machine_normalization_colon() {
        String pattern = "{\"pattern\":\".*:.*\",\"format\":\"\"}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "FE80:0000:0000:0000:0202:B3FF:FE1E:8329", "");
        testNormalization(transformerConfiguration, "[2001:db8:0:1]:80", "");
        testNormalization(transformerConfiguration, "MY-DESKTOP1", "my-desktop1");
    }

    @Test
    public void test_ipv4_pattern_match1() {
        String pattern = "{\"pattern\":\"(\\\\\\\\\\\\\\\\)?\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}(:\\\\d+){0,1}\",\"format\":\"\"}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "10.64.152.163", "");
    }

    @Test
    public void test_ipv4_pattern_match2() {
        String pattern = "{\"pattern\":\"(\\\\\\\\\\\\\\\\)?\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}(:\\\\d+){0,1}\",\"format\":\"\"}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "\\\\10.64.152.163", "");
    }

    @Test
    public void test_ipv4_pattern_no_match1() {
        String pattern = "{\"pattern\":\"(\\\\\\\\\\\\\\\\)?\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}(:\\\\d+){0,1}\",\"format\":\"\"}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "\\10.64.152.163", "\\10.64.152.163");
    }

    @Test
    public void test_ipv4_pattern_no_match2() {
        String pattern = "{\"pattern\":\"(\\\\\\\\\\\\\\\\)?\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}(:\\\\d+){0,1}\",\"format\":\"\"}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "a.64.152.163", "a.64.152.163");
    }

    @Test
    public void test_ipv4_pattern_no_match3() {
        String pattern = "{\"pattern\":\"(\\\\\\\\\\\\\\\\)?\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}(:\\\\d+){0,1}\",\"format\":\"\"}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "10a64.152.163", "10a64.152.163");
    }

    @Test
    public void test_machine_normalization_fqdn() {
        String pattern = "{\"pattern\":\"(\\\\\\\\\\\\\\\\)?(.+)\\\\..+\\\\..+\",\"format\":\"%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":2,\"caseFormat\":\"LOWER\"}]}";
        String transformerConfiguration = buildTransformerConfiguration(pattern);

        testNormalization(transformerConfiguration, "host.domain.local", "host");
        testNormalization(transformerConfiguration, "MACHINE.CORP.GLOBAL", "machine");
        testNormalization(transformerConfiguration, "\\\\HOST.DOMAIN.LOCAL", "host");
        testNormalization(transformerConfiguration, "\\\\machine.corp.global", "machine");
        testNormalization(transformerConfiguration, "Host@Domain.Local", "host@domain.local");
    }

    @Test
    public void test_machine_normalization_preceding_backslashes() {
        String pattern = "{\"pattern\":\"(\\\\\\\\\\\\\\\\)?(.+)\",\"format\":\"[%s]\"," +
                "\"capturingGroupConfigurations\":[{\"index\":2,\"caseFormat\":\"LOWER\"}]}";

        String transformerConfiguration = buildTransformerConfiguration(pattern);
        String destValue = "[my-machine1]";
        testNormalization(transformerConfiguration, "my-machine1", destValue);
        testNormalization(transformerConfiguration, "MY-MACHINE1", destValue);
        testNormalization(transformerConfiguration, "\\\\My-Machine1", destValue);
    }

    private void testNormalization(String configuration, String sourceValue, String destValue) {
        JSONObject jsonObject = new JSONObject();

        String userDstKey = "user.dst";
        String userIdKey = "userId";

        jsonObject.put(userDstKey, sourceValue);
        JSONObject retJsonObject = transform(configuration,jsonObject);
        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);
        assertJsonObjectKeyNotAdded(retJsonObject, userIdKey);
        assertWrongValueAddedToKey(retJsonObject, userIdKey, destValue);
    }

    private String buildTransformerConfiguration(String patterns){
        String transformerConfiguration =
                "{\"name\":\"userNameNormalizer\",\"type\":\"regex_captor_and_formatter\",\"sourceKey\":\"user.dst\",\"destinationKey\":\"userId\",\"captureAndFormatConfigurations\":["
                        + (StringUtils.isBlank(patterns) ? StringUtils.EMPTY : patterns + ",")
                        + "{\"pattern\":\"(.+)\",\"format\":\"%s\",\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"LOWER\"}]}]}";
        return transformerConfiguration;
    }
}
