package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import fortscale.utils.transform.predicate.JsonObjectChainPredicate;
import fortscale.utils.transform.predicate.JsonObjectKeyExistPredicate;
import fortscale.utils.transform.predicate.JsonObjectRegexPredicate;
import fortscale.utils.transform.regexcaptureandformat.CaptureAndFormatConfiguration;
import fortscale.utils.transform.regexcaptureandformat.CapturingGroupConfiguration;
import fortscale.utils.transform.regexcaptureandformat.RegexCaptorAndFormatter;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fortscale.utils.transform.predicate.JsonObjectChainPredicate.LogicalOperation.AND;

public class AuthenticationWindowsAuditTransformerTest extends TransformerTest {
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
    private static final String ALIAS_SRC_FIELD_NAME = "alias_src";
    private static final String HOST_SRC_FIELD_NAME = "host_src";
    private static final String HOST_DST_FIELD_NAME = "host_dst";
    private static final String SRC_MACHINE_ID_FIELD_NAME = "srcMachineId";
    private static final String SRC_MACHINE_NAME_FIELD_NAME = "srcMachineName";
    private static final String DST_MACHINE_ID_FIELD_NAME = "dstMachineId";
    private static final String DST_MACHINE_NAME_FIELD_NAME = "dstMachineName";
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_FAILURE = "FAILURE";
    private static final String INTERACTIVE_LOGON_TYPE = "INTERACTIVE";
    private static final String REMOTE_INTERACTIVE_LOGON_TYPE = "REMOTE_INTERACTIVE";
    private static final String CREDENTIAL_VALIDATION_OPERATION_TYPE = "CREDENTIAL_VALIDATION";
    private static final String EXPLICIT_CREDENTIALS_LOGON = "EXPLICIT_CREDENTIALS_LOGON";
    private static final String SESSION_ID_FIELD_NAME = "sessionid";
    private static final String EC_OUTCOME_FIELD_NAME = "ec_outcome";
    private static final String EC_ACTIVITY_FIELD_NAME = "ec.activity";
    private static final String RSAACESRV_DEVICE_TYPE = "rsaacesrv";
    private static final String ACTION_FIELD_NAME = "action";
    private static final String USER_SRC_FIELD_NAME = "user_src";
    private static final String RHLINUX_DEVICE_TYPE = "rhlinux";


    private String wrapWithDollar(String fieldName) {
        return String.format("${%s}", fieldName);
    }


    private IJsonObjectTransformer buildAuthenticationLinuxAuthTransformer() {
        List<IJsonObjectTransformer> transformerChainList = new ArrayList<>();

        //filter non auth actions
        JsonObjectRegexPredicate actionIsAuth = new JsonObjectRegexPredicate("action-is-login-or-sshd", ACTION_FIELD_NAME, "\\/usr\\/bin\\/login|\\/usr\\/sbin\\/sshd");
        FilterTransformer filterNonAuthActions =
                new FilterTransformer("action-is-auth-filter", actionIsAuth, true);
        transformerChainList.add(filterNonAuthActions);
        //filter empty user.src
        JsonObjectRegexPredicate unkonwnUser = new JsonObjectRegexPredicate("user-unknown", USER_SRC_FIELD_NAME, "^$|\\(unknown\\)");
        FilterTransformer filterUnknownUser=
                new FilterTransformer("user-is-unknown", unkonwnUser, false);
        transformerChainList.add(filterUnknownUser);
        // filter irrelevant auth events (session start and end)
        JsonObjectRegexPredicate authEventType = new JsonObjectRegexPredicate("event-type-is-auth", EVENT_TYPE_FIELD_NAME, "USER_LOGIN|CRED_ACQ|USER_AUTH");
        FilterTransformer filterEventType=
                new FilterTransformer("event-type-is-auth", authEventType, true);
        transformerChainList.add(filterEventType);

        //Convert time field from EPOCH millis to EPOCH seconds
        EpochTimeToNanoRepresentationTransformer dateTimeMillisToSeconds =
                new EpochTimeToNanoRepresentationTransformer("date-time-millis-to-nano-representation", EVENT_TIME_FIELD_NAME, DATE_TIME_FIELD_NAME);
        transformerChainList.add(dateTimeMillisToSeconds);

        //Create capture and format list for source machines and destination machines
        CaptureAndFormatConfiguration machineIdNormalizationZeroPattern =
                new CaptureAndFormatConfiguration("-", "", null);
        CaptureAndFormatConfiguration machineIdNormalizationFirstPattern =
                new CaptureAndFormatConfiguration(".*:.*", "", null);
        CaptureAndFormatConfiguration machineIdNormalizationSecondPattern =
                new CaptureAndFormatConfiguration(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*", "", null);
        CaptureAndFormatConfiguration machineIdNormalizationThirdPattern =
                new CaptureAndFormatConfiguration("(\\\\\\\\)?([^\\.]+)\\..+", "%s",
                        Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration machineIdNormalizationFourthPattern =
                new CaptureAndFormatConfiguration("(\\\\\\\\)?(.+)", "%s",
                        Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));

        List<CaptureAndFormatConfiguration> srcAndDstMachineCaptureAndFormatConfigurationList = Arrays.asList(
                machineIdNormalizationZeroPattern,
                machineIdNormalizationFirstPattern,
                machineIdNormalizationSecondPattern,
                machineIdNormalizationThirdPattern,
                machineIdNormalizationFourthPattern);

        // Normalize the srcMachineId values
        RegexCaptorAndFormatter srcMachineIdNormalization =
                new RegexCaptorAndFormatter("src-machine-id-normalization",
                        HOST_SRC_FIELD_NAME,
                        SRC_MACHINE_ID_FIELD_NAME,
                        srcAndDstMachineCaptureAndFormatConfigurationList);
        transformerChainList.add(srcMachineIdNormalization);


        // Normalize the userId values
        CaptureAndFormatConfiguration userNormalizationFirstPattern = new CaptureAndFormatConfiguration("CN=([^,]+)", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationSecondPattern = new CaptureAndFormatConfiguration("CN=([^,]+),.+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationThirdPattern = new CaptureAndFormatConfiguration("(.+\\\\)+(.+)@.+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationFourthPattern = new CaptureAndFormatConfiguration("(.+\\\\)+([^@]+)", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationFifthPattern = new CaptureAndFormatConfiguration("(.+)@.+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationSixthPattern = new CaptureAndFormatConfiguration(".+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(0, "LOWER")));
        RegexCaptorAndFormatter userIdNormalization = new RegexCaptorAndFormatter("user-id-normalization", USER_SRC_FIELD_NAME, USER_ID_FIELD_NAME,
                Arrays.asList(userNormalizationFirstPattern, userNormalizationSecondPattern, userNormalizationThirdPattern,
                        userNormalizationFourthPattern, userNormalizationFifthPattern, userNormalizationSixthPattern));
        transformerChainList.add(userIdNormalization);

        //Normalize the result values
        CaptureAndFormatConfiguration resultFailedPattern = new CaptureAndFormatConfiguration(".*(?i:fail).*", RESULT_FAILURE, null);
        CaptureAndFormatConfiguration resultSuccessPattern = new CaptureAndFormatConfiguration(".*(?i:succ).*", RESULT_SUCCESS, null);
        RegexCaptorAndFormatter resultNormalizationTransformer =
                new RegexCaptorAndFormatter("result-normalization", RESULT_FIELD_NAME, RESULT_FIELD_NAME,
                        Arrays.asList(resultFailedPattern, resultSuccessPattern));

        transformerChainList.add(resultNormalizationTransformer);


        CopyValueTransformer copyEventTypeToOperationType=
                new CopyValueTransformer(
                        "copy-operation-type",
                        EVENT_TYPE_FIELD_NAME,
                        false,
                        Arrays.asList(OPERATION_TYPE_FIELD_NAME));
        transformerChainList.add(copyEventTypeToOperationType);


        //rename sessionId to eventId
        CopyValueTransformer renameSessionIdToEventId =
                new CopyValueTransformer(
                        "rename-session-id-to-event-id",
                        SESSION_ID_FIELD_NAME,
                        true,
                        Collections.singletonList(EVENT_ID_FIELD_NAME));
        transformerChainList.add(renameSessionIdToEventId);

        //rename action to dataSource
        CopyValueTransformer renameActionToDataSource =
                new CopyValueTransformer(
                        "rename-action-to-data-source",
                        ACTION_FIELD_NAME,
                        true,
                        Collections.singletonList(DATA_SOURCE_FIELD_NAME));
        transformerChainList.add(renameActionToDataSource);

        // copy user_id to userName
        CopyValueTransformer copyUserId =
                new CopyValueTransformer(
                        "copy-user-id",
                        USER_ID_FIELD_NAME,
                        false,
                        Arrays.asList(USER_DISPLAY_NAME_FIELD_NAME, USERNAME_FIELD_NAME));
        transformerChainList.add(copyUserId);

        CopyValueTransformer copySrcMachineName=
                new CopyValueTransformer("copy-src-machine-name",
                        SRC_MACHINE_ID_FIELD_NAME,
                        false,
                        Arrays.asList(SRC_MACHINE_NAME_FIELD_NAME));
        transformerChainList.add(copySrcMachineName);

        //The SecureId Transformer that chain all the transformers together.
        return new JsonObjectChainTransformer("auth-linux-transformer", transformerChainList);

    }

    private IJsonObjectTransformer buildAuthenticationSecureIdTransformer() {
        List<IJsonObjectTransformer> transformerChainList = new ArrayList<>();

        //Convert time field from EPOCH millis to EPOCH seconds
        EpochTimeToNanoRepresentationTransformer dateTimeMillisToSeconds =
                new EpochTimeToNanoRepresentationTransformer("date-time-millis-to-nano-representation", EVENT_TIME_FIELD_NAME, DATE_TIME_FIELD_NAME);
        transformerChainList.add(dateTimeMillisToSeconds);

        //Create capture and format list for source machines and destination machines
        CaptureAndFormatConfiguration machineIdNormalizationZeroPattern =
                new CaptureAndFormatConfiguration("-", "", null);
        CaptureAndFormatConfiguration machineIdNormalizationFirstPattern =
                new CaptureAndFormatConfiguration(".*:.*", "", null);
        CaptureAndFormatConfiguration machineIdNormalizationSecondPattern =
                new CaptureAndFormatConfiguration(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*", "", null);
        CaptureAndFormatConfiguration machineIdNormalizationThirdPattern =
                new CaptureAndFormatConfiguration("(\\\\\\\\)?([^\\.]+)\\..+", "%s",
                        Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration machineIdNormalizationFourthPattern =
                new CaptureAndFormatConfiguration("(\\\\\\\\)?(.+)", "%s",
                        Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));

        List<CaptureAndFormatConfiguration> srcAndDstMachineCaptureAndFormatConfigurationList = Arrays.asList(
                machineIdNormalizationZeroPattern,
                machineIdNormalizationFirstPattern,
                machineIdNormalizationSecondPattern,
                machineIdNormalizationThirdPattern,
                machineIdNormalizationFourthPattern);

        // Normalize the srcMachineId values
        RegexCaptorAndFormatter srcMachineIdNormalization =
                new RegexCaptorAndFormatter("src-machine-id-normalization",
                        HOST_SRC_FIELD_NAME,
                        SRC_MACHINE_ID_FIELD_NAME,
                        srcAndDstMachineCaptureAndFormatConfigurationList);
        transformerChainList.add(srcMachineIdNormalization);


        // Normalize the userId values
        CaptureAndFormatConfiguration userNormalizationFirstPattern = new CaptureAndFormatConfiguration("CN=([^,]+)", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationSecondPattern = new CaptureAndFormatConfiguration("CN=([^,]+),.+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationThirdPattern = new CaptureAndFormatConfiguration("(.+\\\\)+(.+)@.+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationFourthPattern = new CaptureAndFormatConfiguration("(.+\\\\)+([^@]+)", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationFifthPattern = new CaptureAndFormatConfiguration("(.+)@.+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationSixthPattern = new CaptureAndFormatConfiguration(".+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(0, "LOWER")));
        RegexCaptorAndFormatter userIdNormalization = new RegexCaptorAndFormatter("user-id-normalization", USER_DST_FIELD_NAME, USER_ID_FIELD_NAME,
                Arrays.asList(userNormalizationFirstPattern, userNormalizationSecondPattern, userNormalizationThirdPattern,
                        userNormalizationFourthPattern, userNormalizationFifthPattern, userNormalizationSixthPattern));
        transformerChainList.add(userIdNormalization);

        //Supporting ANONYMOUS LOGON and SYSTEM users
        JsonObjectRegexPredicate srcMachineNotBlank = new JsonObjectRegexPredicate("src-machine-not-blank", SRC_MACHINE_ID_FIELD_NAME, "^(?!\\s*$).+");
        FilterTransformer filterEventForAnonymousOrSystemUsersWithNoMachine =
                new FilterTransformer("anonymous-or-system-filter", srcMachineNotBlank, true);
        JoinTransformer createUserIdByJoiningMachineIdAndUserIdTransformer =
                new JoinTransformer("create-user-id-by-joining-machine-id-and-user-id", USER_ID_FIELD_NAME,
                        Arrays.asList(wrapWithDollar(USER_ID_FIELD_NAME), wrapWithDollar(SRC_MACHINE_ID_FIELD_NAME)),
                        "@");
        JsonObjectChainTransformer anonymousOrSystemUsersChainTransformer =
                new JsonObjectChainTransformer("anonymous-or-system-chain",
                        Arrays.asList(filterEventForAnonymousOrSystemUsersWithNoMachine, createUserIdByJoiningMachineIdAndUserIdTransformer));
        JsonObjectRegexPredicate anonymousOrSystemUsersPredicate =
                new JsonObjectRegexPredicate("user-equals-anonymous-or-system-predicate", USER_ID_FIELD_NAME, "anonymous logon|system");
        IfElseTransformer anonymousOrSystemUsersIfElseTransformer =
                new IfElseTransformer("user-equals-anonymous-or-system-if-else",
                        anonymousOrSystemUsersPredicate,
                        anonymousOrSystemUsersChainTransformer);
        transformerChainList.add(anonymousOrSystemUsersIfElseTransformer);

        //Normalize the result values
        CaptureAndFormatConfiguration resultFailedPattern = new CaptureAndFormatConfiguration(".*(?i:fail).*", RESULT_FAILURE, null);
        CaptureAndFormatConfiguration resultSuccessPattern = new CaptureAndFormatConfiguration(".*(?i:succ).*", RESULT_SUCCESS, null);
        RegexCaptorAndFormatter resultNormalizationTransformer =
                new RegexCaptorAndFormatter("result-normalization", EC_OUTCOME_FIELD_NAME, RESULT_FIELD_NAME,
                        Arrays.asList(resultFailedPattern, resultSuccessPattern));

        transformerChainList.add(resultNormalizationTransformer);

        //Fill operationType
        SetterTransformer operationTypeTransformer =
                new SetterTransformer("operation-type-transformer", OPERATION_TYPE_FIELD_NAME, "MFA");

        transformerChainList.add(operationTypeTransformer);

        //rename sessionId to eventId
        CopyValueTransformer renameSessionIdToEventId =
                new CopyValueTransformer(
                        "rename-session-id-to-event-id",
                        SESSION_ID_FIELD_NAME,
                        true,
                        Collections.singletonList(EVENT_ID_FIELD_NAME));
        transformerChainList.add(renameSessionIdToEventId);

        //rename reference_id to dataSource
        CopyValueTransformer renameDeviceTypeToDataSource =
                new CopyValueTransformer(
                        "rename-device-type-to-data-source",
                        DEVICE_TYPE_FIELD_NAME,
                        true,
                        Collections.singletonList(DATA_SOURCE_FIELD_NAME));
        transformerChainList.add(renameDeviceTypeToDataSource);

        // copy user_id to userName
        CopyValueTransformer copyUserId =
                new CopyValueTransformer(
                        "copy-user-id",
                        USER_ID_FIELD_NAME,
                        false,
                        Arrays.asList(USER_DISPLAY_NAME_FIELD_NAME, USERNAME_FIELD_NAME));
        transformerChainList.add(copyUserId);

        CopyValueTransformer copySrcMachineName=
                new CopyValueTransformer("copy-src-machine-name",
                        SRC_MACHINE_ID_FIELD_NAME,
                        false,
                        Arrays.asList(SRC_MACHINE_NAME_FIELD_NAME));
        transformerChainList.add(copySrcMachineName);

        //The SecureId Transformer that chain all the transformers together.
        return new JsonObjectChainTransformer("auth-secureid-transformer", transformerChainList);

    }

    private IJsonObjectTransformer buildAuthTransformer() {
        IJsonObjectTransformer authenticationLinuxAuthTransformer = buildAuthenticationLinuxAuthTransformer();
        IJsonObjectTransformer authenticationSecureIdTransformer = buildAuthenticationSecureIdTransformer();
        IJsonObjectTransformer authenticationWindowsAuditTransformer = buildAuthenticationWindowsAuditTransformer();
        JsonObjectRegexPredicate deviceTypeEqualRsaacesrv =
                new JsonObjectRegexPredicate("device-type-equal-rsaacesrv", DEVICE_TYPE_FIELD_NAME, RSAACESRV_DEVICE_TYPE);
        JsonObjectRegexPredicate deviceTypeEqualRhlinux =
                new JsonObjectRegexPredicate("device-type-equal-rhlinux", DEVICE_TYPE_FIELD_NAME, RHLINUX_DEVICE_TYPE);

        IfElseTransformer rhlinuxIfElseTransformer =
                new IfElseTransformer("device-type-rhlinux", deviceTypeEqualRhlinux, authenticationLinuxAuthTransformer, authenticationWindowsAuditTransformer);
        return new IfElseTransformer("device-type-secureid", deviceTypeEqualRsaacesrv, authenticationSecureIdTransformer, rhlinuxIfElseTransformer);
    }

    private IJsonObjectTransformer buildAuthenticationWindowsAuditTransformer(){
        List<IJsonObjectTransformer> transformerChainList = new ArrayList<>();

        // Filtering events according to the device type and user name
        JsonObjectRegexPredicate userDstNotContainMachine = new JsonObjectRegexPredicate("user-dst-not-contain-machine", USER_DST_FIELD_NAME, "[^\\$]*");
        JsonObjectRegexPredicate deviceTypeSnareOrNic = new JsonObjectRegexPredicate("device-type-snare-or-nic", DEVICE_TYPE_FIELD_NAME, "winevent_snare|winevent_nic");
        JsonObjectChainPredicate deviceTypeAndUserDstPredicate = new JsonObjectChainPredicate("device-type-and-user-dst-predicate", AND,
                Arrays.asList(userDstNotContainMachine, deviceTypeSnareOrNic));
        FilterTransformer deviceTypeAndUserDstFilter = new FilterTransformer("device-type-and-user-dst-filter", deviceTypeAndUserDstPredicate, true);
        transformerChainList.add(deviceTypeAndUserDstFilter);

        // for 4769: Filtering in only events with service name which end with $. meaning that it is machine and not a service.
        JsonObjectRegexPredicate serviceNameEndsWithDollar = new JsonObjectRegexPredicate("service-name-ends-with-dollar", SERVICE_NAME_FIELD_NAME, ".*\\$");
        FilterTransformer serviceNameFilter = new FilterTransformer("service-name-filter", serviceNameEndsWithDollar, true);
        JsonObjectRegexPredicate referenceIdEqual4769 = new JsonObjectRegexPredicate("reference-id-equal-4769", EVENT_CODE_FIELD_NAME, "4769");
        IfElseTransformer serviceNameFilterFor4769 =
                new IfElseTransformer("service-name-filter-for-4769", referenceIdEqual4769, serviceNameFilter);
        transformerChainList.add(serviceNameFilterFor4769);

        //for 4624 and 4625: Filtering in events with logon type equals to 2 or 10 ###
        JsonObjectRegexPredicate logonTypeEqual2Or10 = new JsonObjectRegexPredicate("logon-type-equal-0-or-10", LOGON_TYPE_FIELD_NAME, "2|10");
        FilterTransformer logonTypeFilter = new FilterTransformer("logon-type-filter", logonTypeEqual2Or10, true);
        JsonObjectRegexPredicate referenceIdEqual4624Or4625 = new JsonObjectRegexPredicate("reference-id-equal-4624-or-4625", EVENT_CODE_FIELD_NAME, "4624|4625");
        IfElseTransformer logonTypeFilterFor4624Or4625 =
                new IfElseTransformer("logon-type-filter-for-4624-or-4625", referenceIdEqual4624Or4625, logonTypeFilter);
        transformerChainList.add(logonTypeFilterFor4624Or4625);

        //Convert time field from EPOCH millis to EPOCH seconds
        EpochTimeToNanoRepresentationTransformer dateTimeMillisToSeconds =
                new EpochTimeToNanoRepresentationTransformer("date-time-millis-to-nano-representation", EVENT_TIME_FIELD_NAME, DATE_TIME_FIELD_NAME);
        transformerChainList.add(dateTimeMillisToSeconds);

        //Filling the srcMachineName. The value is taken from different fields depends on the reference_id value
        List<SwitchCaseTransformer.SwitchCase> srcMachineNameCases = new ArrayList<>();
        srcMachineNameCases.add(new SwitchCaseTransformer.SwitchCase("4624", String.format("${%s[0]}", ALIAS_HOST_FIELD_NAME)));
        srcMachineNameCases.add(new SwitchCaseTransformer.SwitchCase("4625", String.format("${%s[0]}", ALIAS_HOST_FIELD_NAME)));
        srcMachineNameCases.add(new SwitchCaseTransformer.SwitchCase("4648", String.format("${%s[0]}", ALIAS_HOST_FIELD_NAME)));
        srcMachineNameCases.add(new SwitchCaseTransformer.SwitchCase("4776", String.format("${%s}", HOST_SRC_FIELD_NAME)));
        SwitchCaseTransformer srcMachineNameSwitchCaseTransformer =
                new SwitchCaseTransformer("src-machine-name-switch-case",
                        EVENT_CODE_FIELD_NAME,
                        SRC_MACHINE_NAME_FIELD_NAME,
                        null,
                        srcMachineNameCases);
        transformerChainList.add(srcMachineNameSwitchCaseTransformer);

        //Filling the dstMachineName. The value is taken from different fields depends on the reference_id value
        List<SwitchCaseTransformer.SwitchCase> dstMachineNameCases = new ArrayList<>();
        dstMachineNameCases.add(new SwitchCaseTransformer.SwitchCase("4648", String.format("${%s}", HOST_DST_FIELD_NAME)));

        SwitchCaseTransformer dstMachineNameSwitchCaseTransformer =
                new SwitchCaseTransformer("dst-machine-name-switch-case",
                        EVENT_CODE_FIELD_NAME,
                        DST_MACHINE_NAME_FIELD_NAME,
                        null,
                        dstMachineNameCases);
        transformerChainList.add(dstMachineNameSwitchCaseTransformer);

        //Create capture and format list for source machines and destination machines
        CaptureAndFormatConfiguration machineIdNormalizationZeroPattern =
                new CaptureAndFormatConfiguration("-", "", null);
        CaptureAndFormatConfiguration machineIdNormalizationFirstPattern =
                new CaptureAndFormatConfiguration(".*:.*", "", null);
        CaptureAndFormatConfiguration machineIdNormalizationSecondPattern =
                new CaptureAndFormatConfiguration(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*", "", null);
        CaptureAndFormatConfiguration machineIdNormalizationThirdPattern =
                new CaptureAndFormatConfiguration("(\\\\\\\\)?([^\\.]+)\\..+", "%s",
                        Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration machineIdNormalizationFourthPattern =
                new CaptureAndFormatConfiguration("(\\\\\\\\)?(.+)", "%s",
                        Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));

        List<CaptureAndFormatConfiguration> srcAndDstMachineCaptureAndFormatConfigurationList = Arrays.asList(
                machineIdNormalizationZeroPattern,
                machineIdNormalizationFirstPattern,
                machineIdNormalizationSecondPattern,
                machineIdNormalizationThirdPattern,
                machineIdNormalizationFourthPattern);

        // Normalize the srcMachineId values
        RegexCaptorAndFormatter srcMachineIdNormalization =
                new RegexCaptorAndFormatter("src-machine-id-normalization",
                        SRC_MACHINE_NAME_FIELD_NAME,
                        SRC_MACHINE_ID_FIELD_NAME,
                        srcAndDstMachineCaptureAndFormatConfigurationList);
        transformerChainList.add(srcMachineIdNormalization);

        // Normalize the dstMachineId values
        RegexCaptorAndFormatter dstMachineIdNormalization =
                new RegexCaptorAndFormatter("dst-machine-id-normalization",
                        DST_MACHINE_NAME_FIELD_NAME,
                        DST_MACHINE_ID_FIELD_NAME,
                        srcAndDstMachineCaptureAndFormatConfigurationList);
        transformerChainList.add(dstMachineIdNormalization);

        // Normalize the userId values
        CaptureAndFormatConfiguration userNormalizationFirstPattern = new CaptureAndFormatConfiguration("CN=([^,]+)", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationSecondPattern = new CaptureAndFormatConfiguration("CN=([^,]+),.+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationThirdPattern = new CaptureAndFormatConfiguration("(.+\\\\)+(.+)@.+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationFourthPattern = new CaptureAndFormatConfiguration("(.+\\\\)+([^@]+)", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(2, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationFifthPattern = new CaptureAndFormatConfiguration("(.+)@.+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(1, "LOWER")));
        CaptureAndFormatConfiguration userNormalizationSixthPattern = new CaptureAndFormatConfiguration(".+", "%s",
                Collections.singletonList(new CapturingGroupConfiguration(0, "LOWER")));
        RegexCaptorAndFormatter userIdNormalization = new RegexCaptorAndFormatter("user-id-normalization", USER_DST_FIELD_NAME, USER_ID_FIELD_NAME,
                Arrays.asList(userNormalizationFirstPattern, userNormalizationSecondPattern, userNormalizationThirdPattern,
                        userNormalizationFourthPattern, userNormalizationFifthPattern, userNormalizationSixthPattern));
        transformerChainList.add(userIdNormalization);

        //Supporting ANONYMOUS LOGON and SYSTEM users
        JsonObjectRegexPredicate srcMachineNotBlank = new JsonObjectRegexPredicate("src-machine-not-blank", SRC_MACHINE_ID_FIELD_NAME, "^(?!\\s*$).+");
        FilterTransformer filterEventForAnonymousOrSystemUsersWithNoMachine =
                new FilterTransformer("anonymous-or-system-filter", srcMachineNotBlank, true);
        JoinTransformer createUserIdByJoiningMachineIdAndUserIdTransformer =
                new JoinTransformer("create-user-id-by-joining-machine-id-and-user-id", USER_ID_FIELD_NAME,
                        Arrays.asList(wrapWithDollar(USER_ID_FIELD_NAME), wrapWithDollar(SRC_MACHINE_ID_FIELD_NAME)),
                        "@");
        JsonObjectChainTransformer anonymousOrSystemUsersChainTransformer =
                new JsonObjectChainTransformer("anonymous-or-system-chain",
                        Arrays.asList(filterEventForAnonymousOrSystemUsersWithNoMachine, createUserIdByJoiningMachineIdAndUserIdTransformer));
        JsonObjectRegexPredicate anonymousOrSystemUsersPredicate =
                new JsonObjectRegexPredicate("user-equals-anonymous-or-system-predicate", USER_ID_FIELD_NAME, "anonymous logon|system");
        IfElseTransformer anonymousOrSystemUsersIfElseTransformer =
                new IfElseTransformer("user-equals-anonymous-or-system-if-else",
                        anonymousOrSystemUsersPredicate,
                        anonymousOrSystemUsersChainTransformer);
        transformerChainList.add(anonymousOrSystemUsersIfElseTransformer);

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
                new SwitchCaseTransformer("result-normalization-on-result-code", RESULT_CODE_FIELD_NAME,
                        RESULT_FIELD_NAME, null, resultNormalizationOnResultCodeCases);
        JsonObjectKeyExistPredicate eventTypeKeyExist = new JsonObjectKeyExistPredicate("event-type-exist", EVENT_TYPE_FIELD_NAME);
        IfElseTransformer resultNormalizationTransformer =
                new IfElseTransformer(
                        "result-normalization",
                        eventTypeKeyExist,
                        resultNormalizationOnEventType,
                        resultNormalizationOnResultCodeSwitchCaseTransformer);
        transformerChainList.add(resultNormalizationTransformer);

        //Fill operationType
        List<SwitchCaseTransformer.SwitchCase> logonTypeCases = new ArrayList<>();
        logonTypeCases.add(new SwitchCaseTransformer.SwitchCase("2", INTERACTIVE_LOGON_TYPE));
        logonTypeCases.add(new SwitchCaseTransformer.SwitchCase("10", REMOTE_INTERACTIVE_LOGON_TYPE));
        SwitchCaseTransformer logonTypeSwitchCaseTransformer =
                new SwitchCaseTransformer("logon-type-to-operation-type-switch-case", LOGON_TYPE_FIELD_NAME,
                        OPERATION_TYPE_FIELD_NAME, null, logonTypeCases);
        SetterTransformer operationTypeFor4776Or4769 =
                new SetterTransformer(
                        "4776-or-4769-to-operation-type",
                        OPERATION_TYPE_FIELD_NAME,
                        CREDENTIAL_VALIDATION_OPERATION_TYPE);

        SetterTransformer operationTypeFor4648 =
                new SetterTransformer(
                        "explicit-credentials-logon-operation-type",
                        OPERATION_TYPE_FIELD_NAME,
                        EXPLICIT_CREDENTIALS_LOGON);

        JsonObjectRegexPredicate referenceIdEqual4648= new JsonObjectRegexPredicate("reference-id-equal-4648", EVENT_CODE_FIELD_NAME, "4648");
        IfElseTransformer operationTypeTransformerInternal =
                new IfElseTransformer("operation-type-transformer-internal",
                        referenceIdEqual4624Or4625,
                        logonTypeSwitchCaseTransformer,
                        operationTypeFor4776Or4769);

        IfElseTransformer operationTypeTransformer =
                new IfElseTransformer("operation-type-transformer", referenceIdEqual4648,
                        operationTypeFor4648, operationTypeTransformerInternal);


        transformerChainList.add(operationTypeTransformer);

        //rename event_source_id to eventId
        CopyValueTransformer renameEventSourceIdToEventId =
                new CopyValueTransformer(
                        "rename-event-source-id-to-event-id",
                        EVENT_SOURCE_ID_FIELD_NAME,
                        true,
                        Collections.singletonList(EVENT_ID_FIELD_NAME));
        transformerChainList.add(renameEventSourceIdToEventId);

        //rename reference_id to dataSource
        CopyValueTransformer renameReferenceIdToDataSource =
                new CopyValueTransformer(
                        "rename-reference-id-to-data-source",
                        EVENT_CODE_FIELD_NAME,
                        true,
                        Collections.singletonList(DATA_SOURCE_FIELD_NAME));
        transformerChainList.add(renameReferenceIdToDataSource);

        // copy user_dst to userName
        CopyValueTransformer copyUserDst =
                new CopyValueTransformer(
                        "copy-user-dst",
                        USER_DST_FIELD_NAME,
                        true,
                        Collections.singletonList(USERNAME_FIELD_NAME));
        transformerChainList.add(copyUserDst);

        // copy userId to userDisplayName
        CopyValueTransformer copyUserId =
                new CopyValueTransformer(
                        "copy-user-id",
                        USER_ID_FIELD_NAME,
                        false,
                        Collections.singletonList(USER_DISPLAY_NAME_FIELD_NAME));
        transformerChainList.add(copyUserId);

        // For remote interactive authentications, convert the source machine to destination machine.
        IfElseTransformer convertSrcMachineToDstMachineIfRemoteInteractive = new IfElseTransformer(
                "convert-src-machine-to-dst-machine-if-remote-interactive",
                new JsonObjectRegexPredicate(
                        "convert-src-machine-to-dst-machine-if-remote-interactive-predicate",
                        OPERATION_TYPE_FIELD_NAME,
                        REMOTE_INTERACTIVE_LOGON_TYPE
                ),
                new JsonObjectChainTransformer(
                        "convert-src-machine-to-dst-machine-if-remote-interactive-transformer",
                        Arrays.asList(
                                new CopyValueTransformer(
                                        "move-src-machine-id-to-dst-machine-id-if-remote-interactive",
                                        SRC_MACHINE_ID_FIELD_NAME,
                                        true,
                                        Collections.singletonList(DST_MACHINE_ID_FIELD_NAME)
                                ),
                                new CopyValueTransformer(
                                        "move-src-machine-name-to-dst-machine-name-if-remote-interactive",
                                        SRC_MACHINE_NAME_FIELD_NAME,
                                        true,
                                        Collections.singletonList(DST_MACHINE_NAME_FIELD_NAME)
                                )
                        )
                )
        );
        transformerChainList.add(convertSrcMachineToDstMachineIfRemoteInteractive);

        //The Auth Windows Audit Transformer that chain all the transformers together.
        return new JsonObjectChainTransformer("auth-windows-audit-transformer", transformerChainList);
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
            String hostSource,
            String aliasSource,
            String hostDestination) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVENT_CODE_FIELD_NAME, eventCode);
        jsonObject.put(USER_DST_FIELD_NAME, userDst);
        jsonObject.put(DEVICE_TYPE_FIELD_NAME, deviceType);
        jsonObject.put(SERVICE_NAME_FIELD_NAME, serviceName);
        jsonObject.put(LOGON_TYPE_FIELD_NAME, logonType);
        jsonObject.put(ALIAS_HOST_FIELD_NAME, new JSONArray(aliasHost));
        jsonObject.put(HOST_SRC_FIELD_NAME, hostSource);
        jsonObject.put(HOST_DST_FIELD_NAME, hostDestination);
        jsonObject.put(EVENT_TIME_FIELD_NAME, eventTime);
        jsonObject.put(EVENT_TYPE_FIELD_NAME, eventType);
        jsonObject.put(RESULT_CODE_FIELD_NAME, resultCode);
        jsonObject.put(EVENT_SOURCE_ID_FIELD_NAME, eventId);
        jsonObject.put(ALIAS_SRC_FIELD_NAME, aliasSource);
        return jsonObject;
    }

    private JSONObject buildAuthLinuxLogonJsonObject(
            String sessionId,
            String userSrc,
            Long eventTime,
            String hostSrc,
            String eventType,
            String action,
            String result
    ){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SESSION_ID_FIELD_NAME, sessionId);
        jsonObject.put(USER_SRC_FIELD_NAME, userSrc);
        jsonObject.put(DEVICE_TYPE_FIELD_NAME, RHLINUX_DEVICE_TYPE);
        jsonObject.put(HOST_SRC_FIELD_NAME, hostSrc);
        jsonObject.put(RESULT_FIELD_NAME, result);
        jsonObject.put(EVENT_TIME_FIELD_NAME, eventTime);
        jsonObject.put(EVENT_TYPE_FIELD_NAME, eventType);
        jsonObject.put(ACTION_FIELD_NAME, action);

        return jsonObject;
    }

    private JSONObject buildAuthSecureIdJsonObject(
            String sessionId,
            String userDst,
            Long eventTime,
            String aliasHost,
            String ecActivity,
            String ecOutcome,
            String hostSource
    ){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SESSION_ID_FIELD_NAME, sessionId);
        jsonObject.put(USER_DST_FIELD_NAME, userDst);
        jsonObject.put(DEVICE_TYPE_FIELD_NAME, RSAACESRV_DEVICE_TYPE);
        jsonObject.put(ALIAS_HOST_FIELD_NAME, new JSONArray(aliasHost));
        jsonObject.put(HOST_SRC_FIELD_NAME, hostSource);
        jsonObject.put(EVENT_TIME_FIELD_NAME, eventTime);
        jsonObject.put(EC_ACTIVITY_FIELD_NAME, ecActivity);
        jsonObject.put(EC_OUTCOME_FIELD_NAME, ecOutcome);

        return jsonObject;
    }

    @Test
    public void deserialize_auth_transformer_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String transformerJsonAsString = mapper.writeValueAsString(transformer);

        Assert.assertNotNull(transformerJsonAsString);
    }

    @Test
    public void event_secure_id_succesful_logon_test() throws JsonProcessingException{
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String sessionId = "1835299306";
        String userDst = "gandalf";
        String aliasHost = "[\"gandalf-srv\"]";
        long eventTime = 1528124556000L;
        String ecActivity = "Logon";
        String ecOutcome = "Success";
        String hostSource = "gandalf-pc";
        String operationType = "MFA";

        JSONObject jsonObject = buildAuthSecureIdJsonObject(sessionId, userDst,
                eventTime*1000, aliasHost, ecActivity, ecOutcome, hostSource);

        JSONObject retJsonObject = transform(transformer, jsonObject);
        Assert.assertEquals("wrong event id", sessionId, retJsonObject.get(EVENT_ID_FIELD_NAME));
        Assert.assertEquals("wrong dateTime", new Double(eventTime), retJsonObject.get(DATE_TIME_FIELD_NAME));
        Assert.assertEquals("username normalization did not work", userDst, retJsonObject.get(USER_ID_FIELD_NAME));
        Assert.assertEquals("wrong username", userDst, retJsonObject.get(USERNAME_FIELD_NAME));
        Assert.assertEquals("wrong userDisplayName", userDst, retJsonObject.get(USER_DISPLAY_NAME_FIELD_NAME));
        Assert.assertEquals("source machine id is not as expected", hostSource, retJsonObject.opt(SRC_MACHINE_ID_FIELD_NAME));
        Assert.assertEquals("source machine name is not as expected", hostSource, retJsonObject.opt(SRC_MACHINE_NAME_FIELD_NAME));
        Assert.assertEquals("result normalization did not work", RESULT_SUCCESS, retJsonObject.get(RESULT_FIELD_NAME));

        Assert.assertEquals("operation type logic according the accesses field did not work", operationType, retJsonObject.get(OPERATION_TYPE_FIELD_NAME));
        Assert.assertEquals("wrong data source", RSAACESRV_DEVICE_TYPE, retJsonObject.get(DATA_SOURCE_FIELD_NAME));

        System.out.println(retJsonObject.toString());
    }

    @Test
    public void event_logon_succesful_logon_test() throws JsonProcessingException{
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String sessionId = "1835299306";
        String usersrc = "gandalf";
        String hostsrc = "gandalf-srv";
        long eventTime = 1528124556000L;
        String eventType = "USER_AUTH";
        String result = "success";
        String action = "/usr/sbin/sshd";
        String operationType = eventType;

        JSONObject jsonObject = buildAuthLinuxLogonJsonObject(sessionId, usersrc,
                eventTime*1000, hostsrc, eventType, action, result);

        JSONObject retJsonObject = transform(transformer, jsonObject);
        Assert.assertEquals("wrong event id", sessionId, retJsonObject.get(EVENT_ID_FIELD_NAME));
        Assert.assertEquals("wrong dateTime", new Double(eventTime), retJsonObject.get(DATE_TIME_FIELD_NAME));
        Assert.assertEquals("username normalization did not work", usersrc, retJsonObject.get(USER_ID_FIELD_NAME));
        Assert.assertEquals("wrong username", usersrc, retJsonObject.get(USERNAME_FIELD_NAME));
        Assert.assertEquals("wrong userDisplayName", usersrc, retJsonObject.get(USER_DISPLAY_NAME_FIELD_NAME));
        Assert.assertEquals("source machine id is not as expected", hostsrc, retJsonObject.opt(SRC_MACHINE_ID_FIELD_NAME));
        Assert.assertEquals("source machine name is not as expected", hostsrc, retJsonObject.opt(SRC_MACHINE_NAME_FIELD_NAME));
        Assert.assertEquals("result normalization did not work", RESULT_SUCCESS, retJsonObject.get(RESULT_FIELD_NAME));
        Assert.assertEquals("operation type logic according the accesses field did not work", operationType, retJsonObject.get(OPERATION_TYPE_FIELD_NAME));
        Assert.assertEquals("wrong data source", action, retJsonObject.get(DATA_SOURCE_FIELD_NAME));

        System.out.println(retJsonObject.toString());
    }

    @Test
    public void event_logon_unknown_user_logon_test() throws JsonProcessingException{
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String sessionId = "1835299306";
        String usersrc = "";
        String hostsrc = "gandalf-srv";
        long eventTime = 1528124556000L;
        String eventType = "USER_AUTH";
        String result = "success";
        String action = "/usr/sbin/sshd";

        JSONObject jsonObject = buildAuthLinuxLogonJsonObject(sessionId, usersrc,
                eventTime*1000, hostsrc, eventType, action, result);
        Assert.assertNull(transform(transformer, jsonObject,true));

        JSONObject jsonObject2 = buildAuthLinuxLogonJsonObject(sessionId, "(unknown)",
                eventTime*1000, hostsrc, eventType, action, result);
        Assert.assertNull(transform(transformer, jsonObject2,true));
    }

    @Test
    public void event_logon_filtered_event_type_logon_test() throws JsonProcessingException{
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String sessionId = "1835299306";
        String usersrc = "gandalf";
        String hostsrc = "gandalf-srv";
        long eventTime = 1528124556000L;
        String eventType = "USER_START";
        String result = "success";
        String action = "/usr/sbin/sshd";

        JSONObject jsonObject = buildAuthLinuxLogonJsonObject(sessionId, usersrc,
                eventTime*1000, hostsrc, eventType, action, result);
        Assert.assertNull(transform(transformer, jsonObject,true));

    }

    @Test
    public void filter_service_name_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthTransformer();

        JSONObject jsonObject = buildAuthWindowAuditJsonObject("4769", "testUser", "winevent_snare",
                "krbtgt", 1528124556000L, "2", "[\"someone-pc\"]", "Success Audit"
                , "  ", "10.25.67.33:50005:91168521", null, null, null);

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered since the service name is krbtgt", retJsonObject);
    }

    @Test
    public void filter_logon_type_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthTransformer();

        JSONObject jsonObject = buildAuthWindowAuditJsonObject("4624", "rsmith@montereytechgroup.com", "winevent_snare",
                null, 1528124556000L, "7", "[\"DESKTOP-LLHJ389\"]", "Success Audit"
                , "  ", "10.25.67.33:50005:91168521", null, null, null);

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered. events with logon type which is not 10 or 2 should be filtered out",
                retJsonObject);
    }

    @Test
    public void event_code_4624_option1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String referenceId = "4624";
        String userDst = "rsmith@montereytechgroup.com";
        String aliasHost = "DESKTOP-LLHJ389";
        String aliasSource = "DESKTOP-ALIAS-SOURCE";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Success Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                null, eventTime * 1000, "2",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, null, aliasSource, null);

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertOnExpectedValues(retJsonObject, eventId, eventTime, "rsmith", userDst, "rsmith",
                aliasHost.toLowerCase(), aliasHost, RESULT_SUCCESS, INTERACTIVE_LOGON_TYPE, referenceId, null, null);
    }

    @Test
    public void event_code_4624_with_anonymous_logon_user_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String referenceId = "4624";
        String userDst = "ANONYMOUS LOGON";
        String aliasHost = "DESKTOP-LLHJ389";
        String aliasSource = "DESKTOP-ALIAS-SOURCE";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Success Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                null, eventTime * 1000, "2",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, null, aliasSource, null);

        JSONObject retJsonObject = transform(transformer, jsonObject);
        String userId = StringUtils.join(Arrays.asList(userDst.toLowerCase(), aliasHost.toLowerCase()), "@");
        assertOnExpectedValues(retJsonObject, eventId, eventTime, userId, userDst, userId,
                aliasHost.toLowerCase(), aliasHost, RESULT_SUCCESS, INTERACTIVE_LOGON_TYPE, referenceId, null, null);
    }

    @Test
    public void event_code_4624_with_anonymous_logon_user_and_blank_machine_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String referenceId = "4624";
        String userDst = "ANONYMOUS LOGON";
        String aliasHost = "a:b";
        String aliasSource = "DESKTOP-LLHJ389";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Success Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                null, eventTime * 1000, "2",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, null, aliasSource, null);

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered. events with anonymous logon user and with empty machine should be filtered out.",
                retJsonObject);
    }

    @Test
    public void event_code_4776_option1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String referenceId = "4776";
        String userDst = "CN=BOBBY,OU=Users,DC=Dell";
        String aliasHost = "DESKTOP-LLHJ389";
        String hostSource = "a:b";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Failure Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_nic",
                null, eventTime * 1000, "10",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, hostSource, null, null);

        JSONObject retJsonObject = transform(transformer, jsonObject);

        String userId = "bobby";
        assertOnExpectedValues(retJsonObject, eventId, eventTime, userId, userDst, userId,
                "", hostSource, RESULT_FAILURE, CREDENTIAL_VALIDATION_OPERATION_TYPE, referenceId, null, null);
    }

    @Test
    public void event_code_4648_option_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthenticationWindowsAuditTransformer();

        String referenceId = "4648";
        String userDst = "CN=BOBBY,OU=Users,DC=Dell";
        String aliasHost = "DESKTOP-LLHJ389";
        String hostSource = "a:b";
        String dstMachine = "AD_SERVER123";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Failure Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_nic",
                null, eventTime * 1000, "10",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, hostSource, null, dstMachine);

        JSONObject retJsonObject = transform(transformer, jsonObject);

        String userId = "bobby";
        String expectedDstMachine = dstMachine.toLowerCase();
        assertOnExpectedValues(retJsonObject, eventId, eventTime, userId, userDst, userId,

                aliasHost.toLowerCase(), aliasHost, RESULT_FAILURE,EXPLICIT_CREDENTIALS_LOGON ,
                referenceId,expectedDstMachine,dstMachine);

    }

    @Test
    public void event_code_4776_option2_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String referenceId = "4776";
        String userDst = "CN=BOBBY,OU=Users,DC=Dell";
        String aliasHost = "DESKTOP-LLHJ389";
        String hostSource = "a:b";
        String aliasSrc = "a:alias_source";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Failure Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                null, eventTime * 1000, "10",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, hostSource, aliasSrc, null);

        JSONObject retJsonObject = transform(transformer, jsonObject);

        String userId = "bobby";
        assertOnExpectedValues(retJsonObject, eventId, eventTime, userId, userDst, userId,
                "", hostSource, RESULT_FAILURE, CREDENTIAL_VALIDATION_OPERATION_TYPE, referenceId, null, null);
    }

    @Test
    public void event_code_4769_option1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthTransformer();

        String referenceId = "4769";
        String userDst = "CN=BOBBY,OU=Users,DC=Dell";
        String aliasHost = "DESKTOP-LLHJ389";
        String hostSource = "a:b";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Failure Audit";
        JSONObject jsonObject = buildAuthWindowAuditJsonObject(referenceId, userDst, "winevent_nic",
                "someMachine$", eventTime * 1000, "10",
                String.format("[\"%s\",\"another alias\"]", aliasHost),
                eventType, "  ", eventId, hostSource, null, null);

        JSONObject retJsonObject = transform(transformer, jsonObject);

        String userId = "bobby";
        assertOnExpectedValues(retJsonObject, eventId, eventTime, userId, userDst, userId,
                null, JSONObject.NULL, RESULT_FAILURE, CREDENTIAL_VALIDATION_OPERATION_TYPE, referenceId, null, null);
    }

    @Test
    public void convert_src_machine_to_dst_machine_if_remote_interactive() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildAuthenticationWindowsAuditTransformer();

        // Logon type = 2.
        String aliasHost = "DESKTOP-ABC123";
        JSONObject original = buildAuthWindowAuditJsonObject(
                "4624", "CN=BOBBY,OU=Users,DC=Dell", "winevent_nic", "someMachine$", 1528124556000L * 1000, "2",
                String.format("[\"%s\",\"MY-ALIAS-HOST\"]", aliasHost),
                "Success Audit", "0x0", "10.25.67.33:50005:91168521", "a:b", "MY-ALIAS-SOURCE", null);

        JSONObject transformed = transform(transformer, original);
        Assert.assertEquals("desktop-abc123", transformed.getString(SRC_MACHINE_ID_FIELD_NAME));
        Assert.assertEquals("DESKTOP-ABC123", transformed.getString(SRC_MACHINE_NAME_FIELD_NAME));
        Assert.assertTrue(!transformed.has(DST_MACHINE_ID_FIELD_NAME) || transformed.isNull(DST_MACHINE_ID_FIELD_NAME));
        Assert.assertTrue(!transformed.has(DST_MACHINE_NAME_FIELD_NAME) || transformed.isNull(DST_MACHINE_NAME_FIELD_NAME));

        // Logon type = 10.
        aliasHost = "LAPTOP-XYZ42";
        original = buildAuthWindowAuditJsonObject(
                "4624", "CN=BOBBY,OU=Users,DC=Dell", "winevent_nic", "someMachine$", 1528124556000L * 1000, "10",
                String.format("[\"%s\",\"MY-ALIAS-HOST\"]", aliasHost),
                "Success Audit", "0x0", "10.25.67.33:50005:91168521", "a:b", "MY-ALIAS-SOURCE", null);

        transformed = transform(transformer, original);
        Assert.assertTrue(!transformed.has(SRC_MACHINE_ID_FIELD_NAME) || transformed.isNull(SRC_MACHINE_ID_FIELD_NAME));
        Assert.assertTrue(!transformed.has(SRC_MACHINE_NAME_FIELD_NAME) || transformed.isNull(SRC_MACHINE_NAME_FIELD_NAME));
        Assert.assertEquals("laptop-xyz42", transformed.getString(DST_MACHINE_ID_FIELD_NAME));
        Assert.assertEquals("LAPTOP-XYZ42", transformed.getString(DST_MACHINE_NAME_FIELD_NAME));
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
                                        String expectedDataSource,
                                        String expectedDstMachineId,
                                        String expectedDstMachineName) {

        Assert.assertEquals("wrong event id", expectedEventId, retJsonObject.get(EVENT_ID_FIELD_NAME));
        Assert.assertEquals("wrong dateTime", new Double(expectedDateTime), retJsonObject.get(DATE_TIME_FIELD_NAME));
        Assert.assertEquals("username normalization did not work", expectedUserId, retJsonObject.get(USER_ID_FIELD_NAME));
        Assert.assertEquals("wrong username", expectedUsername, retJsonObject.get(USERNAME_FIELD_NAME));
        Assert.assertEquals("wrong userDisplayName", expectedUserDisplayName, retJsonObject.get(USER_DISPLAY_NAME_FIELD_NAME));
        Assert.assertEquals("source machine id is not as expected", expectedSrcMachineId, retJsonObject.opt(SRC_MACHINE_ID_FIELD_NAME));
        Assert.assertEquals("source machine name is not as expected", expectedSrcMachineName, retJsonObject.optString(SRC_MACHINE_NAME_FIELD_NAME, null));
        Assert.assertEquals("result normalization did not work", expectedResult, retJsonObject.get(RESULT_FIELD_NAME));
        Assert.assertEquals("operation type logic according the accesses field did not work", expectedOperationType, retJsonObject.get(OPERATION_TYPE_FIELD_NAME));
        Assert.assertEquals("wrong data source", expectedDataSource, retJsonObject.get(DATA_SOURCE_FIELD_NAME));
        Assert.assertEquals("destination machine name is not as expected", expectedDstMachineName, retJsonObject.optString(DST_MACHINE_NAME_FIELD_NAME, null));
        Assert.assertEquals("destination machine id is not as expected", expectedDstMachineId, retJsonObject.opt(DST_MACHINE_ID_FIELD_NAME));
    }
}
