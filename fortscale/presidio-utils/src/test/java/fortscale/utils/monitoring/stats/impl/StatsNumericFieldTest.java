package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsDoubleFlexMetric;
import fortscale.utils.monitoring.stats.StatsLongFlexMetric;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by gaashh on 4/3/16.
 */
@Ignore
public class StatsNumericFieldTest {

    // Long flex metric helper class
    class FlexLong implements StatsLongFlexMetric {
        Long value;
        FlexLong (Long value) { this.value = value; }
        public Long getValue() { return value; }
    }

    // Double flex metric helper class
    class FlexDouble implements StatsDoubleFlexMetric {
        Double value;
        FlexDouble (Double value) { this.value = value; }
        public Double getValue() { return value; }
    }

    class TestNumericFields extends StatsMetricsGroup {

        TestNumericFields(StatsService statsService, Class cls, StatsMetricsGroupAttributes attributes) {
            super(statsService, cls, attributes);
        }

        long longVar;
        Long longObjectVar;
        Long nullLong;

        int     intVar;
        Integer intObjectVar;
        Integer nullInt;

        double doubleVar;
        Double doubleObjectVar;
        Double nullDouble;

        float floatVar;
        Float floatObjectVar;
        Float nullFloat;

        AtomicLong atomicLongVar;
        AtomicLong nullAtomicLongVar;

        AtomicInteger atomicIntegerVar;
        AtomicInteger nullAtomicIntegerVar;

        FlexLong flexLong;
        FlexLong nullValueFlexLong;
        FlexLong nullFlexLong;
        StatsLongFlexMetric flexLongInterface;

        FlexDouble flexDouble;
        FlexDouble nullValueFlexDouble;
        FlexDouble nullFlexDouble;
        StatsDoubleFlexMetric flexDoubleInterface;

    }


    @Test
    public void StatsNumericFieldTest1() throws Exception {

        StatsService statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();

        StatsMetricsGroupAttributes groupAttributes = new StatsMetricsGroupAttributes();

        TestNumericFields numericFields = new TestNumericFields(statsService, StatsNumericFieldTest.class, groupAttributes);

        numericFields.longVar         = 10;
        numericFields.longObjectVar   = 20L;
        numericFields.nullLong        = null;

        numericFields.intVar          = 30;
        numericFields.intObjectVar    = 40;
        numericFields.nullInt         = null;

        numericFields.doubleVar       = 50.7;
        numericFields.doubleObjectVar = 60.8;
        numericFields.nullDouble      = null;

        numericFields.floatVar        = 70.77f;
        numericFields.floatObjectVar  = 80.88f;
        numericFields.nullFloat       = null;

        numericFields.atomicLongVar     = new AtomicLong(2000);
        numericFields.nullAtomicLongVar = null;

        numericFields.atomicIntegerVar     = new AtomicInteger(2100);
        numericFields.nullAtomicIntegerVar = null;

        numericFields.flexLong             = new FlexLong(90L);
        numericFields.nullValueFlexLong    = new FlexLong(null);
        numericFields.nullFlexLong         = null;
        numericFields.flexLongInterface    = new FlexLong(93L);


        numericFields.flexDouble           = new FlexDouble(100.3);
        numericFields.nullValueFlexDouble  = new FlexDouble(null);
        numericFields.nullFlexDouble       = null;
        numericFields.flexDoubleInterface  = new FlexDouble(103.3);

        final double epsilon = 0.00001;
        Field field;
        StatsNumericField statsNumericField;


        // long
        field = numericFields.getClass().getDeclaredField("longVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(numericFields.longVar, statsNumericField.getAsLong().longValue());
        assertEquals(numericFields.longVar, statsNumericField.getAsDouble(), epsilon);

        // Long
        field = numericFields.getClass().getDeclaredField("longObjectVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(numericFields.longObjectVar, statsNumericField.getAsLong());
        assertEquals(numericFields.longObjectVar, statsNumericField.getAsDouble(), epsilon);

        // null Long
        field = numericFields.getClass().getDeclaredField("nullLong");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsLong());
        assertNull(statsNumericField.getAsDouble());

        // int
        field = numericFields.getClass().getDeclaredField("intVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(new Long(numericFields.intVar), statsNumericField.getAsLong());
        assertEquals(numericFields.intVar, statsNumericField.getAsDouble(), epsilon);

        // Integer
        field = numericFields.getClass().getDeclaredField("intObjectVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(new Long(numericFields.intObjectVar), statsNumericField.getAsLong());
        assertEquals(numericFields.intObjectVar, statsNumericField.getAsDouble(), epsilon);


        // null Integer
        field = numericFields.getClass().getDeclaredField("nullInt");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsLong());
        assertNull(statsNumericField.getAsDouble());


        // double
        field = numericFields.getClass().getDeclaredField("doubleVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(new Long(Math.round(numericFields.doubleVar)), statsNumericField.getAsLong() );
        assertEquals(numericFields.doubleVar, statsNumericField.getAsDouble(), epsilon);

        // Double
        field = numericFields.getClass().getDeclaredField("doubleObjectVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(new Long(Math.round(numericFields.doubleObjectVar)), statsNumericField.getAsLong() );
        assertEquals(numericFields.doubleObjectVar, statsNumericField.getAsDouble(), epsilon);

        // null Double
        field = numericFields.getClass().getDeclaredField("nullDouble");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsLong());
        assertNull(statsNumericField.getAsDouble());

        // float
        field = numericFields.getClass().getDeclaredField("floatVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(new Long(Math.round(numericFields.floatVar)), statsNumericField.getAsLong());
        assertEquals(numericFields.floatVar, statsNumericField.getAsDouble(), epsilon);

        // Float
        field = numericFields.getClass().getDeclaredField("floatObjectVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(new Long(Math.round(numericFields.floatObjectVar)), statsNumericField.getAsLong());
        assertEquals(numericFields.floatObjectVar, statsNumericField.getAsDouble(), epsilon);

        // null Float
        field = numericFields.getClass().getDeclaredField("nullFloat");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsLong());
        assertNull(statsNumericField.getAsDouble());

        // AtomicLong
        field = numericFields.getClass().getDeclaredField("atomicLongVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(numericFields.atomicLongVar.longValue(), statsNumericField.getAsLong().longValue());
        assertEquals(numericFields.atomicLongVar.doubleValue(), statsNumericField.getAsDouble(), epsilon);

        // null AtomicLong
        field = numericFields.getClass().getDeclaredField("nullAtomicLongVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsLong());
        assertNull(statsNumericField.getAsDouble());

        // AtomicInteger
        field = numericFields.getClass().getDeclaredField("atomicIntegerVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(numericFields.atomicIntegerVar.longValue(), statsNumericField.getAsLong().longValue());
        assertEquals(numericFields.atomicIntegerVar.doubleValue(), statsNumericField.getAsDouble(), epsilon);

        // null AtomicInteger
        field = numericFields.getClass().getDeclaredField("nullAtomicIntegerVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsLong());
        assertNull(statsNumericField.getAsDouble());

        // flex Long
        field = numericFields.getClass().getDeclaredField("flexLong");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(numericFields.flexLong.getValue(), statsNumericField.getAsLong());
        assertEquals(numericFields.flexLong.getValue(), statsNumericField.getAsDouble(), epsilon);

        // null value flex Long
        field = numericFields.getClass().getDeclaredField("nullValueFlexLong");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsLong());
        assertNull(statsNumericField.getAsDouble());

        // null flex Long
        field = numericFields.getClass().getDeclaredField("nullFlexLong");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsLong());
        assertNull(statsNumericField.getAsDouble());

        // flex Long interface
        field = numericFields.getClass().getDeclaredField("flexLongInterface");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(numericFields.flexLongInterface.getValue(), statsNumericField.getAsLong());
        assertEquals(numericFields.flexLongInterface.getValue(), statsNumericField.getAsDouble(), epsilon);

        // flex Double
        field = numericFields.getClass().getDeclaredField("flexDouble");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(numericFields.flexDouble.getValue(), statsNumericField.getAsDouble());
        assertEquals(numericFields.flexDouble.getValue(), statsNumericField.getAsDouble(), epsilon);

        // null value flex Double
        field = numericFields.getClass().getDeclaredField("nullValueFlexDouble");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsDouble());
        assertNull(statsNumericField.getAsDouble());

        // null flex Double
        field = numericFields.getClass().getDeclaredField("nullFlexDouble");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertNull(statsNumericField.getAsDouble());
        assertNull(statsNumericField.getAsDouble());

        // flex Double interface
        field = numericFields.getClass().getDeclaredField("flexDoubleInterface");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals(numericFields.flexDoubleInterface.getValue(), statsNumericField.getAsDouble());
        assertEquals(numericFields.flexDoubleInterface.getValue(), statsNumericField.getAsDouble(), epsilon);

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
    public void StatsNumericFieldUnsupportedTypeTest() throws Exception {

        StatsService statsService = StatsTestingUtils.createStatsServiceImplWithTestingEngine();

        StatsMetricsGroupAttributes groupAttributes = new StatsMetricsGroupAttributes();

        UnsupportedTypeMetrics metrics = new UnsupportedTypeMetrics(statsService, StatsNumericFieldTest.class, groupAttributes);

        Field field = metrics.getClass().getDeclaredField("unsupportedTypeVar");

        // Should throw
        StatsNumericField.builder(field, metrics);

    }


}
