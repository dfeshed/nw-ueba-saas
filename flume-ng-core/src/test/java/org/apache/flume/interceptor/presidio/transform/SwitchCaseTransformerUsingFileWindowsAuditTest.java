package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flume.interceptor.presidio.transform.SwitchCaseTransformer.SwitchCase;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.flume.interceptor.presidio.transform.TransformerUtil.assertJsonObjectKeyNotAdded;
import static org.apache.flume.interceptor.presidio.transform.TransformerUtil.assertNewJsonObjectNotContainsOriginalJsonObject;


/**
 * Testing SwitchCaseTransformer by using rules from the File Windows audit event. (most of the tests)
 */
public class SwitchCaseTransformerUsingFileWindowsAuditTest extends TransformerTest{
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
    private static final List<String> CASES = Arrays.asList(ACCESSES_CREATE_OPERATION_VALUE, ACCESSES_OPEN_OPERATION_VALUE, ACCESSES_MODIFIED_OPERATION_VALUE, ACCESSES_WRITE_DAC_OPERATION_VALUE, ACCESSES_WRITE_OWNER_OPERATION_VALUE);
    private static final List<String> CASES_VALUES = Arrays.asList(FILE_CREATED, FILE_OPEN, FILE_MODIFIED, FILE_WRITE_DAC_PERMISSION_CHANGED, FILE_WRITE_OWNER_PERMISSION_CHANGED);
    private static final String EVENT_CODE_FIELD_NAME = "reference_id";







    private IJsonObjectTransformer buildTransformer(String sourceKey, String destinationKey, Object destinationDefaultValue, List<SwitchCase> cases){
        return new SwitchCaseTransformer("testName",sourceKey,destinationKey,destinationDefaultValue,cases);
    }

    private IJsonObjectTransformer createTransformerOnAccessesField() throws JsonProcessingException {
        List<SwitchCase> cases = new ArrayList<>();
        for(int i = 0; i < CASES.size(); i++){
            cases.add(new SwitchCase(containedRegex(CASES.get(i)),CASES_VALUES.get(i), true));
        }

        return buildTransformer(ACCESSES_FIELD_NAME, OPERATION_TYPE_FIELD_NAME, null, cases);
    }

    private String containedRegex(String containedStr){
        containedStr = containedStr.replaceAll("\\)", "\\\\)");
        containedStr = containedStr.replaceAll("\\(", "\\\\(");
        return ".*"+containedStr+".*";
    }

    private void transformEventAndTestOperationType(String eventCode, String accessesValue, String expectedOperationType) throws JsonProcessingException {
        JSONObject jsonObject = transformEventWithAccessesField(eventCode, accessesValue);
        assertJsonObjectKeyNotAdded(jsonObject,OPERATION_TYPE_FIELD_NAME);
        Assert.assertTrue(String.format("The wrong value has been inserted to the key %s. expected value: %s, actual value: %s event: %s",
                OPERATION_TYPE_FIELD_NAME, expectedOperationType, jsonObject.get(OPERATION_TYPE_FIELD_NAME), jsonObject),
                jsonObject.get(OPERATION_TYPE_FIELD_NAME).equals(expectedOperationType));
    }



    private JSONObject transformEventWithAccessesField(String eventCode, String accessesValue) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ACCESSES_FIELD_NAME, accessesValue);
        if(eventCode!=null) {
            jsonObject.put(EVENT_CODE_FIELD_NAME, eventCode);
        }

        IJsonObjectTransformer transformer = createTransformerOnAccessesField();

        JSONObject retJsonObject = transform(transformer,jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);

        return retJsonObject;
    }

    @Test
    public void transformFileCreatedEventTest() throws JsonProcessingException {
        transformEventAndTestOperationType("4663", ACCESSES_CREATE_OPERATION_VALUE, FILE_CREATED);
    }

    @Test
    public void transformFileCreatedEventWithNoEventCodeConfAndNoEventCodeValueTest() throws JsonProcessingException {
        transformEventAndTestOperationType(null, ACCESSES_CREATE_OPERATION_VALUE, FILE_CREATED);
    }

    @Test
    public void transformFileCreatedEventWithMultiAccessesValuesTest() throws JsonProcessingException {
        String multiAccessesValues = String.join(",",ACCESSES_OPEN_OPERATION_VALUE,ACCESSES_WRITE_OWNER_OPERATION_VALUE,
                ACCESSES_CREATE_OPERATION_VALUE, ACCESSES_WRITE_DAC_OPERATION_VALUE);
        transformEventAndTestOperationType("4663", multiAccessesValues, FILE_CREATED);
    }

    @Test
    public void transformFileOpenEventWithMultiAccessesValuesTest() throws JsonProcessingException {
        String multiAccessesValues = String.join(",",ACCESSES_MODIFIED_OPERATION_VALUE,ACCESSES_OPEN_OPERATION_VALUE,
                ACCESSES_WRITE_OWNER_OPERATION_VALUE, ACCESSES_WRITE_DAC_OPERATION_VALUE);
        transformEventAndTestOperationType("4663", multiAccessesValues, FILE_OPEN);
    }

    @Test
    public void transformFileFileModifiedEventTest() throws JsonProcessingException {
        transformEventAndTestOperationType("4663", ACCESSES_MODIFIED_OPERATION_VALUE, FILE_MODIFIED);
    }

    @Test
    public void transformFileModifiedEventWithMultiAccessesValuesTest() throws JsonProcessingException {
        String multiAccessesValues = String.join(",",ACCESSES_WRITE_DAC_OPERATION_VALUE,ACCESSES_MODIFIED_OPERATION_VALUE,
                ACCESSES_WRITE_OWNER_OPERATION_VALUE, ACCESSES_WRITE_ATTRIBUTE_OPERATION_VALUE);
        transformEventAndTestOperationType("4663", multiAccessesValues, FILE_MODIFIED);
    }

    @Test
    public void transformFileFileWriteOwnerEventTest() throws JsonProcessingException {
        transformEventAndTestOperationType("4663", ACCESSES_WRITE_OWNER_OPERATION_VALUE, FILE_WRITE_OWNER_PERMISSION_CHANGED);
    }

    @Test
    public void transformMultiAccessesValuesWithNoOperationTypeResolvingTest() throws JsonProcessingException {
        String multiAccessesValues = String.join(",",ACCESSES_READ_ATTRIBUTE_OPERATION_VALUE,ACCESSES_WRITE_ATTRIBUTE_OPERATION_VALUE);
        JSONObject jsonObject = transformEventWithAccessesField("4663", multiAccessesValues);
        assertJsonObjectKeyNotAdded(jsonObject, OPERATION_TYPE_FIELD_NAME);
        Assert.assertNotNull(String.format("The operation type should have been null since the accesses values input do not" +
                        " have resoving in the configuration. event: %s", jsonObject),
                jsonObject.get(OPERATION_TYPE_FIELD_NAME));
    }


}
