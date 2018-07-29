package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import fortscale.utils.transform.predicate.JsonObjectChainPredicate;
import fortscale.utils.transform.predicate.JsonObjectKeyExistPredicate;
import fortscale.utils.transform.predicate.JsonObjectRegexPredicate;
import fortscale.utils.transform.regexcaptureandformat.CaptureAndFormatConfiguration;
import fortscale.utils.transform.regexcaptureandformat.CapturingGroupConfiguration;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fortscale.utils.transform.predicate.JsonObjectChainPredicate.LogicalOperation.AND;

public class FileWindowsAuditTransformerTest extends TransformerTest{

    private static final String ACCESSES_FIELD_NAME = "accesses";
    private static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    private static final String ACCESSES_CREATE_OPERATION_VALUE = "WriteData (or AddFile)";
    private static final String FILE_CREATED = "FILE_CREATED";
    private static final String ACCESSES_OPEN_OPERATION_VALUE = "ReadData (or ListDirectory)";
    private static final String FILE_OPENED = "FILE_OPENED";
    private static final String ACCESSES_MODIFIED_OPERATION_VALUE = "AppendData (or AddSubdirectory or CreatePipeInstance)";
    private static final String FILE_MODIFIED = "FILE_MODIFIED";
    private static final String ACCESSES_WRITE_DAC_OPERATION_VALUE = "WRITE_DAC";
    private static final String FILE_WRITE_DAC_PERMISSION_CHANGED = "FILE_WRITE_DAC_PERMISSION_CHANGED";
    private static final String ACCESSES_WRITE_OWNER_OPERATION_VALUE = "WRITE_OWNER";
    private static final String FILE_WRITE_OWNER_PERMISSION_CHANGED = "FILE_WRITE_OWNER_PERMISSION_CHANGED";
    private static final List<String> CASES = Arrays.asList(ACCESSES_CREATE_OPERATION_VALUE, ACCESSES_OPEN_OPERATION_VALUE, ACCESSES_MODIFIED_OPERATION_VALUE, ACCESSES_WRITE_DAC_OPERATION_VALUE, ACCESSES_WRITE_OWNER_OPERATION_VALUE);
    private static final List<String> CASES_VALUES = Arrays.asList(FILE_CREATED, FILE_OPENED, FILE_MODIFIED, FILE_WRITE_DAC_PERMISSION_CHANGED, FILE_WRITE_OWNER_PERMISSION_CHANGED);
    private static final String EVENT_CODE_FIELD_NAME = "reference_id";
    private static final String USER_DST_FIELD_NAME = "user_dst";
    private static final String DEVICE_TYPE_FIELD_NAME = "device_type";
    private static final String CATEGORY_FIELD_NAME = "category";
    private static final String USER_ID_FIELD_NAME = "userId";
    private static final String EVENT_TIME_FIELD_NAME = "event_time";
    private static final String OBJ_NAME_FIELD_NAME = "obj_name";
    private static final String OBJ_TYPE_FIELD_NAME = "obj_type";
    private static final String FILENAME_FIELD_NAME = "filename";
    private static final String EVENT_TYPE_FIELD_NAME = "event_type";
    private static final String RESULT_FIELD_NAME = "result";
    private static final String RESULT_CODE_FIELD_NAME = "result_code";
    private static final String EVENT_ID_FIELD_NAME = "eventId";
    private static final String EVENT_SOURCE_ID_FIELD_NAME = "event_source_id";
    private static final String DIRECTORY_FIELD_NAME = "directory";
    private static final String SRC_FILE_PATH_FIELD_NAME = "srcFilePath";
    private static final String IS_SRC_DRIVE_SHARED_FIELD_NAME = "isSrcDriveShared";
    private static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    private static final String DATE_TIME_FIELD_NAME = "dateTime";
    private static final String USERNAME_FIELD_NAME = "userName";
    private static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_FAILURE = "FAILURE";



    private String wrapWithDollar(String fieldName){
        return String.format("${%s}", fieldName);
    }

    private IJsonObjectTransformer buildFileWindowsAuditTransformer(){
        List<IJsonObjectTransformer> transformerChainList = new ArrayList<>();

        // Filtering events according to the device type and user name
        JsonObjectRegexPredicate userDstNotContainMachine = new JsonObjectRegexPredicate("user-dst-not-contain-machine", USER_DST_FIELD_NAME, "[^\\$]*");
        JsonObjectRegexPredicate deviceTypeSnareOrNic = new JsonObjectRegexPredicate("device-type-snare-or-nic", DEVICE_TYPE_FIELD_NAME, "winevent_snare|winevent_nic");
        JsonObjectChainPredicate deviceTypeAndUserDstPredicate = new JsonObjectChainPredicate("device-type-and-user-dst-predicate",AND,
                Arrays.asList(userDstNotContainMachine, deviceTypeSnareOrNic));
        FilterTransformer deviceTypeAndUserDstFilter = new FilterTransformer("device-type-and-user-dst-filter", deviceTypeAndUserDstPredicate, true);
        transformerChainList.add(deviceTypeAndUserDstFilter);

        // for 4670: Filter in events with obj.type equals to 'File'
        JsonObjectRegexPredicate objTypeEqualFile = new JsonObjectRegexPredicate("obj-type-equal-file", OBJ_TYPE_FIELD_NAME, "File");
        FilterTransformer objTypeFilter = new FilterTransformer("obj-type-filter", objTypeEqualFile, true);
        JsonObjectRegexPredicate referenceIdEqual4670 = new JsonObjectRegexPredicate("reference-id-equal-4670", EVENT_CODE_FIELD_NAME, "4670");
        IfElseTransformer objTypeFilterFor4670 =
                new IfElseTransformer("obj-type-filter-for-4670",referenceIdEqual4670, objTypeFilter);
        transformerChainList.add(objTypeFilterFor4670);

        // for 4663 and 4660: Filter in events with category equals to 'File System'
        JsonObjectRegexPredicate categoryEqualFileSystem = new JsonObjectRegexPredicate("category-equal-file-system", CATEGORY_FIELD_NAME, "File System");
        FilterTransformer categoryFilter = new FilterTransformer("category-filter", categoryEqualFileSystem, true);
        JsonObjectRegexPredicate referenceIdEqual4660Or4663 = new JsonObjectRegexPredicate("reference-id-equal-4663-or-4660", EVENT_CODE_FIELD_NAME, "4663|4660");
        IfElseTransformer categoryFilterFor4663And4660 =
                new IfElseTransformer("category-filter-for-4663-and-4660",referenceIdEqual4660Or4663, categoryFilter);
        transformerChainList.add(categoryFilterFor4663And4660);

        //operation type logic:

        // 4663 operation type logic:
        List<IJsonObjectTransformer> operationTypeFor4663ChainList = new ArrayList<>();

        // for 4663: Filtering events according to accesses field (filtering out delete operations)
        JsonObjectRegexPredicate accessesEqualDelete = new JsonObjectRegexPredicate("accesses-equal-delete", ACCESSES_FIELD_NAME, "DELETE|DeleteChild");
        FilterTransformer accessesFilter = new FilterTransformer("accesses-filter", accessesEqualDelete, false);
        operationTypeFor4663ChainList.add(accessesFilter);
        // For 4663: Filling the operation type
        List<SwitchCaseTransformer.SwitchCase> accessesCases = new ArrayList<>();
        for(int i = 0; i < CASES.size(); i++){
            accessesCases.add(new SwitchCaseTransformer.SwitchCase(containedRegex(CASES.get(i)),CASES_VALUES.get(i), true));
        }
        SwitchCaseTransformer operationTypeAccordingToAccessesSwitchCaseTransformer =
                new SwitchCaseTransformer("operation_type-according-to-accesses",ACCESSES_FIELD_NAME,
                        OPERATION_TYPE_FIELD_NAME, null,accessesCases);
        operationTypeFor4663ChainList.add(operationTypeAccordingToAccessesSwitchCaseTransformer);
        // for 4663: Filtering events with no operation type
        JsonObjectKeyExistPredicate operationTypeNotNull = new JsonObjectKeyExistPredicate("operation-type-not-null-4663", OPERATION_TYPE_FIELD_NAME, true);
        FilterTransformer operationTypeNotNullFilter = new FilterTransformer("operation-type-not-null-4663-filter", operationTypeNotNull, true);
        operationTypeFor4663ChainList.add(operationTypeNotNullFilter);
        JsonObjectChainTransformer operationTypeFor4663ChainTransformer = new JsonObjectChainTransformer("operation-type-for-4663-chain", operationTypeFor4663ChainList);

        //For 4660, 4670 and 5145: Filling the operation type
        List<SwitchCaseTransformer.SwitchCase> operationTypeAccordingToEventCodeCases = new ArrayList<>();
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4660", "FILE_DELETED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("4670", "FILE_PERMISSION_CHANGED"));
        operationTypeAccordingToEventCodeCases.add(new SwitchCaseTransformer.SwitchCase("5145", FILE_OPENED));
        SwitchCaseTransformer operationTypeAccordingToEventCodeSwitchCaseTransformer =
                new SwitchCaseTransformer("operation_type-according-to-event-code",EVENT_CODE_FIELD_NAME,
                        OPERATION_TYPE_FIELD_NAME, null,operationTypeAccordingToEventCodeCases);

        //if else transformer between 4663 and (4660, 4670, 5145)
        JsonObjectRegexPredicate referenceIdEqual4663 =
                new JsonObjectRegexPredicate("reference-id-equal-4663", EVENT_CODE_FIELD_NAME, "4663");
        IfElseTransformer operationTypeIfElseTransformer =
                new IfElseTransformer("operation-type-if-else-transformer",
                        referenceIdEqual4663,
                        operationTypeFor4663ChainTransformer,
                        operationTypeAccordingToEventCodeSwitchCaseTransformer);
        transformerChainList.add(operationTypeIfElseTransformer);

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

        //Filling the srcFilePath
        List<SwitchCaseTransformer.SwitchCase> srcFilePathCases = new ArrayList<>();
        srcFilePathCases.add(new SwitchCaseTransformer.SwitchCase("4663",wrapWithDollar(OBJ_NAME_FIELD_NAME)));
        srcFilePathCases.add(new SwitchCaseTransformer.SwitchCase("4670",wrapWithDollar(OBJ_NAME_FIELD_NAME)));
        srcFilePathCases.add(new SwitchCaseTransformer.SwitchCase("5145",wrapWithDollar(FILENAME_FIELD_NAME)));
        SwitchCaseTransformer srcFilePathSwitchCaseTransformer =
                new SwitchCaseTransformer("src-file-path-switch-case",EVENT_CODE_FIELD_NAME,
                        SRC_FILE_PATH_FIELD_NAME, null,srcFilePathCases);
        transformerChainList.add(srcFilePathSwitchCaseTransformer);

        //filtering the srcFilePath if its contains one of the empty values (<none>, -)
        JsonObjectRegexPredicate srcFilePathContainsEmptyValuePredicate = new JsonObjectRegexPredicate("src-file-path-contains-empty-value", SRC_FILE_PATH_FIELD_NAME, "-|<none>");
        FilterKeyTransformer srcFilePathFilterField = new FilterKeyTransformer("src-file-path-filter", SRC_FILE_PATH_FIELD_NAME);
        IfElseTransformer srcFilePathContainsEmptyValueFilterField =
                new IfElseTransformer(
                        "src-file-path-contains-empty-value-filter",
                        srcFilePathContainsEmptyValuePredicate,
                        srcFilePathFilterField);
        transformerChainList.add(srcFilePathContainsEmptyValueFilterField);

        // Filling the isSrcDriveShared according to the prefix of the source file path
        List<SwitchCaseTransformer.SwitchCase> isSrcDriveSharedCases = new ArrayList<>();
        isSrcDriveSharedCases.add(new SwitchCaseTransformer.SwitchCase("^[\\\\]+.*$", true, true));
        SwitchCaseTransformer isSrcDriveSharedSwitchCaseTransformer =
                new SwitchCaseTransformer("is-src-drive-shared-switch-case",SRC_FILE_PATH_FIELD_NAME,
                        IS_SRC_DRIVE_SHARED_FIELD_NAME, false,isSrcDriveSharedCases);
        transformerChainList.add(isSrcDriveSharedSwitchCaseTransformer);

        //Fixing the operation type from file operation to folder operation according to the file path suffix
        JsonObjectRegexPredicate isSrcFilePathFolder = new JsonObjectRegexPredicate("is_src_file_path_folder", SRC_FILE_PATH_FIELD_NAME, "^(.*[\\\\\\\\])*[^\\\\.]*$");
        FindAndReplaceTransformer fileToFolderReplacementOperationType = new FindAndReplaceTransformer("file_to_folder_replacement_operation_type",
                OPERATION_TYPE_FIELD_NAME, "^FILE_", "FOLDER_");
        IfElseTransformer fileToFolderOperationType =
                new IfElseTransformer("file_to_folder_operation_type", isSrcFilePathFolder, fileToFolderReplacementOperationType);
        transformerChainList.add(fileToFolderOperationType);

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

        // copy user_dst to userName
        CopyValueTransformer copyUserDst =
                new CopyValueTransformer(
                        "copy-user-dst",
                        USER_DST_FIELD_NAME,
                        true,
                        Arrays.asList(USERNAME_FIELD_NAME));
        transformerChainList.add(copyUserDst);

        // copy userId to userDisplayName
        CopyValueTransformer copyUserId =
                new CopyValueTransformer(
                        "copy-user-id",
                        USER_ID_FIELD_NAME,
                        false,
                        Arrays.asList(USER_DISPLAY_NAME_FIELD_NAME));
        transformerChainList.add(copyUserId);

        //Convert time field from EPOCH millis to EPOCH seconds
        EpochTimeToNanoRepresentationTransformer dateTimeMillisToSeconds =
                new EpochTimeToNanoRepresentationTransformer("date-time-millis-to-nano-representation", EVENT_TIME_FIELD_NAME, DATE_TIME_FIELD_NAME);
        transformerChainList.add(dateTimeMillisToSeconds);


        //The File Windows Audit Transformer that chain all the transformers together.
        JsonObjectChainTransformer fileWindowsAuditTransformer =
                new JsonObjectChainTransformer("file-windows-audit-transformer", transformerChainList);

        return fileWindowsAuditTransformer;
    }

    private String containedRegex(String containedStr){
        containedStr = containedStr.replaceAll("\\)", "\\\\)");
        containedStr = containedStr.replaceAll("\\(", "\\\\(");
        return ".*"+containedStr+".*";
    }

    private JSONObject buildFileWindowAuditJsonObject(
            String eventCode,
            String userDst,
            String deviceType,
            String category,
            String accesses,
            Long eventTime,
            String objName,
            String filename,
            String eventType,
            String resultCode,
            String eventId,
            String directory,
            String objType
    ){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVENT_CODE_FIELD_NAME, eventCode);
        jsonObject.put(USER_DST_FIELD_NAME, userDst);
        jsonObject.put(DEVICE_TYPE_FIELD_NAME, deviceType);
        jsonObject.put(CATEGORY_FIELD_NAME, category);
        jsonObject.put(ACCESSES_FIELD_NAME, accesses);
        jsonObject.put(EVENT_TIME_FIELD_NAME, Long.toString(eventTime));
        jsonObject.put(OBJ_NAME_FIELD_NAME, objName);
        jsonObject.put(FILENAME_FIELD_NAME, filename);
        jsonObject.put(EVENT_TYPE_FIELD_NAME, eventType);
        jsonObject.put(RESULT_CODE_FIELD_NAME, resultCode);
        jsonObject.put(EVENT_SOURCE_ID_FIELD_NAME, eventId);
        jsonObject.put(DIRECTORY_FIELD_NAME, directory);
        jsonObject.put(OBJ_TYPE_FIELD_NAME, objType);

        return jsonObject;
    }

    @Test
    public void filter_device_type_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();

        JSONObject jsonObject = buildFileWindowAuditJsonObject("4663","testUser", "unkonwn_device_type", "File System","ReadData (or ListDirectory)",
                1528124556000L, "obj-name-test", null, "Success Audit", "  ", "10.25.67.33:50005:91168521", null, "File");

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered due to unknown device type", retJsonObject);
    }

    @Test
    public void filter_user_dst_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();

        JSONObject jsonObject = buildFileWindowAuditJsonObject("4663", "testUser$", "winevent_snare", "File System","ReadData (or ListDirectory)",
                1528124556000L, "obj-name-test", null, "Success Audit", "  ", "10.25.67.33:50005:91168521", null, "File");

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered due to user_dst which is a machine", retJsonObject);
    }

    @Test
    public void filter_category_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();

        JSONObject jsonObject = buildFileWindowAuditJsonObject("4663", "testUser", "winevent_snare", "token","ReadData (or ListDirectory)",
                1528124556000L, "obj-name-test", null, "Success Audit", "  ", "10.25.67.33:50005:91168521", null, "File");

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered due to category not equal to File System", retJsonObject);
    }

    @Test
    public void filter_accesses_test1() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();

        JSONObject jsonObject = buildFileWindowAuditJsonObject("4663", "testUser", "winevent_snare", "File System","DeleteChild",
                1528124556000L, "\\Device\\HarddiskVolume28\\testing.file", null, "Success Audit", "  ", "10.25.67.33:50005:91168521",
                null, "File");

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered due to accesses = READ_CONTROL", retJsonObject);
    }

    @Test
    public void filter_accesses_test2() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();

        JSONObject jsonObject = buildFileWindowAuditJsonObject("4663", "testUser", "winevent_snare", "File System","DeleteChild",
                1528124556000L, "\\Device\\HarddiskVolume28\\testing.file", null, "Success Audit", "  ", "10.25.67.33:50005:91168521",
                null, "File");

        JSONObject retJsonObject = transform(transformer, jsonObject, true);

        Assert.assertNull("the event should have been filtered due to accesses = DeleteChild", retJsonObject);
    }

    @Test
    public void filter_4670_with_obj_type_not_file_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();

        String userDst = "testUser";
        String eventSourceId = "10.25.67.33:50005:91168521";
        String referenceId = "4670";
        Long eventTime = 1528124556L;
        String objName = "C:\\HarddiskVolume28";
        JSONObject jsonObject = buildFileWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                "File System","ReadData (or ListDirectory)",
                eventTime*1000, objName, "<none>",
                "Failed again", "0x0", eventSourceId, null, "token");

        transform(transformer, jsonObject, true);
    }

    private void assertOnExpectedValues(JSONObject retJsonObject,
                                        String expectedUserId,
                                        String expectedSrcFilePath,
                                        Boolean expectedIsSrcDriveShared,
                                        String expectedOperationType,
                                        String expectedResult,
                                        String expectedEventId,
                                        String expectedDataSource,
                                        String expectedUsername,
                                        String expectedUserDisplayName,
                                        Long expectedDateTime){
        Assert.assertEquals("username normalization did not work", expectedUserId, retJsonObject.get(USER_ID_FIELD_NAME));
        Assert.assertEquals("source file path is not as expected", expectedSrcFilePath, retJsonObject.opt(SRC_FILE_PATH_FIELD_NAME));
        Assert.assertEquals(String.format("is shared drive should be true. event: %s", retJsonObject), expectedIsSrcDriveShared, retJsonObject.opt(IS_SRC_DRIVE_SHARED_FIELD_NAME));
        Assert.assertEquals("operation type logic did not work", expectedOperationType, retJsonObject.get(OPERATION_TYPE_FIELD_NAME));
        Assert.assertEquals("result normalization did not work", expectedResult, retJsonObject.get(RESULT_FIELD_NAME));
        Assert.assertEquals("wrong event id", expectedEventId, retJsonObject.get(EVENT_ID_FIELD_NAME));
        Assert.assertEquals("wrong data source", expectedDataSource, retJsonObject.get(DATA_SOURCE_FIELD_NAME));
        Assert.assertEquals("wrong username", expectedUsername, retJsonObject.get(USERNAME_FIELD_NAME));
        Assert.assertEquals("wrong userDisplayName", expectedUserDisplayName, retJsonObject.get(USER_DISPLAY_NAME_FIELD_NAME));
        Assert.assertEquals("wrong dateTime", new Double(expectedDateTime), retJsonObject.get(DATE_TIME_FIELD_NAME));
    }

    @Test
    public void not_filtered1_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();
        String userDst = "testUser";
        String objName = "\\Device\\HarddiskVolume28\\testing.file";
        String eventSourceId = "10.25.67.33:50005:91168521";
        String referenceId = "4663";
        Long eventTime = 1528124556L;
        JSONObject jsonObject = buildFileWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                "File System","AppendData (or AddSubdirectory or CreatePipeInstance) & READ_CONTROL",
                eventTime*1000, objName,
                null, "Success Audit", "0x1", eventSourceId, null, "File");

        JSONObject retJsonObject = transform(transformer, jsonObject);

        String userId = userDst.toLowerCase();
        assertOnExpectedValues(retJsonObject, userId, objName, true, FILE_MODIFIED, RESULT_SUCCESS,
                eventSourceId, referenceId, userDst, userId, eventTime);
    }

    @Test
    public void not_filtered2_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();

        String userDst = "testUser";
        String eventSourceId = "10.25.67.33:50005:91168521";
        String referenceId = "5145";
        Long eventTime = 1528124556L;
        JSONObject jsonObject = buildFileWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                "File System","ReadData (or ListDirectory)",
                eventTime*1000, "\\Device\\HarddiskVolume28\\testing.file", "<none>",
                "Failed again", "0x0", eventSourceId, null, null);

        JSONObject retJsonObject = transform(transformer, jsonObject);

        String userId = userDst.toLowerCase();
        assertOnExpectedValues(retJsonObject, userId, null, null, FILE_OPENED, RESULT_FAILURE,
                eventSourceId, referenceId, userDst, userId, eventTime);
    }

    @Test
    public void not_filtered3_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();

        String userDst = "testUser";
        String objName = "C:\\HarddiskVolume28";
        String eventSourceId = "10.25.67.33:50005:91168521";
        String referenceId = "4663";
        Long eventTime = 1528124556L;
        JSONObject jsonObject = buildFileWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                "File System","ReadData (or ListDirectory)",
                eventTime*1000, objName,
                null, null, "0x1", eventSourceId, null, "");

        JSONObject retJsonObject = transform(transformer, jsonObject);

        String userId = userDst.toLowerCase();
        assertOnExpectedValues(retJsonObject, userId, objName, false,
                "FOLDER_OPENED", RESULT_FAILURE,
                eventSourceId, referenceId, userDst, userId, eventTime);
    }

    @Test
    public void not_filtered4_test() throws JsonProcessingException {
        IJsonObjectTransformer transformer = buildFileWindowsAuditTransformer();

        String userDst = "testUser";
        String eventSourceId = "10.25.67.33:50005:91168521";
        String referenceId = "4670";
        Long eventTime = 1528124556L;
        String objName = "C:\\HarddiskVolume28";
        JSONObject jsonObject = buildFileWindowAuditJsonObject(referenceId, userDst, "winevent_snare",
                "File System","ReadData (or ListDirectory)",
                eventTime*1000, objName, "<none>",
                "Failed again", "0x0", eventSourceId, null, "File");

        JSONObject retJsonObject = transform(transformer, jsonObject);

        String userId = userDst.toLowerCase();
        assertOnExpectedValues(retJsonObject, userId, objName, false, "FOLDER_PERMISSION_CHANGED", RESULT_FAILURE,
                eventSourceId, referenceId, userDst, userId, eventTime);
    }
}
