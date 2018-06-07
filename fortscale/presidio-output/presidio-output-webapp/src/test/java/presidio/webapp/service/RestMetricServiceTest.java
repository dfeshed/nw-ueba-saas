package presidio.webapp.service;

import fortscale.utils.time.TimeRange;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.webapp.model.Metric;
import presidio.webapp.spring.RestServiceTestConfig;

import javax.validation.ValidationException;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RestServiceTestConfig.class)
public class RestMetricServiceTest {

    @Autowired
    PresidioMetricPersistencyService presidioMetricPersistencyService;

    @Autowired
    RestMetricsService restMetricsService;


    @Test
    public void testGetMetrics() {


        MetricDocument metricDocument1 = getMetricDocument("metric-1", 10);
        MetricDocument metricDocument2 = getMetricDocument("metric-2", 5);

        List<MetricDocument> metricDocuments  =Arrays.asList(metricDocument1,metricDocument2);
        when(presidioMetricPersistencyService.getMetricsByNamesAndTime(Mockito.anyCollectionOf(String.class),Mockito.any(TimeRange.class))).thenReturn(metricDocuments);

        List<Metric> metrics = restMetricsService.getMetricsByNamesAndTime(Collections.EMPTY_LIST,new TimeRange());

        Assert.assertNotNull(metrics);
        Assert.assertEquals(2,metrics.size());
        assertMetric(metricDocument1,metrics.get(0));
        assertMetric(metricDocument2,metrics.get(1));
    }

    @Test
    public void testGetMetrics_Empty() {



        List<MetricDocument> metricDocuments  = Collections.emptyList();
        when(presidioMetricPersistencyService.getMetricsByNamesAndTime(Mockito.anyCollectionOf(String.class),Mockito.any(TimeRange.class))).thenReturn(metricDocuments);

        List<Metric> metrics = restMetricsService.getMetricsByNamesAndTime(Collections.EMPTY_LIST,new TimeRange());

        Assert.assertNotNull(metrics);
        Assert.assertEquals(0,metrics.size());
    }

    @Test(expected = ValidationException.class)
    public void testGetMetrics_NoDefaultValue() {


        MetricDocument metricDocument1 = getMetricDocument("metric-1", 10, MetricEnums.MetricValues.AGGREGATIONS);
        MetricDocument metricDocument2 = getMetricDocument("metric-2", 5, MetricEnums.MetricValues.SUM);


        List<MetricDocument> metricDocuments  =Arrays.asList(metricDocument1,metricDocument2);
        when(presidioMetricPersistencyService.getMetricsByNamesAndTime(Mockito.anyCollectionOf(String.class),Mockito.any(TimeRange.class))).thenReturn(metricDocuments);

        List<Metric> metrics = restMetricsService.getMetricsByNamesAndTime(Collections.EMPTY_LIST,new TimeRange());

    }

    private void assertMetric(MetricDocument metricDocument, Metric metric) {

        Assert.assertEquals(metricDocument.getName(), metric.getMetricName());
        Assert.assertEquals(metricDocument.getValue().get(MetricEnums.MetricValues.DEFAULT_METRIC_VALUE), metric.getMetricValue());
        Assert.assertEquals(metricDocument.getTimestamp(), Date.from(metric.getReportTime()));
        Assert.assertEquals(metricDocument.getLogicTime(), Date.from(metric.getLogicalTime()));

    }

    private MetricDocument getMetricDocument(String name,Number value ) {

        return getMetricDocument(name,value,MetricEnums.MetricValues.DEFAULT_METRIC_VALUE);
    }

    private MetricDocument getMetricDocument(String name,Number value, MetricEnums.MetricValues valueKey) {
        Map<MetricEnums.MetricValues, Number> values = new HashMap<>();
        values.put(valueKey,value);

        Map<MetricEnums.MetricTagKeysEnum, String> tags= new HashMap<>();

        return new MetricDocument(name,values, new Date(), tags, new Date());
    }


}
