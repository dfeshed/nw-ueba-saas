package org.apache.flume.interceptor.presidio;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.JSONEvent;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.InterceptorBuilderFactory;
import org.apache.flume.interceptor.InterceptorType;
import org.apache.flume.interceptor.presidio.JsonFieldSwitchCaseInterceptor.Builder;
import org.apache.flume.tools.MockMonitorInitiator;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static java.util.Collections.singletonMap;
import static org.apache.flume.interceptor.presidio.JsonFieldSwitchCaseInterceptor.Builder.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing JsonFieldSwitchCaseInterceptor based on the 4663 and the logic of the operation type.
 */
public class JsonFieldSwitchCaseInterceptorTest {

    private Interceptor.Builder builder;
    private static final String ACCESSES_FIELD_NAME = "accesses";
    private static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    private static final String ACCESSES_CREATE_OPERATION_VALUE = "WriteData (or AddFile)";
    private static final String FILE_CREATED = "FILE_CREATED";
    private static final String ACCESSES_OPEN_OPERATION_VALUE = "ReadData (or ListDirectory)";
    private static final String FILE_OPEN = "FILE_OPEN";
    private static final String ACCESSES_MODIFIED_OPERATION_VALUE = "AppendData (or AddSubdirectory or CreatePipeInstance)";
    private static final String FILE_MODIFIED = "FILE_MODIFIED";
    private static final String ACCESSES_WRITE_DAC_OPERATION_VALUE = "WRITE_DAC";
    private static final String FILE_WRITE_DAC_PERMISSION_CHANGED = "FILE_WRITE_DAC_PERMISSION_CHANGED";
    private static final String ACCESSES_WRITE_OWNER_OPERATION_VALUE = "WRITE_OWNER";
    private static final String FILE_WRITE_OWNER_PERMISSION_CHANGED = "FILE_WRITE_OWNER_PERMISSION_CHANGED";
    private static final String ACCESSES_READ_ATTRIBUTE_OPERATION_VALUE = "READ_ATTRIBUTE";
    private static final String ACCESSES_WRITE_ATTRIBUTE_OPERATION_VALUE = "WRITE_ATTRIBUTE";
    private static final String CASES_DELIM = "###";
    private static final String CASES = String.join(CASES_DELIM, ACCESSES_CREATE_OPERATION_VALUE,ACCESSES_OPEN_OPERATION_VALUE,ACCESSES_MODIFIED_OPERATION_VALUE,ACCESSES_WRITE_DAC_OPERATION_VALUE,ACCESSES_WRITE_OWNER_OPERATION_VALUE);
    private static final String CASES_VALUES = String.join(";", FILE_CREATED, FILE_OPEN, FILE_MODIFIED, FILE_WRITE_DAC_PERMISSION_CHANGED, FILE_WRITE_OWNER_PERMISSION_CHANGED);
    private static final String EVENT_CODE_FIELD_NAME = "reference_id";

    private static final String ALIAS_HOST_FIELD_NAME = "alias_host";
    private static final String HOST_SRC_FIELD_NAME = "host_src";
    private static final String SRC_MACHINE = "src_machine";


    @Before
    public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.JSON_FIELD_SWITCH_CASE_INTERCEPTOR.toString());
    }

    private Context createContextWithAccessesConfiguration(boolean isAddConditionConfiguration){
        Context ctx = new Context();
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.ORIGIN_FIELD_CONF_NAME, ACCESSES_FIELD_NAME);
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.DESTINATION_FIELD_CONF_NAME, OPERATION_TYPE_FIELD_NAME);
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.CASES_CONF_NAME, CASES);
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.CASES_DELIM_CONF_NAME, CASES_DELIM);
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.CASES_VALUES_CONF_NAME, CASES_VALUES);
        if(isAddConditionConfiguration) {
            ctx.put(JsonFieldSwitchCaseInterceptor.Builder.CONDITION_FIELD_CONF_NAME, EVENT_CODE_FIELD_NAME);
            ctx.put(JsonFieldSwitchCaseInterceptor.Builder.REGEX_CONDITION_CONF_NAME, "4663");
        }

        return ctx;
    }

    private Interceptor initInterceptorWithAccessesContext(boolean isAddConditionConfiguration) {
        Context ctx = createContextWithAccessesConfiguration(isAddConditionConfiguration);

        builder.configure(ctx);

        return builder.build();
    }

    private void interceptEventAndTestOperationType(boolean includeEventCodeConf, String eventCode, String accessesValue, String expectedOperationType){
        String eventBody = interceptEvent(includeEventCodeConf,eventCode, accessesValue);

        Assert.assertTrue(String.format("The operation type field has not been added. event: %s", eventBody),
                eventBody.contains(OPERATION_TYPE_FIELD_NAME));
        String operationTypeKeyValue = JsonInterceptorUtil.buildKeyValue(OPERATION_TYPE_FIELD_NAME, expectedOperationType);
        Assert.assertTrue(String.format("The operation type field has been added incorrectly. expected key value: %s, event: %s", operationTypeKeyValue, eventBody),
                eventBody.contains(operationTypeKeyValue));
    }

    private String interceptEvent(boolean includeEventCodeConf, String eventCode, String accessesValue){
        Interceptor interceptor = initInterceptorWithAccessesContext(includeEventCodeConf);
        MockMonitorInitiator.setMockMonitor(interceptor);
        ArrayList<String> fields = new ArrayList<>();
        String accessesKeyValue = JsonInterceptorUtil.buildKeyValue(ACCESSES_FIELD_NAME, accessesValue);
        fields.add(accessesKeyValue);
        String eventCodeKeyValue = null;
        if(eventCode!=null) {
            eventCodeKeyValue = JsonInterceptorUtil.buildKeyValue(EVENT_CODE_FIELD_NAME, eventCode);
            fields.add(eventCodeKeyValue);
        }

        Event event = JsonInterceptorUtil.buildEvent(fields);

        event = interceptor.intercept(event);
        Assert.assertNotNull(event);

        String eventBody = new String(event.getBody());
        Assert.assertTrue(String.format("The following key value has been removed or changed. key value: %s, event: %s",
                accessesKeyValue, eventBody),eventBody.contains(accessesKeyValue));
        if(eventCodeKeyValue != null) {
            Assert.assertTrue(String.format("The following key value has been removed or changed. key value: %s, event: %s",
                    eventCodeKeyValue, eventBody), eventBody.contains(eventCodeKeyValue));
        }

        return eventBody;
    }

    @Test
    public void interceptFileCreatedEventTest() {
        interceptEventAndTestOperationType(true, "4663", ACCESSES_CREATE_OPERATION_VALUE, FILE_CREATED);
    }

    @Test
    public void interceptFileCreatedEventWithEventCodeNoMatchTest() {
        String eventBody = interceptEvent(true, "4660", ACCESSES_CREATE_OPERATION_VALUE);

        Assert.assertFalse(String.format("The operation type field has been added though, the event code is not 4663. event: %s",
                eventBody), eventBody.contains(OPERATION_TYPE_FIELD_NAME));
    }

    @Test
    public void interceptFileCreatedEventWithNoEventCodeConfAndNoEventCodeValueTest() {
        interceptEventAndTestOperationType(false,null, ACCESSES_CREATE_OPERATION_VALUE, FILE_CREATED);
    }

    @Test
    public void interceptFileCreatedEventWithNoEventCodeValueTest() {
        String eventBody = interceptEvent(true,null, ACCESSES_CREATE_OPERATION_VALUE);

        Assert.assertFalse(String.format("The operation type field has been added though, the event code doesn't exist. event: %s",
                eventBody), eventBody.contains(OPERATION_TYPE_FIELD_NAME));
    }

    @Test
    public void interceptFileCreatedEventWithMultiAccessesValuesTest() {
        String multiAccessesValues = String.join(",",ACCESSES_OPEN_OPERATION_VALUE,ACCESSES_WRITE_OWNER_OPERATION_VALUE,
                ACCESSES_CREATE_OPERATION_VALUE, ACCESSES_WRITE_DAC_OPERATION_VALUE);
        interceptEventAndTestOperationType(true, "4663", multiAccessesValues, FILE_CREATED);
    }

    @Test
    public void interceptFileOpenEventWithMultiAccessesValuesTest() {
        String multiAccessesValues = String.join(",",ACCESSES_MODIFIED_OPERATION_VALUE,ACCESSES_OPEN_OPERATION_VALUE,
                ACCESSES_WRITE_OWNER_OPERATION_VALUE, ACCESSES_WRITE_DAC_OPERATION_VALUE);
        interceptEventAndTestOperationType(true, "4663", multiAccessesValues, FILE_OPEN);
    }

    @Test
    public void interceptFileFileModifiedEventTest() {
        interceptEventAndTestOperationType(true, "4663", ACCESSES_MODIFIED_OPERATION_VALUE, FILE_MODIFIED);
    }

    @Test
    public void interceptFileModifiedEventWithMultiAccessesValuesTest() {
        String multiAccessesValues = String.join(",",ACCESSES_WRITE_DAC_OPERATION_VALUE,ACCESSES_MODIFIED_OPERATION_VALUE,
                ACCESSES_WRITE_OWNER_OPERATION_VALUE, ACCESSES_WRITE_ATTRIBUTE_OPERATION_VALUE);
        interceptEventAndTestOperationType(true, "4663", multiAccessesValues, FILE_MODIFIED);
    }

    @Test
    public void interceptFileFileWriteOwnerEventTest() {
        interceptEventAndTestOperationType(true, "4663", ACCESSES_WRITE_OWNER_OPERATION_VALUE, FILE_WRITE_OWNER_PERMISSION_CHANGED);
    }

    @Test
    public void interceptMultiAccessesValuesWithNoOperationTypeResolvingTest() {
        String multiAccessesValues = String.join(",",ACCESSES_READ_ATTRIBUTE_OPERATION_VALUE,ACCESSES_WRITE_ATTRIBUTE_OPERATION_VALUE);
        String eventBody = interceptEvent(true, "4663", multiAccessesValues);

        Assert.assertTrue(String.format("The operation type field has not been added. event: %s", eventBody),
                eventBody.contains(OPERATION_TYPE_FIELD_NAME));
        String operationTypeKeyValue = JsonInterceptorUtil.buildKeyNullValue(OPERATION_TYPE_FIELD_NAME);
        Assert.assertTrue(String.format("The operation type should have been null since the accesses values input do not" +
                        " have resolving in the configuration. event: %s", eventBody),
                eventBody.contains(operationTypeKeyValue));
    }

    private String wrapWithDollar(String fieldName){
        return String.format("${%s}", fieldName);
    }

    private String dollarCaseValueTest(String eventCode, String aliasHostValue, String hostSrcValue){
        Context ctx = new Context();
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.ORIGIN_FIELD_CONF_NAME, EVENT_CODE_FIELD_NAME);
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.DESTINATION_FIELD_CONF_NAME, SRC_MACHINE);
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.CASES_CONF_NAME, String.join(CASES_DELIM, "4624", "4776", "4769"));
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.CASES_DELIM_CONF_NAME, CASES_DELIM);
        ctx.put(JsonFieldSwitchCaseInterceptor.Builder.CASES_VALUES_CONF_NAME,
                String.join(";",wrapWithDollar(ALIAS_HOST_FIELD_NAME), wrapWithDollar(HOST_SRC_FIELD_NAME),wrapWithDollar(ALIAS_HOST_FIELD_NAME)));

        builder.configure(ctx);

        Interceptor interceptor = builder.build();
        MockMonitorInitiator.setMockMonitor(interceptor);

        ArrayList<String> fields = new ArrayList<>();
        String eventCodeKeyValue = JsonInterceptorUtil.buildKeyValue(EVENT_CODE_FIELD_NAME, eventCode);
        fields.add(eventCodeKeyValue);
        String aliasHostKeyValue = JsonInterceptorUtil.buildKeyValue(ALIAS_HOST_FIELD_NAME, aliasHostValue);
        fields.add(aliasHostKeyValue);
        String hostSrcKeyValue = JsonInterceptorUtil.buildKeyValue(HOST_SRC_FIELD_NAME, hostSrcValue);
        fields.add(hostSrcKeyValue);

        Event event = JsonInterceptorUtil.buildEvent(fields);

        event = interceptor.intercept(event);
        Assert.assertNotNull(event);

        String eventBody = new String(event.getBody());
        Assert.assertTrue(String.format("The following key value has been removed or changed. key value: %s, event: %s",
                eventCodeKeyValue, eventBody),eventBody.contains(eventCodeKeyValue));
        Assert.assertTrue(String.format("The following key value has been removed or changed. key value: %s, event: %s",
                aliasHostKeyValue, eventBody),eventBody.contains(aliasHostKeyValue));
        Assert.assertTrue(String.format("The following key value has been removed or changed. key value: %s, event: %s",
                hostSrcKeyValue, eventBody),eventBody.contains(hostSrcKeyValue));
        Assert.assertTrue(String.format("The %s field has not been added. event: %s", SRC_MACHINE, eventBody),
                eventBody.contains(SRC_MACHINE));

        return eventBody;
    }

    @Test
    public void dollarCaseValueTest1(){
        String eventCode = "4444";
        String aliasHostValue = "aliasHostMachine";
        String hostSrcValue = "hostSrcValue";
        String eventBody = dollarCaseValueTest(eventCode,aliasHostValue, hostSrcValue);

        String srcMachineKeyValue = JsonInterceptorUtil.buildKeyNullValue(SRC_MACHINE);
        Assert.assertTrue(String.format("The %s should have been null since %s" +
                        " have no mapping in the configuration. event: %s", SRC_MACHINE, eventCode, eventBody),
                eventBody.contains(srcMachineKeyValue));
    }

    @Test
    public void dollarCaseValueTest2(){
        String eventCode = "4624";
        String aliasHostValue = "aliasHostMachine";
        String hostSrcValue = "hostSrcValue";
        String eventBody = dollarCaseValueTest(eventCode,aliasHostValue, hostSrcValue);

        String srcMachineKeyValue = JsonInterceptorUtil.buildKeyValue(SRC_MACHINE, aliasHostValue);
        Assert.assertTrue(String.format("The %s field has been added with the wrong value. expected key value: %s, event: %s", SRC_MACHINE, srcMachineKeyValue, eventBody),
                eventBody.contains(srcMachineKeyValue));

        eventCode = "4769";
        eventBody = dollarCaseValueTest(eventCode,aliasHostValue, hostSrcValue);
        Assert.assertTrue(String.format("The %s field has been added with the wrong value. expected key value: %s, event: %s", SRC_MACHINE, srcMachineKeyValue, eventBody),
                eventBody.contains(srcMachineKeyValue));
    }

    @Test
    public void dollarCaseValueTest3(){
        String eventCode = "4776";
        String aliasHostValue = "aliasHostMachine";
        String hostSrcValue = "hostSrcValue";
        String eventBody = dollarCaseValueTest(eventCode,aliasHostValue, hostSrcValue);


        String srcMachineKeyValue = JsonInterceptorUtil.buildKeyValue(SRC_MACHINE, hostSrcValue);
        Assert.assertTrue(String.format("The %s field has been added with the wrong value. expected key value: %s, event: %s", SRC_MACHINE, srcMachineKeyValue, eventBody),
                eventBody.contains(srcMachineKeyValue));
    }

    @Test
    public void hierarchicalDestinationFieldTest() {
        Builder builder = new Builder();
        Context context = mock(Context.class);
        when(context.getString(eq(ORIGIN_FIELD_CONF_NAME))).thenReturn("reference_id");
        when(context.getString(eq(DESTINATION_FIELD_CONF_NAME))).thenReturn("additionalInfo.secondaryObjectId");
        when(context.getString(eq(CASES_DELIM_CONF_NAME), anyString())).thenReturn(",");
        when(context.getString(eq(CASES_CONF_NAME), anyString())).thenReturn("4733,4728,4756,4757,4717,4729,4732");
        when(context.getString(eq(CASES_VALUES_CONF_NAME), anyString())).thenReturn("${group};${group};${group};${group};${accesses};${group};${group}");
        builder.doConfigure(context);
        AbstractPresidioJsonInterceptor interceptor = builder.doBuild();
        Event event = new JSONEvent();

        // additionalInfo should be added, and a new key-value pair should be added
        JSONObject jsonObject = new JSONObject()
                .put("reference_id", "4733")
                .put("group", "My Group");
        event.setBody(jsonObject.toString().getBytes());
        jsonObject = new JSONObject(new String(interceptor.doIntercept(event).getBody()));
        Assert.assertEquals("My Group", jsonObject.getJSONObject("additionalInfo").getString("secondaryObjectId"));

        // additionalInfo is present with an existing key-value pair, and a new key-value pair should be added
        jsonObject = new JSONObject()
                .put("reference_id", "4728")
                .put("group", "Your Group")
                .put("additionalInfo", new JSONObject(singletonMap("yetAnotherKey", "yetAnotherValue")));
        event.setBody(jsonObject.toString().getBytes());
        jsonObject = new JSONObject(new String(interceptor.doIntercept(event).getBody()));
        Assert.assertEquals("yetAnotherValue", jsonObject.getJSONObject("additionalInfo").getString("yetAnotherKey"));
        Assert.assertEquals("Your Group", jsonObject.getJSONObject("additionalInfo").getString("secondaryObjectId"));

        // additionalInfo is present with an existing key-value pair, and this key-value pair should be overwritten
        jsonObject = new JSONObject()
                .put("reference_id", "4756")
                .put("group", "Our Group")
                .put("additionalInfo", new JSONObject(singletonMap("secondaryObjectId", "yetAnotherValue")));
        event.setBody(jsonObject.toString().getBytes());
        jsonObject = new JSONObject(new String(interceptor.doIntercept(event).getBody()));
        Assert.assertEquals(1, jsonObject.getJSONObject("additionalInfo").length());
        Assert.assertEquals("Our Group", jsonObject.getJSONObject("additionalInfo").getString("secondaryObjectId"));

        when(context.getString(eq(DESTINATION_FIELD_CONF_NAME))).thenReturn("first.second.third");
        builder.doConfigure(context);
        interceptor = builder.doBuild();

        // first, second and third should be added
        jsonObject = new JSONObject()
                .put("reference_id", "4757")
                .put("group", "Wizards");
        event.setBody(jsonObject.toString().getBytes());
        jsonObject = new JSONObject(new String(interceptor.doIntercept(event).getBody()));
        Assert.assertEquals("Wizards", jsonObject.getJSONObject("first").getJSONObject("second").getString("third"));

        // first is present, and second and third should be added
        jsonObject = new JSONObject()
                .put("reference_id", "4717")
                .put("accesses", "Lizards")
                .put("first", new JSONObject(singletonMap("yetAnotherKey", "yetAnotherValue")));
        event.setBody(jsonObject.toString().getBytes());
        jsonObject = new JSONObject(new String(interceptor.doIntercept(event).getBody()));
        Assert.assertEquals("yetAnotherValue", jsonObject.getJSONObject("first").getString("yetAnotherKey"));
        Assert.assertEquals("Lizards", jsonObject.getJSONObject("first").getJSONObject("second").getString("third"));
    }
}
