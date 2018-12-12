package presidio.monitoring.services;


import com.google.common.collect.Lists;
import fortscale.utils.time.TimeRange;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.repository.MetricRepositoryStaticIndexForTests;
import presidio.monitoring.elastic.services.MetricDocumentStaticIndexForTests;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.generator.MetricGeneratorService;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.sdk.api.services.model.Metric;
import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.spring.MetricPersistencyServiceTestConfig;

import java.time.Instant;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MetricPersistencyServiceTestConfig.class)
public class MetricPersistencyServiceTest {

    @Autowired
    private MetricGeneratorService metricGeneratorService;

    @Autowired
    private PresidioMetricPersistencyService presidioMetricPersistencyService;

    @Autowired
    public MetricRepository metricRepository;

    @Autowired
    public MetricRepositoryStaticIndexForTests metricRepositoryStaticIndexForTests;

    @After
    public void deleteTestData() {
        metricRepository.delete(metricRepository.findAll());
    }

    @Test
    public void testSaveMetrics() {
        Instant from = Instant.now().minusMillis(1000000);
        Instant to = Instant.now().minusMillis(1000);
        List<Number> values = new LinkedList();
        values.add(100);
        values.add(50);
        values.add(10);
        List<MetricDocument> metricList = metricGeneratorService.generateMetrics(100, from, to, values,
                new Metric.MetricBuilder().setMetricName("test")
                        .setMetricTags(new HashMap<>())
                        .build());
        presidioMetricPersistencyService.save(metricList);

        //verify first metric only:
        MetricDocument metric = metricRepository.findAll().iterator().next();
        Assert.assertFalse(metric.getValue().isEmpty());
        Assert.assertEquals("test", metric.getName());
        Assert.assertEquals(100, metricRepository.count());
    }

    @Test
    public void testSave() {

        MetricDocument metricDocument = getMetricDocument("metric-a");
        MetricDocument testMetric = presidioMetricPersistencyService.save(metricDocument);

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

        Iterable<MetricDocument> testMetrics = presidioMetricPersistencyService.save(metricList);

        assertThat(Lists.newArrayList(testMetrics).size(), is(3));

    }

    @Ignore
    @Test
    public void testMetricsByNamesAndTime() {
        Instant theDayBefore = Instant.parse("2017-12-02T10:00:00.00Z");
        Instant today = Instant.parse("2017-12-03T10:00:00.00Z");
        Instant theDayAfter = Instant.parse("2017-12-04T10:00:00.00Z");

        String name1 = "metric-a";
        String name2 = "metric-b";
        String name3 = "metric-c";

        //The day before
        presidioMetricPersistencyService.save(getMetricDocument(name1,Date.from(theDayBefore)));
        presidioMetricPersistencyService.save(getMetricDocument(name2,Date.from(theDayBefore)));
        presidioMetricPersistencyService.save(getMetricDocument(name3,Date.from(theDayBefore)));

        //The Today
        presidioMetricPersistencyService.save(getMetricDocument(name1,Date.from(today)));
        presidioMetricPersistencyService.save(getMetricDocument(name2,Date.from(today)));
        presidioMetricPersistencyService.save(getMetricDocument(name3,Date.from(today)));

        //The day after
        presidioMetricPersistencyService.save(getMetricDocument(name1,Date.from(theDayAfter)));
        presidioMetricPersistencyService.save(getMetricDocument(name2,Date.from(theDayAfter)));
        presidioMetricPersistencyService.save(getMetricDocument(name3,Date.from(theDayAfter)));

        TimeRange timeRange = new TimeRange(Instant.parse("2017-12-03T00:00:00.00Z"),Instant.parse("2017-12-04T00:00:00.00Z"));

        List<MetricDocument> results = presidioMetricPersistencyService.getMetricsByNamesAndTime(Arrays.asList(name1,name2),timeRange, null);
        Assert.assertEquals(4,results.size());

        boolean name1Found = results.get(0).getName().equals(name1) || results.get(1).getName().equals(name1);
        boolean name2Found = results.get(0).getName().equals(name2) || results.get(1).getName().equals(name2);
        Assert.assertTrue(results.stream().filter(x->x.getName().equals(name1)).count()>0);
        Assert.assertTrue(results.stream().filter(x->x.getName().equals(name2)).count()>0);
    }

    @Ignore
    @Test
    public void testMetricsByNamesAndTime_metricFromTwoDifferentIndexes() {
        Instant today = Instant.parse("2017-12-03T10:00:00.00Z");
        Instant theDayAfter = Instant.parse("2017-12-04T10:00:00.00Z");

        String name1 = "metric-a";
        String name2 = "metric-b";
        String name3 = "metric-c";

        //The Today
        metricRepositoryStaticIndexForTests.save(convertMetricDoc(getMetricDocument(name1,Date.from(today))));
        metricRepositoryStaticIndexForTests.save(convertMetricDoc(getMetricDocument(name2,Date.from(today))));
        metricRepositoryStaticIndexForTests.save(convertMetricDoc(getMetricDocument(name3,Date.from(today))));
        metricRepository.save(getMetricDocument(name2,Date.from(today)));

        //The day after
        metricRepositoryStaticIndexForTests.save(convertMetricDoc(getMetricDocument(name1,Date.from(theDayAfter))));
        metricRepositoryStaticIndexForTests.save(convertMetricDoc(getMetricDocument(name2,Date.from(theDayAfter))));
        metricRepositoryStaticIndexForTests.save(convertMetricDoc(getMetricDocument(name3,Date.from(theDayAfter))));

        TimeRange timeRange = new TimeRange(Instant.parse("2017-12-03T00:00:00.00Z"),Instant.parse("2017-12-04T00:00:00.00Z"));

        List<MetricDocument> results = presidioMetricPersistencyService.getMetricsByNamesAndTime(Arrays.asList(name1,name2),timeRange, null);
        Assert.assertEquals(3,results.size());

        boolean name1Found = results.get(0).getName().equals(name1) || results.get(1).getName().equals(name1);
        boolean name2Found = results.get(0).getName().equals(name2) || results.get(1).getName().equals(name2);
        Assert.assertTrue(name1Found && name2Found && results.get(0).getId()!=results.get(1).getId());
    }

    private MetricDocumentStaticIndexForTests convertMetricDoc(MetricDocument metricDocument) {
        MetricDocumentStaticIndexForTests docForTests = new MetricDocumentStaticIndexForTests();
        docForTests.setId(metricDocument.getId());
        docForTests.setLogicTime(metricDocument.getLogicTime());
        docForTests.setName(metricDocument.getName());
        docForTests.setTimestamp(metricDocument.getTimestamp());
        docForTests.setValue(metricDocument.getValue());

        return docForTests;
    }

    private MetricDocument getMetricDocument(String name, Date timestamp) {
        MetricDocument metricDocument = getMetricDocument(name);
        metricDocument.setLogicTime(timestamp);

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
