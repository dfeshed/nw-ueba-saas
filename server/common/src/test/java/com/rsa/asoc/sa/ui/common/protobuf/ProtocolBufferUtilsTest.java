package com.rsa.asoc.sa.ui.common.protobuf;

import com.google.common.base.Defaults;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol.Dictionary;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol.PropertyList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ProtocolBufferUtils}.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class ProtocolBufferUtilsTest {

    @Mock
    private Consumer<String> stringConsumer;

    @Mock
    private Consumer<RoundingMode> roundingModeConsumer;

    @Mock
    private Consumer<Instant> instantConsumer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateDictionaryFromMap() {
        final String hostname = "device.netwitness.local";
        final Integer port = 12345;
        final Boolean secure = true;

        Map<String, Object> map = ImmutableMap.<String, Object>builder()
                .put("Hostname", hostname)
                .put("Port", port)
                .put("EnableSSL", secure)
                .build();

        Dictionary dictionary = ProtocolBufferUtils.createDictionaryFromMap(map);
        assertEquals(3, dictionary.getEntryCount());
        assertEquals(hostname, ProtocolBufferUtils.getDictionaryValue(dictionary, "Hostname", String.class));
        assertEquals(port, ProtocolBufferUtils.getDictionaryValue(dictionary, "Port", Integer.class));
        assertEquals(secure, ProtocolBufferUtils.getDictionaryValue(dictionary, "EnableSSL", Boolean.class));
    }

    /**
     * Tests creating a dictionary from a map that contains an embedded map.
     * For Incident Mangement we found that embedded null keys caused an exception to be thrown, so this test helps verify that condition also.
     */
    @Test
    public void testCreateDictionaryFromNestedMap() {
        final String hostname = "device.netwitness.local";
        final Integer port = 12345;
        final Boolean secure = true;

        Map<String, Object> embedded = new HashMap<String, Object>() {{
            put("key", "somevalue");
            put("key2", null);
            put("intkey", 23);
            put("boolkey", true);
        }};

        Map<String, Object> map = ImmutableMap.<String, Object>builder()
                .put("Hostname", hostname)
                .put("Port", port)
                .put("EnableSSL", secure)
                .put("embeddedMap", embedded)
                .build();

        Dictionary dictionary = ProtocolBufferUtils.createDictionaryFromMap(map);
        assertEquals(4, dictionary.getEntryCount());
        assertEquals(hostname, ProtocolBufferUtils.getDictionaryValue(dictionary, "Hostname", String.class));
        assertEquals(port, ProtocolBufferUtils.getDictionaryValue(dictionary, "Port", Integer.class));
        assertEquals(secure, ProtocolBufferUtils.getDictionaryValue(dictionary, "EnableSSL", Boolean.class));

        embedded.remove("key2"); // null keys are removed by the translaction process
        assertEquals(embedded, ProtocolBufferUtils.getDictionaryValue(dictionary, "embeddedMap", Map.class));
    }

    @Test
    public void testCreateMapFromDictionary() {
        final String hostname = "device.netwitness.local";
        final Integer port = 12345;
        final Boolean secure = true;

        Map<String, Object> map = ImmutableMap.<String, Object>builder()
                .put("Hostname", hostname)
                .put("Port", port)
                .put("EnableSSL", secure)
                .build();

        Dictionary dictionary = ProtocolBufferUtils.createDictionaryFromMap(map);
        assertEquals(3, dictionary.getEntryCount());
        assertEquals(hostname, ProtocolBufferUtils.getDictionaryValue(dictionary, "Hostname", String.class));
        assertEquals(port, ProtocolBufferUtils.getDictionaryValue(dictionary, "Port", Integer.class));
        assertEquals(secure, ProtocolBufferUtils.getDictionaryValue(dictionary, "EnableSSL", Boolean.class));

        Map<String, Object> result = ProtocolBufferUtils.createMapFromDictionary(dictionary);
        assertEquals(3, result.size());
        assertEquals(hostname, result.get("Hostname"));
        assertEquals(port, result.get("Port"));
        assertEquals(secure, result.get("EnableSSL"));
    }

    @Test
    public void testCreateMapFromDictionaryWithEmbeddedDictionary() {
        final String hostname = "device.netwitness.local";
        final Integer port = 12345;
        final Boolean secure = true;

        Map<String, Object> map = ImmutableMap.<String, Object>builder()
                .put("Hostname", hostname)
                .put("Port", port)
                .put("EnableSSL", secure)
                .build();

        Map<String, Object> embedded = ImmutableMap.<String, Object>builder()
                .put("param1", "value1")
                .put("param2", "value2")
                .put("param3", "value3")
                .build();

        PropertyListProtocol.Dictionary.Entry embeddedEntry =
                ProtocolBufferUtils.createEntry("parameters", ProtocolBufferUtils.createDictionaryFromMap(embedded));

        Dictionary dictionary = ProtocolBufferUtils.createDictionaryFromMap(map);
        dictionary = dictionary.toBuilder()
                .addEntry(embeddedEntry)
                .build();

        assertEquals(4, dictionary.getEntryCount());
        assertEquals(hostname, ProtocolBufferUtils.getDictionaryValue(dictionary, "Hostname", String.class));
        assertEquals(port, ProtocolBufferUtils.getDictionaryValue(dictionary, "Port", Integer.class));
        assertEquals(secure, ProtocolBufferUtils.getDictionaryValue(dictionary, "EnableSSL", Boolean.class));

        Map<String, Object> result = ProtocolBufferUtils.createMapFromDictionary(dictionary);
        assertEquals(4, result.size());
        assertEquals(hostname, result.get("Hostname"));
        assertEquals(port, result.get("Port"));
        assertEquals(secure, result.get("EnableSSL"));
        assertEquals("value1", ((Map) result.get("parameters")).get("param1"));
        assertEquals("value2", ((Map) result.get("parameters")).get("param2"));
        assertEquals("value3", ((Map) result.get("parameters")).get("param3"));
    }

    @Test
    public void testCreatePropertyList() {
        final String stringValue = "Hello";
        PropertyList stringProperty = ProtocolBufferUtils.createPropertyList(stringValue);
        assertNotNull(stringProperty);
        assertEquals(PropertyList.Type.String, stringProperty.getType());
        assertEquals(stringValue, stringProperty.getString());

        final long longValue = 1L;
        PropertyList longProperty = ProtocolBufferUtils.createPropertyList(longValue);
        assertNotNull(longProperty);
        assertEquals(PropertyList.Type.Number, longProperty.getType());
        assertEquals(PropertyListProtocol.Number.Type.INT_64, longProperty.getNumber().getType());
        assertEquals(longValue, longProperty.getNumber().getInt64());

        final int intValue = 1;
        PropertyList intProperty = ProtocolBufferUtils.createPropertyList(intValue);
        assertNotNull(intProperty);
        assertEquals(PropertyList.Type.Number, intProperty.getType());
        assertEquals(PropertyListProtocol.Number.Type.INT_32, intProperty.getNumber().getType());
        assertEquals(intValue, intProperty.getNumber().getInt32());

        final boolean boolValue = true;
        PropertyList boolProperty = ProtocolBufferUtils.createPropertyList(boolValue);
        assertNotNull(boolProperty);
        assertEquals(PropertyList.Type.Boolean, boolProperty.getType());
        assertEquals(boolValue, boolProperty.getBoolean());

        final Date dateValue = new Date();
        PropertyList dateProperty = ProtocolBufferUtils.createPropertyList(dateValue);
        assertNotNull(dateProperty);
        assertEquals(PropertyList.Type.Date, dateProperty.getType());
        assertEquals(dateValue.getTime(), dateProperty.getDate());
    }

    @Test
    public void testCreatePropertyListWithIterable() {
        List<String> list = Lists.newArrayList("A", "B", "C", "D");
        PropertyList propertyList = ProtocolBufferUtils.createPropertyList(list);
        assertNotNull(propertyList);
        assertEquals(PropertyList.Type.Array, propertyList.getType());
        assertEquals(list.size(), propertyList.getArray().getElementCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePropertyListUnknownType() {
        ProtocolBufferUtils.createPropertyList(new Object());
    }

    @Test(expected = NullPointerException.class)
    public void testCreatePropertyListWithNullValueArgument() {
        ProtocolBufferUtils.createPropertyList(null);
    }

    @Test(expected = NullPointerException.class)
    public void testCreatePropertyListWithNullTypeArgument() {
        ProtocolBufferUtils.createPropertyList(1L, null);
    }

    @Test
    public void testCreatePropertyListWithNullPrimitiveWrapper() {
        PropertyList longProperty = ProtocolBufferUtils.createPropertyList(null, Long.class);
        assertNotNull(longProperty);
        assertEquals(PropertyList.Type.Number, longProperty.getType());
        assertEquals(PropertyListProtocol.Number.Type.INT_64, longProperty.getNumber().getType());
        assertEquals((long) Defaults.defaultValue(long.class), longProperty.getNumber().getInt64());

        PropertyList longPrimitiveProperty = ProtocolBufferUtils.createPropertyList(null, Long.class);
        assertNotNull(longPrimitiveProperty);
        assertEquals(PropertyList.Type.Number, longPrimitiveProperty.getType());
        assertEquals(PropertyListProtocol.Number.Type.INT_64, longPrimitiveProperty.getNumber().getType());
        assertEquals((long) Defaults.defaultValue(long.class), longPrimitiveProperty.getNumber().getInt64());

        PropertyList intProperty = ProtocolBufferUtils.createPropertyList(null, Integer.class);
        assertNotNull(intProperty);
        assertEquals(PropertyList.Type.Number, intProperty.getType());
        assertEquals(PropertyListProtocol.Number.Type.INT_32, intProperty.getNumber().getType());
        assertEquals((int) Defaults.defaultValue(int.class), intProperty.getNumber().getInt32());

        PropertyList doublePrimitiveProperty = ProtocolBufferUtils.createPropertyList(null, Double.class);
        assertNotNull(doublePrimitiveProperty);
        assertEquals(PropertyList.Type.Number, doublePrimitiveProperty.getType());
        assertEquals(PropertyListProtocol.Number.Type.REAL_64, doublePrimitiveProperty.getNumber().getType());
        assertEquals(Defaults.defaultValue(double.class), doublePrimitiveProperty.getNumber().getReal64(), .001);

        PropertyList floatPrimitiveProperty = ProtocolBufferUtils.createPropertyList(null, Float.class);
        assertNotNull(floatPrimitiveProperty);
        assertEquals(PropertyList.Type.Number, floatPrimitiveProperty.getType());
        assertEquals(PropertyListProtocol.Number.Type.REAL_32, floatPrimitiveProperty.getNumber().getType());
        assertEquals(Defaults.defaultValue(float.class), floatPrimitiveProperty.getNumber().getReal32(), .001);

        PropertyList boolProperty = ProtocolBufferUtils.createPropertyList(null, Boolean.class);
        assertNotNull(boolProperty);
        assertEquals(PropertyList.Type.Boolean, boolProperty.getType());
        assertEquals(Defaults.defaultValue(boolean.class), boolProperty.getBoolean());
    }

    @Test
    public void testCreatePropertyListWithNullValue() {
        PropertyList stringProperty = ProtocolBufferUtils.createPropertyList(null, String.class);
        assertNotNull(stringProperty);
        assertEquals(PropertyList.Type.String, stringProperty.getType());
        assertEquals("", stringProperty.getString());

        PropertyList dateProperty = ProtocolBufferUtils.createPropertyList(null, Date.class);
        assertNotNull(dateProperty);
        assertEquals(PropertyList.Type.Date, dateProperty.getType());
        assertEquals(0L, dateProperty.getDate());
    }

    @Test
    public void testGetPropertyListValue() {
        final String stringValue = "Hello";
        PropertyList stringProperty = ProtocolBufferUtils.createPropertyList(stringValue);
        assertEquals(stringValue, ProtocolBufferUtils.getPropertyListValue(stringProperty));

        final long longValue = 1L;
        PropertyList longProperty = ProtocolBufferUtils.createPropertyList(longValue);
        assertEquals(longValue, ProtocolBufferUtils.getPropertyListValue(longProperty));

        final int intValue = 1;
        PropertyList intProperty = ProtocolBufferUtils.createPropertyList(intValue);
        assertEquals(intValue, ProtocolBufferUtils.getPropertyListValue(intProperty));

        final boolean boolValue = true;
        PropertyList boolProperty = ProtocolBufferUtils.createPropertyList(boolValue);
        assertEquals(boolValue, ProtocolBufferUtils.getPropertyListValue(boolProperty));

        final Date dateValue = new Date();
        PropertyList dateProperty = ProtocolBufferUtils.createPropertyList(dateValue);
        assertEquals(dateValue, ProtocolBufferUtils.getPropertyListValue(dateProperty));
    }

    @Test
    public void testConsumeValueFromMap() {
        final String name = "Testing";
        Map<String, Object> map = ImmutableMap.<String, Object>builder()
                .put("id", 1L)
                .put("name", name)
                .build();

        ProtocolBufferUtils.consumeValueFromMap(map, "name", String.class, stringConsumer);

        verify(stringConsumer).accept(eq(name));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConsumeValueFromMapWithWrongType() {
        Map<String, Object> map = ImmutableMap.<String, Object>builder()
                .put("id", 1L)
                .put("name", new Date())
                .build();

        ProtocolBufferUtils.consumeValueFromMap(map, "name", String.class, stringConsumer);
    }

    @Test
    public void testConsumeValueFromMapWithMissingAttribute() {
        Map<String, Object> map = ImmutableMap.<String, Object>builder()
                .put("id", 1L)
                .put("name", "Testing")
                .build();

        ProtocolBufferUtils.consumeValueFromMap(map, "email", String.class, stringConsumer);

        verify(stringConsumer, never()).accept(anyString());
    }

    @Test
    public void testConsumeEnumFromMap() throws Exception {
        Map<String, Object> map = ImmutableMap.<String, Object>builder().put("mode", "DOWN").build();

        ProtocolBufferUtils.consumeEnumFromMap(map, "mode", RoundingMode.class, roundingModeConsumer);
        verify(roundingModeConsumer).accept(RoundingMode.DOWN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConsumeEnumFromMapWithInvalidEnum() throws Exception {
        Map<String, Object> map = ImmutableMap.<String, Object>builder().put("mode", "BOOM").build();

        ProtocolBufferUtils.consumeEnumFromMap(map, "mode", RoundingMode.class, roundingModeConsumer);
    }

    @Test
    public void testConsumeInstantFromMap() throws Exception {
        Date date = new Date();
        Map<String, Object> map = ImmutableMap.<String, Object>builder().put("date", date).build();

        ProtocolBufferUtils.consumeInstantFromMap(map, "date", instantConsumer);

        verify(instantConsumer).accept(date.toInstant());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConsumeInstantFromMapWithInvalidDateObject() throws Exception {
        Map<String, Object> map = ImmutableMap.<String, Object>builder().put("date", "date").build();

        ProtocolBufferUtils.consumeInstantFromMap(map, "date", instantConsumer);
    }
}
