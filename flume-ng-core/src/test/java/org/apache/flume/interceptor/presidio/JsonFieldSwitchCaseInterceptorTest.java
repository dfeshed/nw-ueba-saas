package org.apache.flume.interceptor.presidio;

import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.InterceptorBuilderFactory;
import org.apache.flume.interceptor.InterceptorType;
import org.apache.flume.tools.MockMonitorInitiator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


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

    private Interceptor initInteceptorWithAccessesContext(){
        return initInteceptorWithAccessesContext(false);
    }

    private Interceptor initInteceptorWithAccessesContext(boolean isAddConditionConfiguration){
        Context ctx = createContextWithAccessesConfiguration(isAddConditionConfiguration);

        builder.configure(ctx);

        return builder.build();
    }

    private Event buildEvent(List<String> fields){
        StringBuilder eventBuilder = new StringBuilder();
        eventBuilder.append("{");
        boolean isFirst = true;
        for(String field: fields){
            if(!isFirst){
                eventBuilder.append(",");
            } else {
                isFirst = false;
            }
            eventBuilder.append(field);
        }
        eventBuilder.append("}");

        return EventBuilder.withBody(eventBuilder.toString(), Charsets.UTF_8);
    }

    private String buildKeyValue(String key, String value){
        return String.format("\"%s\":\"%s\"", key, value);
    }

    private String buildKeyNullValue(String key){
        return String.format("\"%s\":null", key);
    }

    private void interceptEventAndTestOperationType(boolean includeEventCodeConf, String eventCode, String accessesValue, String expectedOperationType){
        String eventBody = interceptEvent(includeEventCodeConf,eventCode, accessesValue);

        Assert.assertTrue(String.format("The operation type field has not been added. event: %s", eventBody),
                eventBody.contains(OPERATION_TYPE_FIELD_NAME));
        String operationTypeKeyValue = buildKeyValue(OPERATION_TYPE_FIELD_NAME, expectedOperationType);
        Assert.assertTrue(String.format("The operation type field has been added incorrectly. expected key value: %s, event: %s", operationTypeKeyValue, eventBody),
                eventBody.contains(operationTypeKeyValue));
    }

    private String interceptEvent(boolean includeEventCodeConf, String eventCode, String accessesValue){
        Interceptor interceptor = initInteceptorWithAccessesContext(includeEventCodeConf);
        MockMonitorInitiator.setMockMonitor(interceptor);
        ArrayList<String> fields = new ArrayList<>();
        String accessesKeyValue = buildKeyValue(ACCESSES_FIELD_NAME, accessesValue);
        fields.add(accessesKeyValue);
        String eventCodeKeyValue = null;
        if(eventCode!=null) {
            eventCodeKeyValue = buildKeyValue(EVENT_CODE_FIELD_NAME, eventCode);
            fields.add(eventCodeKeyValue);
        }

        Event event = buildEvent(fields);

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
        String operationTypeKeyValue = buildKeyNullValue(OPERATION_TYPE_FIELD_NAME);
        Assert.assertTrue(String.format("The operation type should have been null since the accesses values input do not" +
                        " have resoving in the configuration. event: %s", eventBody),
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
        String eventCodeKeyValue = buildKeyValue(EVENT_CODE_FIELD_NAME, eventCode);
        fields.add(eventCodeKeyValue);
        String aliasHostKeyValue = buildKeyValue(ALIAS_HOST_FIELD_NAME, aliasHostValue);
        fields.add(aliasHostKeyValue);
        String hostSrcKeyValue = buildKeyValue(HOST_SRC_FIELD_NAME, hostSrcValue);
        fields.add(hostSrcKeyValue);

        Event event = buildEvent(fields);

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

        String srcMachineKeyValue = buildKeyNullValue(SRC_MACHINE);
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

        String srcMachineKeyValue = buildKeyValue(SRC_MACHINE, aliasHostValue);
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


        String srcMachineKeyValue = buildKeyValue(SRC_MACHINE, hostSrcValue);
        Assert.assertTrue(String.format("The %s field has been added with the wrong value. expected key value: %s, event: %s", SRC_MACHINE, srcMachineKeyValue, eventBody),
                eventBody.contains(srcMachineKeyValue));
    }
}
