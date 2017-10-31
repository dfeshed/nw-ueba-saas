package presidio.output.domain.services;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.assertj.core.util.Lists;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.AbstractElasticDocument;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static presidio.output.domain.records.alerts.AlertEnums.AlertSeverity;
import static presidio.output.domain.records.alerts.AlertEnums.AlertTimeframe;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig.class})
public class AlertPersistencyServiceTest extends EmbeddedElasticsearchTest {

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    @Autowired
    public Client client;

    List<String> classifications1 = new ArrayList<>(Arrays.asList("a", "b", "c"));
    List<String> classifications2 = new ArrayList<>(Arrays.asList("b"));
    List<String> classifications3 = new ArrayList<>(Arrays.asList("a"));
    List<String> classifications4 = new ArrayList<>(Arrays.asList("d"));

    Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, Alert.SCORE));

    @After
    public void cleanTestdata() {

        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + Alert.ALERT_TYPE)
                .get();
    }

    @Test
    public void testSave() {
        Date startDate = new Date();
        Date endDate = new Date();
        Alert alert =
                new Alert("userId", "smartId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        Alert testAlert = alertPersistencyService.save(alert);
        assertNotNull(testAlert.getId());
        assertEquals(testAlert.getId(), alert.getId());
        assertEquals(testAlert.getUserName(), alert.getUserName());
        assertEquals(testAlert.getStartDate(), alert.getStartDate());
    }

    @Test
    public void testSaveBulk() {
        Date startDate = new Date();
        Date endDate = new Date();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", "smartId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", classifications3, "user2", startDate, endDate, 10.0d, 7, AlertTimeframe.DAILY, AlertSeverity.CRITICAL, null, 5D));
        Iterable<Alert> testAlert = alertPersistencyService.save(alertList);

        assertThat(Lists.newArrayList(testAlert).size(), is(2));

    }

    @Test
    public void testFindOne() {
        Date startDate = new Date();
        Date endDate = new Date();
        Alert alert =
                new Alert("userId", "smartId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        Date createAtDate = alert.getCreatedDate();

        alertPersistencyService.save(alert);

        Alert testAlert = alertPersistencyService.findOne(alert.getId());
        Date updateAtDateFirstFind = testAlert.getUpdatedDate();
        Date createAtDateFirstFind = testAlert.getCreatedDate();
        testAlert.setUserName("smartId1");
        alertPersistencyService.save(testAlert);
        testAlert = alertPersistencyService.findOne(alert.getId());
        Date createAtDateSecondFind = testAlert.getCreatedDate();
        Date updateAtDateSecondFind = testAlert.getUpdatedDate();

        assertNotNull(testAlert.getId());
        assertEquals(createAtDate, createAtDateFirstFind);
        assertEquals(createAtDateSecondFind, createAtDateFirstFind);
        assertNotEquals(createAtDateSecondFind, updateAtDateSecondFind);
        assertNotEquals(updateAtDateFirstFind, updateAtDateSecondFind);

        assertEquals(testAlert.getId(), alert.getId());
        assertEquals(testAlert.getUserName(), "smartId1");
        testAlert.setIndicatorsNum(100);
        alertPersistencyService.save(testAlert);
        Alert testAlert2 = alertPersistencyService.findOne(alert.getId());
        assertEquals(testAlert2.getCreatedDate(), createAtDate);
        assertEquals(100, testAlert2.getIndicatorsNum());
    }

    @Test
    public void testFindAll() {
        Date startDate = new Date();
        Date endDate = new Date();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", "smartId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        Iterable<Alert> testAlert = alertPersistencyService.findAll();
        assertThat(Lists.newArrayList(testAlert).size(), is(2));


    }

    @Test
    public void testFindByUserName() {
        Date startDate = new Date();
        Date endDate = new Date();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", "smartId", classifications1, "user1@fortscale.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications1, "user2@fortscale.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        Page<Alert> byName1 = alertPersistencyService.findByUserName("user1@fortscale.com", new PageRequest(0, 10));
        assertThat(byName1.getTotalElements(), is(1L));
        assertEquals("userId", byName1.getContent().get(0).getUserId());
        assertEquals("smartId", byName1.getContent().get(0).getSmartId());

        Page<Alert> byName2 = alertPersistencyService.findByUserName("user2", new PageRequest(0, 10));
        assertThat(byName2.getTotalElements(), is(1L));
    }


    @Test
    public void testDelete() {
        Date startDate = new Date();
        Date endDate = new Date();
        Alert alert =
                new Alert("userId", "smartId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alertPersistencyService.save(alert);
        alertPersistencyService.delete(alert);
        Alert testAlert = alertPersistencyService.findOne(alert.getId());
        assertNull(testAlert);
    }

    @Test
    public void testFindByQuery() {
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 1000 * 60);

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", new Date(startDate.getTime() - 1), new Date(endDate.getTime() + 5), 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, new Date(endDate.getTime() + 5), 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId3", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", new Date(startDate.getTime() + 1), new Date(endDate.getTime() + 5), 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId4", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", new Date(startDate.getTime() + 2), new Date(endDate.getTime() + 5), 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId6", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, new Date(endDate.getTime() + 5), 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null, 5D));
        alertList.add(
                new Alert("userId5", "smartId", classifications1, "normalized_username_ipusr4@somebigcompany.com", startDate, new Date(endDate.getTime() + 5), 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertPersistencyService.save(alertList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByUserName(new ArrayList<>(Arrays.asList("normalized_username_ipusr3@somebigcompany.com")))
                        .filterBySeverity(new ArrayList<>(Arrays.asList(AlertSeverity.HIGH.name())))
                        .filterByStartDate(startDate.getTime())
                        .filterByEndDate(startDate.getTime() + 1)
                        .filterByClassification(classifications2)
                        .sortField(sort)
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(2L));
    }

    @Test
    public void testFindWithMinScoreByQuery() {
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 1000 * 60);

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", new Date(startDate.getTime() - 1), new Date(endDate.getTime() + 5), 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, new Date(endDate.getTime() + 5), 50.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertPersistencyService.save(alertList);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByMinScore(90)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(1L));
    }

    @Test
    public void testFindWithMaxScoreByQuery() {
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 1000 * 60);

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", new Date(startDate.getTime() - 1), new Date(endDate.getTime() + 5), 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, new Date(endDate.getTime() + 5), 50.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertPersistencyService.save(alertList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByMaxScore(60)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(1L));
    }

    @Test
    public void testFindWithMaxScoreMinScoreByQuery() {
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 1000 * 60);

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", new Date(startDate.getTime() - 1), new Date(endDate.getTime() + 5), 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, new Date(endDate.getTime() + 5), 50.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertPersistencyService.save(alertList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByMaxScore(90)
                        .filterByMinScore(50)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(1L));
    }


    @Test
    public void testFindByQueryWitheClassification1() {

        Date startDate = new Date();
        Date endDate = new Date();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", "smartId", classifications3, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications4, "normalized_username_ipusr5@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(sort)
                        .aggregateByFields(aggregationFields)
                        .filterByClassification(classifications1)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(2L));
    }

    @Test
    public void testFindByQueryWitheClassification2() {

        Date startDate = new Date();
        Date endDate = new Date();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications4, "normalized_username_ipusr5@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(sort)
                        .aggregateByFields(aggregationFields)
                        .filterByClassification(classifications3)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(1L));
    }

    @Test
    public void testFindByQueryWitheClassification3() {

        Date startDate = new Date();
        Date endDate = new Date();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(sort)
                        .aggregateByFields(aggregationFields)
                        .filterByClassification(classifications4)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(0L));
    }


    @Test
    public void testFindByQueryWitheClassificationEmptyFilter() {

        Date startDate = new Date();
        Date endDate = new Date();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications4, "normalized_username_ipusr5@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }
        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);
        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(sort)
                        .aggregateByFields(aggregationFields)
                        .filterByClassification(null)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(3L));
    }


    @Test
    public void testFindByIUserAdmin_true() {

        Date startDate = new Date();
        Date endDate = new Date();
        List<String> tags = new ArrayList<>();
        tags.add("ADMIN");

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, tags, 5D));
        alertList.add(
                new Alert("userId", "smartId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByTags(tags)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(1L));
    }

    @Test
    public void testFindByQueryWithSeverityAggregation() {

        Date startDate = new Date();
        Date endDate = new Date();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId3", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId4", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId5", "smartId", classifications1, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId6", "smartId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null, 5D));
        alertPersistencyService.save(alertList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);
        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Alert>) testAlert).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get("severity");
        List<Terms.Bucket> buckets = severityAgg.getBuckets();

        assertEquals(buckets.size(), 2L); //two buckets- HIGH and MEDIUM
        assertEquals(severityAgg.getBucketByKey("HIGH").getDocCount(), 5L);
        assertEquals(severityAgg.getBucketByKey("MEDIUM").getDocCount(), 1L);
    }


    @Test
    public void testFindByQueryWithClassificationsAggregation() {

        Date startDate = new Date();
        Date endDate = new Date();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", "smartId", Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", Arrays.asList("a"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId3", "smartId", Arrays.asList("a"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId4", "smartId", Arrays.asList("b", "c"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId5", "smartId", Arrays.asList("c"), "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertPersistencyService.save(alertList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.CLASSIFICATIONS);
        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Alert>) testAlert).getAggregations().asMap();
        StringTerms classificationsAgg = (StringTerms) stringAggregationMap.get(Alert.CLASSIFICATIONS);
        List<Terms.Bucket> buckets = classificationsAgg.getBuckets();

        assertEquals(buckets.size(), 11L);//11 buckets
        assertEquals(classificationsAgg.getBucketByKey("a").getDocCount(), 3L);
        assertEquals(classificationsAgg.getBucketByKey("b").getDocCount(), 2L);
        assertEquals(classificationsAgg.getBucketByKey("c").getDocCount(), 3L);
    }

    @Test
    public void testFindByQueryWithClassificationsAndSeverityAggregation() {

        Date startDate = new Date();
        Date endDate = new Date();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", "smartId", Arrays.asList("a", "b", "c"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", Arrays.asList("a"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId3", "smartId", Arrays.asList("a"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null, 5D));
        alertList.add(
                new Alert("userId4", "smartId", Arrays.asList("b", "c"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.LOW, null, 5D));
        alertList.add(
                new Alert("userId5", "smartId", Arrays.asList("c"), "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertPersistencyService.save(alertList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.CLASSIFICATIONS);
        aggregationFields.add(Alert.SEVERITY);
        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Alert>) testAlert).getAggregations().asMap();
        StringTerms classificationsAgg = (StringTerms) stringAggregationMap.get(Alert.CLASSIFICATIONS);
        List<Terms.Bucket> buckets = classificationsAgg.getBuckets();

        assertEquals(buckets.size(), 3L);//3 buckets- a,b,c
        assertEquals(classificationsAgg.getBucketByKey("a").getDocCount(), 3L);
        assertEquals(classificationsAgg.getBucketByKey("b").getDocCount(), 2L);
        assertEquals(classificationsAgg.getBucketByKey("c").getDocCount(), 3L);

        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(Alert.SEVERITY);
        buckets = severityAgg.getBuckets();

        assertEquals(buckets.size(), 3L);//3 buckets- a,b,c
        assertEquals(severityAgg.getBucketByKey(AlertSeverity.LOW.name()).getDocCount(), 1L);
        assertEquals(severityAgg.getBucketByKey(AlertSeverity.HIGH.name()).getDocCount(), 3L);
        assertEquals(severityAgg.getBucketByKey(AlertSeverity.CRITICAL.name()).getDocCount(), 1L);
    }

    @Test
    public void testFindByQueryWithSeverityAggregationPerDay() {
        Instant instant1 = LocalDate.parse("2016-04-17").atTime(LocalTime.parse("00:00:10")).toInstant(ZoneOffset.UTC);
        Instant instant2 = LocalDate.parse("2016-04-18").atTime(LocalTime.parse("00:00:10")).toInstant(ZoneOffset.UTC);
        Date startDate1 = new Date(instant1.toEpochMilli());
        Date startDate2 = new Date(instant2.toEpochMilli());

        List<Alert> alertList = new ArrayList<>();
        //alerts for first day (instant1):
        alertList.add(
                new Alert("userId1", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D));
        alertList.add(
                new Alert("userId3", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null, 5D));
        alertList.add(
                new Alert("userId4", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null, 5D));
        alertList.add(
                new Alert("userId5", "smartId", null, "normalized_username_ipusr4@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null, 5D));

        //alerts for second day (instant2):
        alertList.add(
                new Alert("userId1", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null, 5D));
        alertList.add(
                new Alert("userId2", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null, 5D));
        alertList.add(
                new Alert("userId3", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null, 5D));
        alertList.add(
                new Alert("userId4", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null, 5D));
        alertList.add(
                new Alert("userId5", "smartId", null, "normalized_username_ipusr4@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.LOW, null, 5D));
        alertPersistencyService.save(alertList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.AGGR_SEVERITY_PER_DAY);
        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Alert>) testAlert).getAggregations().asMap();
        Histogram severityPerDayAggr = (Histogram) stringAggregationMap.get(Alert.AGGR_SEVERITY_PER_DAY);
        List<? extends Histogram.Bucket> buckets = severityPerDayAggr.getBuckets();

        assertEquals(2, severityPerDayAggr.getBuckets().size()); //bucket per day- 2 buckets
        for (Histogram.Bucket entry : buckets) {
            StringTerms severityAggregation = (StringTerms) entry.getAggregations().asMap().get(Alert.SEVERITY);
            if (entry.getKeyAsString().startsWith("2016-04-17")) {
                assertEquals(3L, severityAggregation.getBuckets().size());
                assertEquals(1L, severityAggregation.getBucketByKey(AlertSeverity.MEDIUM.name()).getDocCount());
                assertEquals(2L, severityAggregation.getBucketByKey(AlertSeverity.CRITICAL.name()).getDocCount());
                assertEquals(2L, severityAggregation.getBucketByKey(AlertSeverity.HIGH.name()).getDocCount());
            }
            if (entry.getKeyAsString().startsWith("2016-04-18")) {
                assertEquals(3L, severityAggregation.getBuckets().size());
                assertEquals(2L, severityAggregation.getBucketByKey(AlertSeverity.MEDIUM.name()).getDocCount());
                assertEquals(2L, severityAggregation.getBucketByKey(AlertSeverity.CRITICAL.name()).getDocCount());
                assertEquals(1L, severityAggregation.getBucketByKey(AlertSeverity.LOW.name()).getDocCount());
            }
        }
    }

    @Test
    public void testFindByQueryWithIndicatorNamesAggregation() {

        Date startDate = new Date();
        Date endDate = new Date();
        List<String> indicatorNames1 = Arrays.asList("a");
        List<String> indicatorNames2 = Arrays.asList("a", "b");
        List<String> indicatorNames3 = Arrays.asList("a", "b", "c");
        Alert alert1 = new Alert("userId1", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert1.setIndicatorsNames(indicatorNames1);
        Alert alert2 = new Alert("userId2", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert2.setIndicatorsNames(indicatorNames2);
        Alert alert3 = new Alert("userId3", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert3.setIndicatorsNames(indicatorNames3);
        List<Alert> alertList = Arrays.asList(alert1, alert2, alert3);
        alertPersistencyService.save(alertList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.INDICATOR_NAMES);
        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Alert>) testAlert).getAggregations().asMap();
        StringTerms indicatorsAgg = (StringTerms) stringAggregationMap.get(Alert.INDICATOR_NAMES);
        List<Terms.Bucket> buckets = indicatorsAgg.getBuckets();

        assertEquals(buckets.size(), 3L);//3 buckets- a,b,c
        assertEquals(indicatorsAgg.getBucketByKey("a").getDocCount(), 3L);
        assertEquals(indicatorsAgg.getBucketByKey("b").getDocCount(), 2L);
        assertEquals(indicatorsAgg.getBucketByKey("c").getDocCount(), 1L);
    }

    @Test
    public void testFindByQueryWithIndicatorNamesFilter() {

        Date startDate = new Date();
        Date endDate = new Date();
        List<String> indicatorNames1 = Arrays.asList("a");
        List<String> indicatorNames2 = Arrays.asList("a", "b");
        List<String> indicatorNames3 = Arrays.asList("a", "b", "c");
        Alert alert1 = new Alert("userId1", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert1.setIndicatorsNames(indicatorNames1);
        Alert alert2 = new Alert("userId2", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert2.setIndicatorsNames(indicatorNames2);
        Alert alert3 = new Alert("userId3", "smartId", null, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert3.setIndicatorsNames(indicatorNames3);
        List<Alert> alertList = Arrays.asList(alert1, alert2, alert3);
        alertPersistencyService.save(alertList);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByIndicatorNames(Arrays.asList("c", "b"))
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        Assert.assertEquals(2, testAlert.getTotalElements());
        Iterator<Alert> iterator = testAlert.iterator();
        Alert firstAlert = iterator.next();
        Assert.assertTrue(firstAlert.getIndicatorsNames().contains("c") || firstAlert.getIndicatorsNames().contains("b"));
        Alert secondAlert = iterator.next();
        Assert.assertTrue(secondAlert.getIndicatorsNames().contains("c") || secondAlert.getIndicatorsNames().contains("b"));
    }

    @Test
    public void testSortByUserName() {

        Date startDate = new Date();
        Date endDate = new Date();
        List<String> indicatorNames1 = Arrays.asList("a");
        String firstUserName = "Z_normalized_username_ipusr1@somebigcompany.com";
        String secondUserName = "W_normalized_username_ipusr2@somebigcompany.com";
        String thirdUserName = "X_normalized_username_ipusr3@somebigcompany.com";
        Alert alert1 = new Alert("userId1", "smartId", null, firstUserName, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert1.setIndicatorsNames(indicatorNames1);
        Alert alert2 = new Alert("userId2", "smartId", null, secondUserName, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert2.setIndicatorsNames(indicatorNames1);
        Alert alert3 = new Alert("userId3", "smartId", null, thirdUserName, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert3.setIndicatorsNames(indicatorNames1);
        List<Alert> alertList = Arrays.asList(alert1, alert2, alert3);
        alertPersistencyService.save(alertList);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(Alert.INDEXED_USER_NAME, true)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        Assert.assertEquals(3, testAlert.getTotalElements());
        Iterator<Alert> iterator = testAlert.iterator();
        Assert.assertEquals(secondUserName, iterator.next().getUserName());
        Assert.assertEquals(thirdUserName, iterator.next().getUserName());
        Assert.assertEquals(firstUserName, iterator.next().getUserName());
    }

    @Test
    public void testFindByUserNameCaseInsensitive() {

        Date startDate = new Date();
        Date endDate = new Date();
        List<String> indicatorNames1 = Arrays.asList("a");
        String firstUserName = "Z_normalized_username_ipusr1@somebigcompany.com";
        String secondUserName = "W_normalized_username_ipusr2@somebigcompany.com";
        String thirdUserName = "X_normalized_username_ipusr3@somebigcompany.com";
        Alert alert1 = new Alert("userId1", "smartId", null, firstUserName, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert1.setIndicatorsNames(indicatorNames1);
        Alert alert2 = new Alert("userId2", "smartId", null, secondUserName, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert2.setIndicatorsNames(indicatorNames1);
        Alert alert3 = new Alert("userId3", "smartId", null, thirdUserName, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert3.setIndicatorsNames(indicatorNames1);
        List<Alert> alertList = Arrays.asList(alert1, alert2, alert3);
        alertPersistencyService.save(alertList);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByUserName(Arrays.asList(firstUserName.toLowerCase()))
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        Assert.assertEquals(1, testAlert.getTotalElements());
        Iterator<Alert> iterator = testAlert.iterator();
        Assert.assertEquals(firstUserName, iterator.next().getUserName());
    }

    @Test
    public void testFindByUserNameContains() {

        Date startDate = new Date();
        Date endDate = new Date();
        List<String> indicatorNames1 = Arrays.asList("a");
        String firstUserName = "Z_normalized_username_ipusr1@somebigcompany.com";
        String secondUserName = "W_normalized_username_ipusr2@somebigcompany.com";
        String thirdUserName = "X_normalized_username_ipusr3@somebigcompany.com";
        Alert alert1 = new Alert("userId1", "smartId", null, firstUserName, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert1.setIndicatorsNames(indicatorNames1);
        Alert alert2 = new Alert("userId2", "smartId", null, secondUserName, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert2.setIndicatorsNames(indicatorNames1);
        Alert alert3 = new Alert("userId3", "smartId", null, thirdUserName, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null, 5D);
        alert3.setIndicatorsNames(indicatorNames1);
        List<Alert> alertList = Arrays.asList(alert1, alert2, alert3);
        alertPersistencyService.save(alertList);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByUserName(Arrays.asList("Z_normalized_username_ipusr1"))
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        Assert.assertEquals(1, testAlert.getTotalElements());
        Iterator<Alert> iterator = testAlert.iterator();
        Assert.assertEquals(firstUserName, iterator.next().getUserName());
    }
}