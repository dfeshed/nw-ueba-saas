package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsDoubleFlexMetric;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.StatsStringFlexMetric;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;


/**
 * Created by gaashh on 5/29/16.
 */

public class StatsStringFieldTest {

    // String flex metric helper class
    class FlexString implements StatsStringFlexMetric {
        String value;
        FlexString (String value) { this.value = value; }
        public String getValue() { return value; }
    }

    class TestStringFields extends StatsMetricsGroup {

        TestStringFields(StatsService statsService, Class cls, StatsMetricsGroupAttributes attributes) {
            super(statsService, cls, attributes);
        }

        String stringVar;
        String nullString;

        FlexString flexString;
        FlexString nullValueFlexString;
        FlexString nullFlexString;

    }


    @Test
    public void StatsStringFieldTest1() throws Exception {

        StatsService statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();

        StatsMetricsGroupAttributes groupAttributes = new StatsMetricsGroupAttributes();

        TestStringFields stringFields = new TestStringFields(statsService, StatsStringFieldTest.class, groupAttributes);

        stringFields.stringVar            = "String--var";
        stringFields.nullString           = null;

        stringFields.flexString           = new FlexString("Flex--string");
        stringFields.nullValueFlexString  = new FlexString(null);
        stringFields.nullFlexString       = null;

        Field field;
        StatsStringField statsStringField;


        // String
        field = stringFields.getClass().getDeclaredField("stringVar");
        statsStringField = StatsStringField.builder(field, stringFields);
        assertEquals(stringFields.stringVar, statsStringField.getAsString());

        // null String
        field = stringFields.getClass().getDeclaredField("nullString");
        statsStringField = StatsStringField.builder(field, stringFields);
        assertNull(statsStringField.getAsString());

        // flex String
        field = stringFields.getClass().getDeclaredField("flexString");
        statsStringField = StatsStringField.builder(field, stringFields);
        assertEquals(stringFields.flexString.getValue(), statsStringField.getAsString());

        // null value flex String
        field = stringFields.getClass().getDeclaredField("nullValueFlexString");
        statsStringField = StatsStringField.builder(field, stringFields);
        assertNull(statsStringField.getAsString());

        // null flex String
        field = stringFields.getClass().getDeclaredField("nullFlexString");
        statsStringField = StatsStringField.builder(field, stringFields);
        assertNull(statsStringField.getAsString());

    }

    class UnsupportedType {
    }

    class UnsupportedTypeMetrics extends StatsMetricsGroup {
        UnsupportedTypeMetrics(StatsService statsService, Class cls, StatsMetricsGroupAttributes attributes) {
            super(statsService, cls, attributes);
        }

        UnsupportedType unsupportedTypeVar;
    }

    @Test (expected = StatsMetricsExceptions.UnsupportedDataTypeException.class)
    public void StatsStringFieldUnsupportedTypeTest() throws Exception {

        StatsService statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();

        StatsMetricsGroupAttributes groupAttributes = new StatsMetricsGroupAttributes();

        UnsupportedTypeMetrics metrics = new UnsupportedTypeMetrics(statsService, StatsStringFieldTest.class, groupAttributes);

        Field field = metrics.getClass().getDeclaredField("unsupportedTypeVar");

        // Should throw
        StatsNumericField.builder(field, metrics);

    }


}

