package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import fortscale.utils.transform.predicate.JsonObjectChainPredicate;
import fortscale.utils.transform.predicate.JsonObjectKeyExistPredicate;
import fortscale.utils.transform.predicate.JsonObjectRegexPredicate;
import fortscale.utils.transform.regexcaptureandformat.CaptureAndFormatConfiguration;
import fortscale.utils.transform.regexcaptureandformat.CapturingGroupConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fortscale.utils.transform.predicate.JsonObjectChainPredicate.LogicalOperation.AND;


public class AuthenticationWindowsAuditTransformerTest extends TransformerTest{

    private static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    private static final String SERVICE_NAME_FIELD_NAME = "service_name";
    private static final String LOGON_TYPE_FIELD_NAME = "logon_type";
    private static final String EVENT_CODE_FIELD_NAME = "reference_id";
    private static final String USER_DST_FIELD_NAME = "user_dst";
    private static final String DEVICE_TYPE_FIELD_NAME = "device_type";
    private static final String USER_ID_FIELD_NAME = "userId";
    private static final String EVENT_TIME_FIELD_NAME = "event_time";
    private static final String EVENT_TYPE_FIELD_NAME = "event_type";
    private static final String RESULT_FIELD_NAME = "result";
    private static final String RESULT_CODE_FIELD_NAME = "result_code";
    private static final String EVENT_ID_FIELD_NAME = "eventId";
    private static final String EVENT_SOURCE_ID_FIELD_NAME = "event_source_id";
    private static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    private static final String DATE_TIME_FIELD_NAME = "dateTime";
    private static final String USERNAME_FIELD_NAME = "userName";
    private static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    private static final String ALIAS_HOST_FIELD_NAME = "alias_host";
    private static final String HOST_SRC_FIELD_NAME = "host_src";
    private static final String SRC_MACHINE_ID_FIELD_NAME = "srcMachineId";
    private static final String SRC_MACHINE_NAME_FIELD_NAME = "srcMachineName";
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_FAILURE = "FAILURE";

    private IJsonObjectTransformer buildAuthenticationWindowsAuditTransformer(){
        List<IJsonObjectTransformer> transformerChainList = new ArrayList<>();

        // Filtering events according to the device type and user name
        JsonObjectRegexPredicate userDstNotContainMachine = new JsonObjectRegexPredicate("user-dst-not-contain-machine", USER_DST_FIELD_NAME, "[^\\$]*");
        JsonObjectRegexPredicate deviceTypeSnareOrNic = new JsonObjectRegexPredicate("device-type-snare-or-nic", DEVICE_TYPE_FIELD_NAME, "winevent_snare|winevent_nic");
        JsonObjectChainPredicate deviceTypeAndUserDstPredicate = new JsonObjectChainPredicate("device-type-and-user-dst-predicate",AND,
                Arrays.asList(userDstNotContainMachine, deviceTypeSnareOrNic));
        FilterTransformer deviceTypeAndUserDstFilter = new FilterTransformer("device-type-and-user-dst-filter", deviceTypeAndUserDstPredicate, true);
        transformerChainList.add(deviceTypeAndUserDstFilter);

        // for 4769: Filtering out events with service name equals to 'krbtgt'
        JsonObjectRegexPredicate serviceNameEqualKrbtgt = new JsonObjectRegexPredicate("service-name-equal-krbtgt", SERVICE_NAME_FIELD_NAME, "krbtgt");
        FilterTransformer serviceNameFilter = new FilterTransformer("service-name-filter", serviceNameEqualKrbtgt, false);
        JsonObjectRegexPredicate referenceIdEqual4769 = new JsonObjectRegexPredicate("reference-id-equal-4769", EVENT_CODE_FIELD_NAME, "4769");
        IfElseTransformer serviceNameFilterFor4769 =
                new IfElseTransformer("service-name-filter-for-4769",referenceIdEqual4769, serviceNameFilter);
        transformerChainList.add(serviceNameFilterFor4769);

        //for 4624 and 4625: Filtering in events with logon type equals to 2 or 10 ###
        JsonObjectRegexPredicate logonTypeEqual2Or10 = new JsonObjectRegexPredicate("logon-type-equal-0-or-10", LOGON_TYPE_FIELD_NAME, "2|10");
        FilterTransformer logonTypeFilter = new FilterTransformer("logon-type-filter", logonTypeEqual2Or10, true);
        JsonObjectRegexPredicate referenceIdEqual4624Or4625= new JsonObjectRegexPredicate("reference-id-equal-4624-or-4625", EVENT_CODE_FIELD_NAME, "4624|4625");
        IfElseTransformer logonTypeFilterFor4624Or4625 =
                new IfElseTransformer("logon-type-filter-for-4624-or-4625",referenceIdEqual4624Or4625, logonTypeFilter);
        transformerChainList.add(logonTypeFilterFor4624Or4625);

        //Convert time field from EPOCH millis to EPOCH seconds
        EpochTimeToNanoRepresentationTransformer dateTimeMillisToSeconds =
                new EpochTimeToNanoRepresentationTransformer("date-time-millis-to-nano-representation", EVENT_TIME_FIELD_NAME, DATE_TIME_FIELD_NAME);
        transformerChainList.add(dateTimeMillisToSeconds);

        //Filling the srcMachineName. The value is taken from different fields depends on the reference_id value
        List<SwitchCaseTransformer.SwitchCase> srcMachineNameCases = new ArrayList<>();
        srcMachineNameCases.add(new SwitchCaseTransformer.SwitchCase("4624",String.format("${%s[0]}", ALIAS_HOST_FIELD_NAME)));
        srcMachineNameCases.add(new SwitchCaseTransformer.SwitchCase("4625",String.format("${%s[0]}", ALIAS_HOST_FIELD_NAME)));
        srcMachineNameCases.add(new SwitchCaseTransformer.SwitchCase("4776",String.format("${%s}", HOST_SRC_FIELD_NAME)));
        SwitchCaseTransformer srcMachineNameSwitchCaseTransformer =
                new SwitchCaseTransformer("src-machine-id-switch-case",
                        EVENT_CODE_FIELD_NAME,
                        SRC_MACHINE_NAME_FIELD_NAME,
                        null,
                        srcMachineNameCases);
        transformerChainList.add(srcMachineNameSwitchCaseTransformer);

        // Normalize the srcMachineId values
            CaptureAndFormatConfiguration srcMachineIdNormalizationFirstPattern =
                    new CaptureAndFormatConfiguration(".*:.*", "", null);
        CaptureAndFormatConfiguration srcMachineIdNormalizationSecondPattern =
                new CaptureAndFormatConfiguration(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*", "",null);
        CaptureAndFormatConfiguration srcMachineIdNormalizationThirdPattern =
                new CaptureAndFormatConfiguration("(\\\\\\\\)?([^\\.]+)\\..+", "%s",
                        Arrays.asList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration srcMachineIdNormalizationFourthPattern =
                new CaptureAndFormatConfiguration("(\\\\\\\\)?(.+)", "%s",
                Arrays.asList(new CapturingGroupConfiguration(2, "LOWER")));
        RegexCaptorAndFormatter srcMachineIdNormalization =
                new RegexCaptorAndFormatter("src-machine-id-normalization",
                        SRC_MACHINE_NAME_FIELD_NAME,
                        SRC_MACHINE_ID_FIELD_NAME,
                Arrays.asList(srcMachineIdNormalizationFirstPattern, srcMachineIdNormalizationSecondPattern,
                        srcMachineIdNormalizationThirdPattern, srcMachineIdNormalizationFourthPattern));
        transformerChainList.add(srcMachineIdNormalization);

        // Normalize the userId values
        CaptureAndFormatConfiguration userNormalizationFirstPattern = new CaptureAndFormatConfiguration("CN=([^,]+)", "%s",
                Arrays.asList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationSecondPattern = new CaptureAndFormatConfiguration("CN=([^,]+),.+", "%s",
                Arrays.asList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationThirdPattern = new CaptureAndFormatConfiguration("(.+\\\\)+(.+)@.+", "%s",
                Arrays.asList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationFourthPattern = new CaptureAndFormatConfiguration("(.+\\\\)+([^@]+)", "%s",
                Arrays.asList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationFifthPattern = new CaptureAndFormatConfiguration("(.+)@.+", "%s",
                Arrays.asList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationSixthPattern = new CaptureAndFormatConfiguration(".+", "%s",
                Arrays.asList(new CapturingGroupConfiguration(0, "LOWER")));
        RegexCaptorAndFormatter userIdNormalization = new RegexCaptorAndFormatter("user-id-normalization", USER_DST_FIELD_NAME, USER_ID_FIELD_NAME,
                Arrays.asList(userNormalizationFirstPattern, userNormalizationSecondPattern, userNormalizationThirdPattern,
                        userNormalizationFourthPattern, userNormalizationFifthPattern, userNormalizationSixthPattern));
        transformerChainList.add(userIdNormalization);

        //Normalize the result values
        CaptureAndFormatConfiguration resultFailedPattern = new CaptureAndFormatConfiguration(".*(?i:fail).*", RESULT_FAILURE, null);
        CaptureAndFormatConfiguration resultSuccessPattern = new CaptureAndFormatConfiguration(".*(?i:succ).*", RESULT_SUCCESS, null);
        RegexCaptorAndFormatter resultNormalizationOnEventType =
                new RegexCaptorAndFormatter("result-normalization-on-event-type", EVENT_TYPE_FIELD_NAME, RESULT_FIELD_NAME,
                        Arrays.asList(resultFailedPattern, resultSuccessPattern));
        List<SwitchCaseTransformer.SwitchCase> resultNormalizationOnResultCodeCases = new ArrayList<>();
        resultNormalizationOnResultCodeCases.add(new SwitchCaseTransformer.SwitchCase("0x0", RESULT_SUCCESS));
        resultNormalizationOnResultCodeCases.add(new SwitchCaseTransformer.SwitchCase("0x.*", RESULT_FAILURE, true));
        SwitchCaseTransformer resultNormalizationOnResultCodeSwitchCaseTransformer =
                new SwitchCaseTransformer("result-normalization-on-result-code",RESULT_CODE_FIELD_NAME,
                        RESULT_FIELD_NAME, null,resultNormalizationOnResultCodeCases);
        JsonObjectKeyExistPredicate eventTypeKeyExist = new JsonObjectKeyExistPredicate("event-type-exist", EVENT_TYPE_FIELD_NAME);
        IfElseTransformer resultNormalizationTransformer =
                new IfElseTransformer(
                        "result-normalization",
                        eventTypeKeyExist,
                        resultNormalizationOnEventType,
                        resultNormalizationOnResultCodeSwitchCaseTransformer);
        transformerChainList.add(resultNormalizationTransformer);

        //rename event_source_id to eventId
        CopyValueTransformer ranameEventSourceIdToEventId =
                new CopyValueTransformer(
                        "rename-event-source-id-to-event-id",
                        EVENT_SOURCE_ID_FIELD_NAME,
                        true,
                        Collections.singletonList(EVENT_ID_FIELD_NAME));
        transformerChainList.add(ranameEventSourceIdToEventId);

        //rename reference_id to dataSource
        CopyValueTransformer ranameReferenceIdToDataSource =
                new CopyValueTransformer(
                        "rename-reference-id-to-data-source",
                        EVENT_CODE_FIELD_NAME,
                        true,
                        Collections.singletonList(DATA_SOURCE_FIELD_NAME));
        transformerChainList.add(ranameReferenceIdToDataSource);
        //rename event_type to operationType
        CopyValueTransformer ranameEventTypeToOperationType =
                new CopyValueTransformer(
                        "rename-event-type-to-operation-type",
                        EVENT_TYPE_FIELD_NAME,
                        true,
                        Collections.singletonList(OPERATION_TYPE_FIELD_NAME));
        transformerChainList.add(ranameEventTypeToOperationType);

        // copy user_dst to userName,userDisplayName
        CopyValueTransformer copyUserDst =
                new CopyValueTransformer(
                        "copy-user-dst",
                        USER_DST_FIELD_NAME,
                        true,
                        Arrays.asList(USERNAME_FIELD_NAME, USER_DISPLAY_NAME_FIELD_NAME));
        transformerChainList.add(copyUserDst);

        //The Auth Windows Audit Transformer that chain all the transformers together.
        JsonObjectChainTransformer authWindowsAuditTransformer =
                new JsonObjectChainTransformer("auth-windows-audit-transformer", transformerChainList);

        return authWindowsAuditTransformer;
    }

    private JSONObject buildAuthWindowAuditJsonObject(
            String eventCode,
            String userDst,
            String deviceType,
            String serviceName,
            Long eventTime,
            String logonType,
            String aliasHost,
            String eventType,
            String resultCode,
            String eventId,
            String hostSource
    ){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVENT_CODE_FIELD_NAME, eventCode);
        jsonObject.put(USER_DST_FIELD_NAME, userDst);
        jsonObject.put(DEVICE_TYPE_FIELD_NAME, deviceType);
        jsonObject.put(SERVICE_NAME_FIELD_NAME, serviceName);
        jsonObject.put(LOGON_TYPE_FIELD_NAME, logonType);
        jsonObject.put(ALIAS_HOST_FIELD_NAME, new JSONArray(aliasHost));
        jsonObject.put(HOST_SRC_FIELD_NAME, hostSource);
        jsonObject.put(EVENT_TIME_FIELD_NAME, eventTime);
        jsonObject.put(EVENT_TYPE_FIELD_NAME, eventType);
        jsonObject.put(RESULT_CODE_FIELD_NAME, resultCode);
        jsonObject.put(EVENT_SOURCE_ID_FIELD_NAME, eventId);

        return jsonObject;
    }

    @Test
    public void deserialize_auth_trasformer_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthenticationWindowsAuditTransformer();

        String transformerJsonAsString = mapper.writeValueAsString(transformer);


        Assert.assertNotNull(transformerJsonAsString);
    }

    @Test
    public void filter_service_name_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthenticationWindowsAuditTransformer();

        JSONObject jsonObject = buildAuthWindowAuditJsonObject("4769", "testUser", "winevent_snare",
                "krbtgt", 1528124556000L, "2", "[\"someone-pc\"]", "Success Audit"
                , "  ", "10.25.67.33:50005:91168521", null);

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered since the service name is krbtgt", retJsonObject);
    }

    @Test
    public void filter_logon_type_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthenticationWindowsAuditTransformer();

        JSONObject jsonObject = buildAuthWindowAuditJsonObject("4624", "rsmith@montereytechgroup.com", "winevent_snare",
                null, 1528124556000L, "7", "[\"DESKTOP-LLHJ389\"]", "Success Audit"
                , "  ", "10.25.67.33:50005:91168521", null);

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered. events with logon type which is not 10 or 2 should be filtered out",
                retJsonObject);
    }

    @Test
    public void event_code_4624_option1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthenticationWindowsAuditTransformer();

        String referenceId = "4624";
        String userDst = "rsmith@montereytechgroup.com";
        String aliasHost = "DESKTOP-LLHJ389";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Success Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                null, eventTime*1000, "2",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, null);

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertOnExpectedValues(retJsonObject, eventId, eventTime, "rsmith", userDst, userDst,
                aliasHost.toLowerCase(), aliasHost, RESULT_SUCCESS, eventType, referenceId);
    }

    @Test
    public void event_code_4776_option1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthenticationWindowsAuditTransformer();

        String referenceId = "4776";
        String userDst = "CN=BOBBY,OU=Users,DC=Dell";
        String aliasHost = "DESKTOP-LLHJ389";
        String hostSource = "a:b";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Failure Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                null, eventTime*1000, "10",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, hostSource);

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertOnExpectedValues(retJsonObject, eventId, eventTime, "bobby", userDst, userDst,
                "", hostSource, RESULT_FAILURE, eventType, referenceId);
    }

    @Test
    public void event_code_4769_option1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthenticationWindowsAuditTransformer();

        String referenceId = "4769";
        String userDst = "CN=BOBBY,OU=Users,DC=Dell";
        String aliasHost = "DESKTOP-LLHJ389";
        String hostSource = "a:b";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Failure Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                null, eventTime*1000, "10",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, hostSource);

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertOnExpectedValues(retJsonObject, eventId, eventTime, "bobby", userDst, userDst,
                null, JSONObject.NULL, RESULT_FAILURE, eventType, referenceId);
    }

    private void assertOnExpectedValues(JSONObject retJsonObject,
                                        String expectedEventId,
                                        Long expectedDateTime,
                                        String expectedUserId,
                                        String expectedUsername,
                                        String expectedUserDisplayName,
                                        String expectedSrcMachineId,
                                        Object expectedSrcMachineName,
                                        String expectedResult,
                                        String expectedOperationType,
                                        String expectedDataSource){
        Assert.assertEquals("wrong event id", expectedEventId, retJsonObject.get(EVENT_ID_FIELD_NAME));
        Assert.assertEquals("wrong dateTime", new Double(expectedDateTime), retJsonObject.get(DATE_TIME_FIELD_NAME));
        Assert.assertEquals("username normalization did not work", expectedUserId, retJsonObject.get(USER_ID_FIELD_NAME));
        Assert.assertEquals("wrong username", expectedUsername, retJsonObject.get(USERNAME_FIELD_NAME));
        Assert.assertEquals("wrong userDisplayName", expectedUserDisplayName, retJsonObject.get(USER_DISPLAY_NAME_FIELD_NAME));
        Assert.assertEquals("source machine id is not as expected", expectedSrcMachineId, retJsonObject.opt(SRC_MACHINE_ID_FIELD_NAME));
        Assert.assertEquals("source machine name is not as expected", expectedSrcMachineName, retJsonObject.opt(SRC_MACHINE_NAME_FIELD_NAME));
        Assert.assertEquals("result normalization did not work", expectedResult, retJsonObject.get(RESULT_FIELD_NAME));
        Assert.assertEquals("operation type logic according the accesses field did not work", expectedOperationType, retJsonObject.get(OPERATION_TYPE_FIELD_NAME));
        Assert.assertEquals("wrong data source", expectedDataSource, retJsonObject.get(DATA_SOURCE_FIELD_NAME));
    }
}
