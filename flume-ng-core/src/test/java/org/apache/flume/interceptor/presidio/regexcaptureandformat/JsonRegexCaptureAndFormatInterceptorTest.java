package org.apache.flume.interceptor.presidio.regexcaptureandformat;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.JSONEvent;
import org.apache.flume.interceptor.presidio.AbstractPresidioJsonInterceptor;
import org.apache.flume.interceptor.presidio.regexcaptureandformat.JsonRegexCaptureAndFormatInterceptor.Builder;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.mockito.Mockito.*;

public class JsonRegexCaptureAndFormatInterceptorTest {
    @Test
    public void test_ldap_format_with_common_name_alone() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(
                "{\"pattern\":\"CN=([^,]+)\",\"format\":\"%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"LOWER\"}]}"
        );

        Event event = buildEvent("user.dst", "CN=Bobby");
        event = interceptor.doIntercept(event);
        assertEvent(event, "userId", "bobby");
    }

    @Test
    public void test_ldap_format_with_common_name_and_additional_attributes() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(
                "{\"pattern\":\"CN=([^,]+),.+\",\"format\":\"%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"LOWER\"}]}"
        );

        Event event = buildEvent("user.dst", "CN=BOBBY,OU=Users,DC=Dell");
        event = interceptor.doIntercept(event);
        assertEvent(event, "userId", "bobby");
    }

    @Test
    public void test_domain_backslash_username_at_domain_name_format() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(
                "{\"pattern\":\".+\\\\\\\\(.+)@(.+)\",\"format\":\"%s@%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"LOWER\"},{\"index\":2,\"caseFormat\":\"LOWER\"}]}"
        );

        Event event = buildEvent("user.dst", "CORP\\\\BOBBY@DELL.COM");
        event = interceptor.doIntercept(event);
        assertEvent(event, "userId", "bobby@dell.com");
    }

    @Test
    public void test_domain_backslash_username_format() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(
                "{\"pattern\":\".+\\\\\\\\([^@]+)\",\"format\":\"%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"UPPER\"}]}"
        );

        Event event = buildEvent("user.dst", "corp\\\\bobby");
        event = interceptor.doIntercept(event);
        assertEvent(event, "userId", "BOBBY");
    }

    @Test
    public void test_email_format() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(
                "{\"pattern\":\"(.+)@(.+)\",\"format\":\"%s@%s\"," +
                "\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"UPPER\"},{\"index\":2,\"caseFormat\":\"LOWER\"}]}"
        );

        Event event = buildEvent("user.dst", "bobby@DELL.COM");
        event = interceptor.doIntercept(event);
        assertEvent(event, "userId", "BOBBY@dell.com");
    }

    @Test
    public void test_username_or_unclassified_format() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(null);
        Event event = buildEvent("user.dst", "BOBBY");
        event = interceptor.doIntercept(event);
        assertEvent(event, "userId", "bobby");
    }

    @Test
    public void test_case_insensitive_matching() {
        AbstractPresidioJsonInterceptor interceptor = buildInterceptor(
                "{\"pattern\":\".*(?i:fail).*\",\"format\":\"FAILURE\"}," +
                "{\"pattern\":\".*(?i:succ).*\",\"format\":\"SUCCESS\"}"
        );

        Event event = buildEvent("user.dst", "YOU FAIL TO AMAZE ME");
        assertEvent(interceptor.doIntercept(event), "userId", "FAILURE");

        event = buildEvent("user.dst", "I Failed Once");
        assertEvent(interceptor.doIntercept(event), "userId", "FAILURE");

        event = buildEvent("user.dst", "failure is not an option");
        assertEvent(interceptor.doIntercept(event), "userId", "FAILURE");

        event = buildEvent("user.dst", "IF YOU DON'T TRY, YOU WON'T SUCCEED");
        assertEvent(interceptor.doIntercept(event), "userId", "SUCCESS");

        event = buildEvent("user.dst", "I Succeeded!");
        assertEvent(interceptor.doIntercept(event), "userId", "SUCCESS");

        event = buildEvent("user.dst", "the key to success is...");
        assertEvent(interceptor.doIntercept(event), "userId", "SUCCESS");
    }

    private static AbstractPresidioJsonInterceptor buildInterceptor(String captureAndFormatConfiguration) {
        String configuration =
                "{\"sourceKey\":\"user.dst\",\"destinationKey\":\"userId\",\"captureAndFormatConfigurations\":["
                + (isBlank(captureAndFormatConfiguration) ? EMPTY : captureAndFormatConfiguration + ",")
                + "{\"pattern\":\"(.+)\",\"format\":\"%s\",\"capturingGroupConfigurations\":[{\"index\":1,\"caseFormat\":\"LOWER\"}]}]}";
        Context context = mock(Context.class);
        when(context.getString(eq(Builder.CONFIGURATION_KEY))).thenReturn(configuration);
        Builder builder = new Builder();
        builder.doConfigure(context);
        return builder.doBuild();
    }

    private static Event buildEvent(String key, String value) {
        Event event = new JSONEvent();
        event.setBody(String.format("{\"%s\":\"%s\"}", key, value).getBytes());
        return event;
    }

    private static void assertEvent(Event event, String key, String value) {
        String body = new String(event.getBody());
        JSONObject jsonObject = new JSONObject(body);
        Assert.assertEquals(value, jsonObject.getString(key));
    }
}
