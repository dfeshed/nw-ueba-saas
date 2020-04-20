package presidio.ade.domain.record.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import presidio.ade.domain.record.AdeRecordReader;

public class EqualsAdeRecordReaderPredicateTest {
    private static final String DEFAULT_FIELD_NAME = "myField";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void actual_value_is_equal_to_null_expected_value() {
        EqualsAdeRecordReaderPredicate equalsAdeRecordReaderPredicate = createPredicate(null);
        AdeRecordReader adeRecordReader = mockReader(null);
        Assert.assertTrue(equalsAdeRecordReaderPredicate.test(adeRecordReader));
    }

    @Test
    public void actual_value_is_not_equal_to_null_expected_value() {
        EqualsAdeRecordReaderPredicate equalsAdeRecordReaderPredicate = createPredicate(null);
        AdeRecordReader adeRecordReader = mockReader("notNull");
        Assert.assertFalse(equalsAdeRecordReaderPredicate.test(adeRecordReader));
    }

    @Test
    public void actual_value_is_equal_to_boolean_expected_value() {
        EqualsAdeRecordReaderPredicate equalsAdeRecordReaderPredicate = createPredicate(true);
        AdeRecordReader adeRecordReader = mockReader(true);
        Assert.assertTrue(equalsAdeRecordReaderPredicate.test(adeRecordReader));
    }

    @Test
    public void actual_value_is_not_equal_to_boolean_expected_value() {
        EqualsAdeRecordReaderPredicate equalsAdeRecordReaderPredicate = createPredicate(true);
        AdeRecordReader adeRecordReader = mockReader(false);
        Assert.assertFalse(equalsAdeRecordReaderPredicate.test(adeRecordReader));
    }

    @Test
    public void actual_value_is_equal_to_numerical_expected_value() {
        EqualsAdeRecordReaderPredicate equalsAdeRecordReaderPredicate = createPredicate(42);
        AdeRecordReader adeRecordReader = mockReader(42);
        Assert.assertTrue(equalsAdeRecordReaderPredicate.test(adeRecordReader));
    }

    @Test
    public void actual_value_is_not_equal_to_numerical_expected_value() {
        EqualsAdeRecordReaderPredicate equalsAdeRecordReaderPredicate = createPredicate(42);
        AdeRecordReader adeRecordReader = mockReader(Math.PI);
        Assert.assertFalse(equalsAdeRecordReaderPredicate.test(adeRecordReader));
    }

    @Test
    public void actual_value_is_equal_to_textual_expected_value() {
        EqualsAdeRecordReaderPredicate equalsAdeRecordReaderPredicate = createPredicate("myValue");
        AdeRecordReader adeRecordReader = mockReader("myValue");
        Assert.assertTrue(equalsAdeRecordReaderPredicate.test(adeRecordReader));
    }

    @Test
    public void actual_value_is_not_equal_to_textual_expected_value() {
        EqualsAdeRecordReaderPredicate equalsAdeRecordReaderPredicate = createPredicate("myValue");
        AdeRecordReader adeRecordReader = mockReader("notMyValue");
        Assert.assertFalse(equalsAdeRecordReaderPredicate.test(adeRecordReader));
    }

    @Test
    public void actual_value_is_not_equal_to_expected_value() {
        EqualsAdeRecordReaderPredicate equalsAdeRecordReaderPredicate = createPredicate("myValue", true);
        AdeRecordReader adeRecordReader = mockReader("notMyValue");
        Assert.assertTrue(equalsAdeRecordReaderPredicate.test(adeRecordReader));
    }

    private static EqualsAdeRecordReaderPredicate createPredicate(Object expectedValue, boolean negate) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", EqualsAdeRecordReaderPredicate.ADE_RECORD_READER_PREDICATE_TYPE);
        jsonObject.put("fieldName", DEFAULT_FIELD_NAME);
        jsonObject.put("expectedValue", expectedValue);
        if (negate) jsonObject.put("negate", true);
        String jsonString = jsonObject.toString();

        try {
            return (EqualsAdeRecordReaderPredicate)objectMapper.readValue(jsonString, AdeRecordReaderPredicate.class);
        } catch (Exception exception) {
            String format = "Exception caught when deserializing JSON string %s to %s.";
            String classSimpleName = EqualsAdeRecordReaderPredicate.class.getSimpleName();
            String message = String.format(format, jsonString, classSimpleName);
            throw new RuntimeException(message, exception);
        }
    }

    private static EqualsAdeRecordReaderPredicate createPredicate(Object expectedValue) {
        return createPredicate(expectedValue, false);
    }

    private static AdeRecordReader mockReader(Object actualValue) {
        AdeRecordReader adeRecordReader = Mockito.mock(AdeRecordReader.class);
        Mockito.when(adeRecordReader.get(Mockito.eq(DEFAULT_FIELD_NAME))).thenReturn(actualValue);
        return adeRecordReader;
    }
}
