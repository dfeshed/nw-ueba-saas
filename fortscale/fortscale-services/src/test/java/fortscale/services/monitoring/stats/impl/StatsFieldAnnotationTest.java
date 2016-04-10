package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.services.monitoring.stats.annotations.StatsNumericMetricParams;

import fortscale.services.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.services.monitoring.stats.StatsService;
import fortscale.services.monitoring.stats.engine.StatsEngine;
import fortscale.services.monitoring.stats.engine.testing.StatsTestingEngine;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import fortscale.services.monitoring.stats.StatsMetricsGroup;

import java.lang.reflect.Field;

/**
 * Created by gaashh on 4/4/16.
 */

public class StatsFieldAnnotationTest {

    @StatsMetricsGroupParams(name="MetricName")
    class TestAnnotationFields extends StatsMetricsGroup {

        TestAnnotationFields(Class cls, StatsMetricsGroupAttributes attributes) {
            super(cls, attributes);
        }

        @StatsNumericMetricParams
        long    defaultMertic;

        long    nonMentric1;

        @StatsNumericMetricParams(name="non-default-metric", factor = 10.0)
        Float    nonDefaultMetric;

        long    nonMentric2;

        @StatsNumericMetricParams(factor = 100.0)
        @StatsNumericMetricParams(name = "three-B", factor = 200.0)
        @StatsNumericMetricParams(name = "three-C", factor = 300.0)
        int     threeMetrics;
    }


    @Test
    public void StatsAnnotationTest1() throws Exception {

        StatsService statsService = new StatsServiceImpl();
        StatsEngine  statsEngine  = new StatsTestingEngine();
        statsService.registerStatsEngine(statsEngine);

        StatsMetricsGroupAttributes groupAttributes = new StatsMetricsGroupAttributes();
        groupAttributes.setStatsService(statsService);
        groupAttributes.setGroupName("attr-group");

        TestAnnotationFields annotationFields = new TestAnnotationFields(TestAnnotationFields.class, groupAttributes);

        annotationFields.defaultMertic    = 10;
        annotationFields.nonDefaultMetric = 20.7f;

        annotationFields.manualUpdate();

    }
}
