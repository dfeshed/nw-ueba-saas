package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroupAttributes;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsService;

import java.lang.reflect.Field;


/**
 * Created by gaashh on 4/3/16.
 */

public class StatsNumericFieldTest {

    class TestNumericFields extends StatsMetricsGroup {

        TestNumericFields(Class cls, StatsMetricsGroupAttributes attributes) {
            super(cls, attributes);
        }

        long    longVar;
        Long    longObjectVar;

        int     intVar;
        Integer intObjectVar;

        double  doubleVar;
        Double  doubleObjectVar;

        float   floatVar;
        Float   floatObjectVar;

    }


    @Test
    public void StatsNumericFieldTest1() throws Exception {

        StatsService statsService = new StatsServiceImpl();
        StatsMetricsGroupAttributes groupAttributes = new StatsMetricsGroupAttributes();
        groupAttributes.setStatsService(statsService);

        TestNumericFields numericFields = new TestNumericFields(StatsNumericFieldTest.class, groupAttributes);

        numericFields.longVar         = 10;
        numericFields.longObjectVar   = 20L;
        numericFields.intVar          = 30;
        numericFields.intObjectVar    = 40;
        numericFields.doubleVar       = 50.7;
        numericFields.doubleObjectVar = 60.8;
        numericFields.floatVar        = 70.77f;
        numericFields.floatObjectVar  = 80.88f;


        final double epsilon = 0.00001;
        Field field;
        StatsNumericField statsNumericField;


        // long
        field = numericFields.getClass().getDeclaredField("longVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals( statsNumericField.getAsLong()   ,  numericFields.longVar );
        assertEquals( statsNumericField.getAsDouble() ,  new Long(numericFields.longVar).doubleValue(), epsilon );

        // Long
        field = numericFields.getClass().getDeclaredField("longObjectVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals( statsNumericField.getAsLong()   ,  numericFields.longObjectVar.longValue() );
        assertEquals( statsNumericField.getAsDouble() ,  numericFields.longObjectVar.doubleValue(), epsilon );


        // int
        field = numericFields.getClass().getDeclaredField("intVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals( statsNumericField.getAsLong()   ,  (long)numericFields.intVar );
        assertEquals( statsNumericField.getAsDouble() ,  new Long(numericFields.intVar).doubleValue(), epsilon );

        // Integer
        field = numericFields.getClass().getDeclaredField("intObjectVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals( statsNumericField.getAsLong()   ,  numericFields.intObjectVar.longValue() );
        assertEquals( statsNumericField.getAsDouble() ,  numericFields.intObjectVar.doubleValue(), epsilon );

        // double
        field = numericFields.getClass().getDeclaredField("doubleVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals( statsNumericField.getAsLong()   ,  Math.round(numericFields.doubleVar) );
        assertEquals( statsNumericField.getAsDouble() ,  numericFields.doubleVar, epsilon );

        // Double
        field = numericFields.getClass().getDeclaredField("doubleObjectVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals( statsNumericField.getAsLong()   ,  Math.round(numericFields.doubleObjectVar) );
        assertEquals( statsNumericField.getAsDouble() ,  numericFields.doubleObjectVar, epsilon );

        // float
        field = numericFields.getClass().getDeclaredField("floatVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals( statsNumericField.getAsLong()   ,  Math.round(numericFields.floatVar) );
        assertEquals( statsNumericField.getAsDouble() ,  new Float(numericFields.floatVar).doubleValue(), epsilon );

        // Float
        field = numericFields.getClass().getDeclaredField("floatObjectVar");
        statsNumericField = StatsNumericField.builder(field, numericFields);
        assertEquals( statsNumericField.getAsLong()   ,  Math.round(numericFields.floatObjectVar) );
        assertEquals( statsNumericField.getAsDouble() ,  numericFields.floatObjectVar, epsilon );

    }
}
