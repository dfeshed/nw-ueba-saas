package fortscale.utils.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static fortscale.utils.transform.TransformerUtil.assertJsonObjectKeyNotAdded;
import static fortscale.utils.transform.TransformerUtil.assertNewJsonObjectNotContainsOriginalJsonObject;


/**
 * Testing SwitchCaseTransformer by using rules from the Authentication Windows audit event.
 */
public class SwitchCaseTransformerOnDollarFunctionalityTest extends TransformerTest{

    private static final String ALIAS_HOST_FIELD_NAME = "alias_host";
    private static final String HOST_SRC_FIELD_NAME = "host_src";
    private static final String SRC_MACHINE = "src_machine";
    private static final String EVENT_CODE_FIELD_NAME = "reference_id";





    private IJsonObjectTransformer buildTransformer(String sourceKey, String destinationKey, Object destinationDefaultValue, List<SwitchCaseTransformer.SwitchCase> cases) {
        return new SwitchCaseTransformer("testName",sourceKey,destinationKey,destinationDefaultValue,cases);
    }

    private String wrapWithDollar(String fieldName){
        return String.format("${%s}", fieldName);
    }

    private JSONObject dollarCaseValueTest(String eventCode, String aliasHostValue, String hostSrcValue) throws JsonProcessingException {
        List<SwitchCaseTransformer.SwitchCase> cases = new ArrayList<>();
        cases.add(new SwitchCaseTransformer.SwitchCase("4624",wrapWithDollar(ALIAS_HOST_FIELD_NAME)));
        cases.add(new SwitchCaseTransformer.SwitchCase("4776",wrapWithDollar(HOST_SRC_FIELD_NAME)));
        cases.add(new SwitchCaseTransformer.SwitchCase("4769",wrapWithDollar(ALIAS_HOST_FIELD_NAME)));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVENT_CODE_FIELD_NAME, eventCode);
        jsonObject.put(ALIAS_HOST_FIELD_NAME, aliasHostValue);
        jsonObject.put(HOST_SRC_FIELD_NAME, hostSrcValue);

        IJsonObjectTransformer transformer = buildTransformer(EVENT_CODE_FIELD_NAME, SRC_MACHINE, null, cases);
        JSONObject retJsonObject = transform(transformer, jsonObject);

        assertNewJsonObjectNotContainsOriginalJsonObject(retJsonObject, jsonObject);

        assertJsonObjectKeyNotAdded(retJsonObject, SRC_MACHINE);



        return retJsonObject;
    }

    @Test
    public void dollarCaseValueTest1() throws JsonProcessingException {
        String eventCode = "4444";
        String aliasHostValue = "aliasHostMachine";
        String hostSrcValue = "hostSrcValue";
        JSONObject jsonObject = dollarCaseValueTest(eventCode,aliasHostValue, hostSrcValue);

        Assert.assertNotNull(String.format("The key %s should have value null since event code %s" +
                        " have no mapping in the configuration. event: %s", SRC_MACHINE, eventCode, jsonObject),
                jsonObject.get(SRC_MACHINE));
    }

    @Test
    public void dollarCaseValueTest2() throws JsonProcessingException {
        String eventCode = "4624";
        String aliasHostValue = "aliasHostMachine";
        String hostSrcValue = "hostSrcValue";
        JSONObject jsonObject = dollarCaseValueTest(eventCode,aliasHostValue, hostSrcValue);

        Assert.assertTrue(String.format("The %s field has been added with the wrong value. expected value: %s, actual value: %s event: %s",
                SRC_MACHINE, aliasHostValue, jsonObject.getString(SRC_MACHINE), jsonObject),
                jsonObject.getString(SRC_MACHINE).equals(aliasHostValue));

        eventCode = "4769";
        jsonObject = dollarCaseValueTest(eventCode,aliasHostValue, hostSrcValue);
        Assert.assertTrue(String.format("The %s field has been added with the wrong value. expected key value: %s, event: %s",
                SRC_MACHINE, aliasHostValue, jsonObject.getString(SRC_MACHINE), jsonObject),
                jsonObject.getString(SRC_MACHINE).equals(aliasHostValue));
    }

    @Test
    public void dollarCaseValueTest3() throws JsonProcessingException {
        String eventCode = "4776";
        String aliasHostValue = "aliasHostMachine";
        String hostSrcValue = "hostSrcValue";
        JSONObject jsonObject = dollarCaseValueTest(eventCode,aliasHostValue, hostSrcValue);


        Assert.assertTrue(String.format("The %s field has been added with the wrong value. expected key value: %s, event: %s",
                SRC_MACHINE, hostSrcValue, jsonObject.getString(SRC_MACHINE), jsonObject),
                jsonObject.getString(SRC_MACHINE).equals(hostSrcValue));
    }
}
