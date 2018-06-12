package presidio.output.domain.services;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.time.TimeRange;
import org.assertj.core.util.Lists;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.junit.After;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.records.MetricDocument;

import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import presidio.output.domain.spring.TestConfig;


import javax.validation.constraints.AssertTrue;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig.class, TestConfig.class, ElasticsearchTestConfig.class})
public class MetricsPersistencyServiceTest {


    @Autowired
    PresidioMetricPersistencyService metricPersistencyService;

    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    public Client client;



    @After
    public void cleanTestdata() {

        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(MetricDocument.METRIC_INDEX_NAME)
                .get();
    }

    @Test
    public void testSave() {

        MetricDocument metricDocument = getMetricDocument("metric-a");
        MetricDocument testMetric = metricPersistencyService.save(metricDocument);

        assertNotNull(testMetric.getId());
        assertEquals(testMetric.getId(), metricDocument.getId());
        assertEquals(testMetric.getName(), metricDocument.getName());

    }



    @Test
    public void testSaveBulk() {

        List<MetricDocument> metricList = new ArrayList<>();
        metricList.add(getMetricDocument("metric-a"));
        metricList.add(getMetricDocument("metric-b"));
        metricList.add(getMetricDocument("metric-c"));

        Iterable<MetricDocument> testMetrics = metricPersistencyService.save(metricList);

        assertThat(Lists.newArrayList(testMetrics).size(), is(3));

    }


    @Test
    public void testMetricsByNamesAndTime() {
        Instant theDayBefore = Instant.parse("2017-12-02T10:00:00.00Z");
        Instant today = Instant.parse("2017-12-03T10:00:00.00Z");
        Instant theDayAfter = Instant.parse("2017-12-04T10:00:00.00Z");

        String name1 = "metric-a";
        String name2 = "metric-b";
        String name3 = "metric-c";

        //The day before
        metricPersistencyService.save(getMetricDocument(name1,Date.from(theDayBefore)));
        metricPersistencyService.save(getMetricDocument(name2,Date.from(theDayBefore)));
        metricPersistencyService.save(getMetricDocument(name3,Date.from(theDayBefore)));

        //The Today before
        metricPersistencyService.save(getMetricDocument(name1,Date.from(today))).getId();
        metricPersistencyService.save(getMetricDocument(name2,Date.from(today))).getId();
        metricPersistencyService.save(getMetricDocument(name3,Date.from(today)));

        //The day after
        metricPersistencyService.save(getMetricDocument(name1,Date.from(theDayAfter)));
        metricPersistencyService.save(getMetricDocument(name2,Date.from(theDayAfter)));
        metricPersistencyService.save(getMetricDocument(name3,Date.from(theDayAfter)));



        TimeRange timeRange = new TimeRange(Instant.parse("2017-12-03T00:00:00.00Z"),Instant.parse("2017-12-04T00:00:00.00Z"));

        List<MetricDocument> results = metricPersistencyService.getMetricsByNamesAndTime(Arrays.asList(name1,name2),timeRange);
        Assert.assertEquals(2,results.size());

        boolean name1Found = results.get(0).getName().equals(name1) || results.get(1).getName().equals(name1);
        boolean name2Found = results.get(0).getName().equals(name2) || results.get(1).getName().equals(name2);
        Assert.assertTrue(name1Found && name2Found && results.get(0).getId()!=results.get(1).getId());

    }

    @Test
    public void testMetricsByNames() {

        String name1 = "metric-a";
        String name2 = "metric-b";
        String name3 = "metric-c";

        //The day before
        metricPersistencyService.save(getMetricDocument(name1));
        metricPersistencyService.save(getMetricDocument(name2));
        metricPersistencyService.save(getMetricDocument(name3));



        List<MetricDocument> results = metricPersistencyService.getMetricsByNames(Arrays.asList(name1,name2));
        Assert.assertEquals(2,results.size());

        boolean name1Found = results.get(0).getName().equals(name1) || results.get(1).getName().equals(name1);
        boolean name2Found = results.get(0).getName().equals(name2) || results.get(1).getName().equals(name2);
        Assert.assertTrue(name1Found && name2Found && results.get(0).getName()!=results.get(1).getName());


    }


    private MetricDocument getMetricDocument(String name, Date timestamp) {
        MetricDocument metricDocument = getMetricDocument(name);
        metricDocument.setTimestamp(timestamp);

        return metricDocument;
    }

    private MetricDocument getMetricDocument(String metricName) {
        Map<MetricEnums.MetricValues, Number> value = new HashMap<>();
        value.put(MetricEnums.MetricValues.SUM,5);
        Date timestamp = new Date();
        Date logicTime = new Date();
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap<>();
        tags.put(MetricEnums.MetricTagKeysEnum.ALERT_CLASSIFICATION,"CLASS-1");

        return new MetricDocument(metricName, value, timestamp, tags, logicTime);
    }


}