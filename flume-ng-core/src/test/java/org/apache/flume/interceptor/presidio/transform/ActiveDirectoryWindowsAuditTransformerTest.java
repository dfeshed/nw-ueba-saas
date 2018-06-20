package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import fortscale.utils.transform.*;
import fortscale.utils.transform.predicate.JsonObjectChainPredicate;
import static fortscale.utils.transform.predicate.JsonObjectChainPredicate.LogicalOperation.AND;
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

public class ActiveDirectoryWindowsAuditTransformerTest extends TransformerTest{

    private static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    private static final String OBJECT_ID_FIELD_NAME = "objectId";
    private static final String OPERATION_TYPE_CATEGORIES_FIELD_NAME = "operationTypeCategory";
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
    private static final String SECONDARY_OBJECTID_FIELD_PATH = "additionalInfo.secondaryObjectId";
    private static final String USER_SOURCE_FIELD_NAME= "user_src";
    private static final String GROUP_FIELD_NAME = "group";
    private static final String OBJ_NAME_FIELD_NAME = "obj_name";
    private static final String ACCESSES_FIELD_NAME = "accesses";
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_FAILURE = "FAILURE";

    private IJsonObjectTransformer buildADWindowsAuditTransformer() {
        List<IJsonObjectTransformer> transformerChainList = new ArrayList<>();

        // Filtering events according to the device type and user name
        JsonObjectRegexPredicate userDstNotContainMachine = new JsonObjectRegexPredicate("user-dst-not-contain-machine", USER_DST_FIELD_NAME, "[^\\$]*");
        JsonObjectRegexPredicate deviceTypeSnareOrNic = new JsonObjectRegexPredicate("device-type-snare-or-nic", DEVICE_TYPE_FIELD_NAME, "winevent_snare|winevent_nic");
        JsonObjectChainPredicate deviceTypeAndUserDstPredicate = new JsonObjectChainPredicate("device-type-and-user-dst-predicate",AND,
                Arrays.asList(userDstNotContainMachine, deviceTypeSnareOrNic));
        FilterTransformer deviceTypeAndUserDstFilter = new FilterTransformer("device-type-and-user-dst-filter", deviceTypeAndUserDstPredicate, true);
        transformerChainList.add(deviceTypeAndUserDstFilter);

        //Convert time field from EPOCH millis to EPOCH seconds
        EpochTimeToNanoRepresentationTransformer dateTimeMillisToSeconds =
                new EpochTimeToNanoRepresentationTransformer("date-time-millis-to-nano-representation", EVENT_TIME_FIELD_NAME, DATE_TIME_FIELD_NAME);
        transformerChainList.add(dateTimeMillisToSeconds);

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

        // copy user_dst to userName,userDisplayName
        CopyValueTransformer copyUserDst =
                new CopyValueTransformer(
                        "copy-user-dst",
                        USER_DST_FIELD_NAME,
                        true,
                        Arrays.asList(USERNAME_FIELD_NAME, USER_DISPLAY_NAME_FIELD_NAME));
        transformerChainList.add(copyUserDst);

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

        //Add operationType according to reference_id
        List<SwitchCaseTransformer.SwitchCase> operationTypeAccordingToEventCodeCases = new ArrayList<>();
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4670", "PERMISSIONS_ON_OBJECT_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4717", "SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4720", "USER_ACCOUNT_CREATED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4722", "USER_ACCOUNT_ENABLED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4723", "USER_PASSWORD_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4724", "USER_PASSWORD_RESET"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4725", "USER_ACCOUNT_DISABLED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4726", "USER_ACCOUNT_DELETED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4727", "SECURITY_ENABLED_GLOBAL_GROUP_CREATED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4728", "MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4729", "MEMBER_REMOVED_FROM_SECURITY_ENABLED_GLOBAL_GROUP"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4730", "SECURITY_ENABLED_GLOBAL_GROUP_DELETED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4731", "SECURITY_ENABLED_LOCAL_GROUP_CREATED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4732", "MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4733", "MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4734", "SECURITY_ENABLED_LOCAL_GROUP_DELETED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4735", "SECURITY_ENABLED_LOCAL_GROUP_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4737", "SECURITY_ENABLED_GLOBAL_GROUP_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4738", "USER_ACCOUNT_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4739", "DOMAIN_POLICY_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4740", "USER_ACCOUNT_LOCKED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4741", "COMPUTER_ACCOUNT_CREATED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4742", "COMPUTER_ACCOUNT_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4743", "COMPUTER_ACCOUNT_DELETED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4754", "SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4755", "SECURITY_ENABLED_UNIVERSAL_GROUP_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4756", "MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4757", "MEMBER_REMOVED_FROM_SECURITY_ENABLED_UNIVERSAL_GROUP"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4758", "SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4764", "GROUP_TYPE_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4767", "USER_ACCOUNT_UNLOCKED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4794", "ATTEMPT_MADE_TO_SET_DIRECTORY_SERVICES_RESTORE_MODE_ADMINISTRATOR_PASSWORD"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("5136", "DIRECTORY_SERVICE_OBJECT_MODIFIED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("5376", "CREDENTIAL_MANAGER_CREDENTIALS_BACKED_UP"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("5377", "CREDENTIAL_MANAGER_CREDENTIALS_RESTORED_FROM_BACKUP"));
        SwitchCaseTransformer operationTypeAccordingToEventCodeSwitchCaseTransformer =
                new SwitchCaseTransformer("operation_type-according-to-event-code",EVENT_CODE_FIELD_NAME,
                        OPERATION_TYPE_FIELD_NAME, null,operationTypeAccordingToEventCodeCases);
        transformerChainList.add(operationTypeAccordingToEventCodeSwitchCaseTransformer);

        // Add operationTypeCategories according to operationType
        List<SwitchCaseTransformer.SwitchCase> operationTypeCategoriesAccordingToOperationTypeCases = new ArrayList<>();
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("COMPUTER_ACCOUNT_CREATED", new String[]{"COMPUTER_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("COMPUTER_ACCOUNT_CHANGED", new String[]{"COMPUTER_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP", new String[]{"GROUP_MEMBERSHIP_REMOVE_OPERATION"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SECURITY_ENABLED_LOCAL_GROUP_DELETED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SECURITY_ENABLED_LOCAL_GROUP_CHANGED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SECURITY_ENABLED_UNIVERSAL_GROUP_CHANGED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("ATTEMPT_MADE_TO_SET_DIRECTORY_SERVICES_RESTORE_MODE_ADMINISTRATOR_PASSWORD", new String[]{"OBJECT_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("CREDENTIAL_MANAGER_CREDENTIALS_BACKED_UP", new String[]{"OBJECT_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("CREDENTIAL_MANAGER_CREDENTIALS_RESTORED_FROM_BACKUP", new String[]{"OBJECT_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("DIRECTORY_SERVICE_OBJECT_MODIFIED", new String[]{"OBJECT_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("GROUP_TYPE_CHANGED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("PERMISSIONS_ON_OBJECT_CHANGED", new String[]{"OBJECT_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("COMPUTER_ACCOUNT_DELETED", new String[]{"COMPUTER_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("DOMAIN_POLICY_CHANGED", new String[]{"DOMAIN_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SECURITY_ENABLED_GLOBAL_GROUP_CHANGED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SECURITY_ENABLED_GLOBAL_GROUP_CREATED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP", new String[]{"GROUP_MEMBERSHIP_ADD_OPERATION"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP", new String[]{"GROUP_MEMBERSHIP_ADD_OPERATION"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("MEMBER_REMOVED_FROM_SECURITY_ENABLED_UNIVERSAL_GROUP", new String[]{"GROUP_MEMBERSHIP_REMOVE_OPERATION"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("USER_ACCOUNT_CREATED", new String[]{"USER_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("USER_ACCOUNT_DELETED", new String[]{"USER_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("USER_ACCOUNT_CHANGED", new String[]{"USER_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT", new String[]{"OBJECT_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("MEMBER_REMOVED_FROM_SECURITY_ENABLED_GLOBAL_GROUP", new String[]{"GROUP_MEMBERSHIP_REMOVE_OPERATION"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SECURITY_ENABLED_GLOBAL_GROUP_DELETED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("SECURITY_ENABLED_LOCAL_GROUP_CREATED", new String[]{"GROUP_MANAGEMENT"}));
        operationTypeCategoriesAccordingToOperationTypeCases.add(new SwitchCaseTransformer.SwitchCase("MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP", new String[]{"GROUP_MEMBERSHIP_ADD_OPERATION"}));
        SwitchCaseTransformer operationTypeCategoriesAccordingToOperationTypeSwitchCaseTransformer =
                new SwitchCaseTransformer("operation_type-categories-according-to-operation-type",OPERATION_TYPE_FIELD_NAME,
                        OPERATION_TYPE_CATEGORIES_FIELD_NAME, null,operationTypeCategoriesAccordingToOperationTypeCases);
        transformerChainList.add(operationTypeCategoriesAccordingToOperationTypeSwitchCaseTransformer);

        //Add objectId from a varying NetWitness metadata key according to reference_id
        List<SwitchCaseTransformer.SwitchCase> objectIdAccordingToReferenceIdCases = new ArrayList<>();
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4741", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4742", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4733", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4734", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4735", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4755", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4740", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("5136", String.format("${%s}", OBJ_NAME_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4764", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4670", String.format("${%s}", OBJ_NAME_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4743", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4737", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4727", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4728", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4754", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4756", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4757", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4758", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4720", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4722", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4723", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4724", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4725", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4726", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4738", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4767", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4717", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4729", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4730", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4731", String.format("${%s}", GROUP_FIELD_NAME)));
        objectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4732", String.format("${%s}", USER_SOURCE_FIELD_NAME)));
        SwitchCaseTransformer objectIdAccordingToEventCodeSwitchCaseTransformer =
                new SwitchCaseTransformer("object-id-according-to-reference-id",EVENT_CODE_FIELD_NAME,
                        OBJECT_ID_FIELD_NAME, null,objectIdAccordingToReferenceIdCases);
        transformerChainList.add(objectIdAccordingToEventCodeSwitchCaseTransformer);

        //Add additionalInfo from a varying NetWitness metadata key according to reference_id
        List<SwitchCaseTransformer.SwitchCase> secondObjectIdAccordingToReferenceIdCases = new ArrayList<>();
        secondObjectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4733", String.format("${%s}", GROUP_FIELD_NAME)));
        secondObjectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4728", String.format("${%s}", GROUP_FIELD_NAME)));
        secondObjectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4756", String.format("${%s}", GROUP_FIELD_NAME)));
        secondObjectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4757", String.format("${%s}", GROUP_FIELD_NAME)));
        secondObjectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4717", String.format("${%s}", ACCESSES_FIELD_NAME)));
        secondObjectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4729", String.format("${%s}", GROUP_FIELD_NAME)));
        secondObjectIdAccordingToReferenceIdCases.add(new SwitchCaseTransformer.SwitchCase("4732", String.format("${%s}", GROUP_FIELD_NAME)));
        SwitchCaseTransformer secondObjectIdAccordingToEventCodeSwitchCaseTransformer =
                new SwitchCaseTransformer("secondary-object-id-according-to-reference-id",EVENT_CODE_FIELD_NAME,
                        SECONDARY_OBJECTID_FIELD_PATH, null,secondObjectIdAccordingToReferenceIdCases);
        transformerChainList.add(secondObjectIdAccordingToEventCodeSwitchCaseTransformer);

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


        //The Active Directory Windows Audit Transformer that chain all the transformers together.
        JsonObjectChainTransformer adWindowsAuditTransformer =
                new JsonObjectChainTransformer("active-directory-windows-audit-transformer", transformerChainList);

        return adWindowsAuditTransformer;
    }

    @Test
    public void deserialize_ad_transformer_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildADWindowsAuditTransformer();

        String transformerJsonAsString = mapper.writeValueAsString(transformer);


        Assert.assertNotNull(transformerJsonAsString);
    }

    @Test
    public void event_code_4741_option1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildADWindowsAuditTransformer();

        String referenceId = "4741";
        String userDst = "rsmith@montereytechgroup.com";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Success Audit";
        String userSource = "The computer on whom the operation have been done";
        JSONObject jsonObject = buildADWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                eventTime*1000, eventType, "  ", eventId, userSource, null, "just some obj name", "");

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertOnExpectedValues(retJsonObject, eventId, eventTime, "rsmith", userDst, userDst,
                RESULT_SUCCESS, "COMPUTER_ACCOUNT_CREATED", referenceId,
                "[\"COMPUTER_MANAGEMENT\"]", userSource, null);
    }

    @Test
    public void event_code_4739_option1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildADWindowsAuditTransformer();

        String referenceId = "4739";
        String userDst = "rsmith@montereytechgroup.com";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Success Audit";
        JSONObject jsonObject = buildADWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                eventTime*1000, eventType, "  ", eventId, "just some user source name", null, "just some obj name", "");

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertOnExpectedValues(retJsonObject, eventId, eventTime, "rsmith", userDst, userDst,
                RESULT_SUCCESS, "DOMAIN_POLICY_CHANGED", referenceId,
                "[\"DOMAIN_MANAGEMENT\"]", null, null);
    }

    @Test
    public void event_code_4733_option1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildADWindowsAuditTransformer();

        String referenceId = "4733";
        String userDst = "CORP\\BOBBY@DELL.COM";
        String eventId = "10.25.67.33:50005:91168521";
        Long eventTime = 1528124556000L;
        String eventType = "Success Audit";
        String userSource = "The user on whom the operation have been done";
        String group = "The group on whom the operation have been done";
        JSONObject jsonObject = buildADWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                eventTime*1000, eventType, "  ", eventId, userSource, group,
                "just some obj name", "just some accesses value");

        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertOnExpectedValues(retJsonObject, eventId, eventTime, "bobby", userDst, userDst,
                RESULT_SUCCESS, "MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP", referenceId,
                "[\"GROUP_MEMBERSHIP_REMOVE_OPERATION\"]", userSource, group);
    }





    private JSONObject buildADWindowAuditJsonObject(
            String eventCode,
            String userDst,
            String deviceType,
            Long eventTime,
            String eventType,
            String resultCode,
            String eventId,
            String userSource,
            String group,
            String objName,
            String accesses
    ){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVENT_CODE_FIELD_NAME, eventCode);
        jsonObject.put(USER_DST_FIELD_NAME, userDst);
        jsonObject.put(DEVICE_TYPE_FIELD_NAME, deviceType);
        jsonObject.put(EVENT_TIME_FIELD_NAME, Long.toString(eventTime));
        jsonObject.put(EVENT_TYPE_FIELD_NAME, eventType);
        jsonObject.put(RESULT_CODE_FIELD_NAME, resultCode);
        jsonObject.put(EVENT_SOURCE_ID_FIELD_NAME, eventId);
        jsonObject.put(USER_SOURCE_FIELD_NAME, userSource);
        jsonObject.put(GROUP_FIELD_NAME, group);
        jsonObject.put(OBJ_NAME_FIELD_NAME, objName);
        jsonObject.put(ACCESSES_FIELD_NAME, accesses);

        return jsonObject;
    }

    private void assertOnExpectedValues(JSONObject retJsonObject,
                                        String expectedEventId,
                                        Long expectedDateTime,
                                        String expectedUserId,
                                        String expectedUsername,
                                        String expectedUserDisplayName,
                                        String expectedResult,
                                        String expectedOperationType,
                                        String expectedDataSource,
                                        String expectedOperationTypeCategories,
                                        String expectedObjectId,
                                        String expectedSecondaryObjectId){
        Assert.assertEquals("wrong event id", expectedEventId, retJsonObject.get(EVENT_ID_FIELD_NAME));
        Assert.assertEquals("wrong dateTime", new Double(expectedDateTime), retJsonObject.get(DATE_TIME_FIELD_NAME));
        Assert.assertEquals("username normalization did not work", expectedUserId, retJsonObject.get(USER_ID_FIELD_NAME));
        Assert.assertEquals("wrong username", expectedUsername, retJsonObject.get(USERNAME_FIELD_NAME));
        Assert.assertEquals("wrong userDisplayName", expectedUserDisplayName, retJsonObject.get(USER_DISPLAY_NAME_FIELD_NAME));
        Assert.assertEquals("result normalization did not work", expectedResult, retJsonObject.get(RESULT_FIELD_NAME));
        Assert.assertEquals("operation type logic according the accesses field did not work", expectedOperationType, retJsonObject.get(OPERATION_TYPE_FIELD_NAME));
        Assert.assertEquals("wrong data source", expectedDataSource, retJsonObject.get(DATA_SOURCE_FIELD_NAME));
        assertEqualJsonArrays(retJsonObject, OPERATION_TYPE_CATEGORIES_FIELD_NAME, new JSONArray(expectedOperationTypeCategories));
        Assert.assertEquals("wrong objectId",
                expectedObjectId == null? JSONObject.NULL : expectedObjectId,
                retJsonObject.opt(OBJECT_ID_FIELD_NAME));
        Assert.assertEquals("wrong secondary objectId",
                expectedSecondaryObjectId == null ? JSONObject.NULL : expectedSecondaryObjectId,
                (new JsonPointer(SECONDARY_OBJECTID_FIELD_PATH)).get(retJsonObject));
    }

    private void assertEqualJsonArrays(JSONObject retJsonObject, String key, JSONArray expectedJSONArray){
        Assert.assertTrue(String.format("The %s expected to contain json array. expected: %s actual: %s",key, expectedJSONArray, retJsonObject.get(key)),
                retJsonObject.get(key) instanceof JSONArray);
        JSONArray actualJsonArray = retJsonObject.getJSONArray(key);
        Assert.assertEquals(
                String.format("The expected json array and the actual json array contain different amount of elements. actual: %s expected: %s",
                        actualJsonArray, expectedJSONArray),
                actualJsonArray.length(), expectedJSONArray.length());
        for (int i = 0; i < actualJsonArray.length(); i++){
            boolean found = false;
            for (int j = 0; j < expectedJSONArray.length(); j++){
                if(expectedJSONArray.get(j).equals(actualJsonArray.get(i))){
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(
                    String.format("The expected json array and the actual json array are not equals. actual: %s expected: %s",
                            actualJsonArray, expectedJSONArray),
                    found);
        }
    }
}
