package presidio.output.domain.services;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.assertj.core.util.Lists;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static presidio.output.domain.records.alerts.AlertEnums.AlertSeverity;
import static presidio.output.domain.records.alerts.AlertEnums.AlertTimeframe;


@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig.class)
public class AlertPersistencyServiceTest {

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;
    List<String> classifications1;
    List<String> classifications2;
    List<String> classifications3;
    List<String> classifications4;
    List<String> classifications5;

    Sort sort;

    @Before
    public void before() {
        esTemplate.deleteIndex(Alert.class);
        esTemplate.createIndex(Alert.class);
        esTemplate.putMapping(Alert.class);
        esTemplate.refresh(Alert.class);
        classifications1 = new ArrayList<>(Arrays.asList("a", "b", "c"));
        classifications2 = new ArrayList<>(Arrays.asList("b"));
        classifications3 = new ArrayList<>(Arrays.asList("a"));
        classifications4 = new ArrayList<>(Arrays.asList("d"));
        classifications5 = null;
        sort = new Sort(new Sort.Order(Sort.Direction.ASC, Alert.SCORE));
    }

    @Test
    public void testSave() {
        Date startDate = new Date();
        Date endDate = new Date();
        Alert alert =
                new Alert("userId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null);
        Alert testAlert = alertPersistencyService.save(alert);

        assertNotNull(testAlert.getId());
        assertEquals(testAlert.getId(), alert.getId());
        assertEquals(testAlert.getUserName(), alert.getUserName());
        //assertEquals(testAlert.getStartDate(), alert.getStartDate());
    }

    @Test
    public void testSaveBulk() {
        Date startDate = new Date();
        Date endDate = new Date();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId2", classifications3, "user2", startDate, endDate, 10.0d, 7, AlertTimeframe.DAILY, AlertSeverity.CRITICAL, null));
        Iterable<Alert> testAlert = alertPersistencyService.save(alertList);

        assertThat(Lists.newArrayList(testAlert).size(), is(2));

    }

    @Test
    public void testFindOne() {
        Date startDate = new Date();
        Date endDate = new Date();
        Alert alert =
                new Alert("userId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null);

        alertPersistencyService.save(alert);

        Alert testAlert = alertPersistencyService.findOne(alert.getId());

        assertNotNull(testAlert.getId());
        assertEquals(testAlert.getId(), alert.getId());
        assertEquals(testAlert.getUserName(), alert.getUserName());
        // assertEquals(testAlert.getStartDate(), alert.getStartDate());

    }

    @Test
    public void testFindAll() {
        Date startDate = new Date();
        Date endDate = new Date();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
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
                new Alert("userId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        Page<Alert> byName1 = alertPersistencyService.findByUserName("user1", new PageRequest(0, 10));
        assertThat(byName1.getTotalElements(), is(2L));

        Page<Alert> byName2 = alertPersistencyService.findByUserName("user2", new PageRequest(0, 10));
        assertThat(byName2.getTotalElements(), is(0L));
    }


    @Test
    public void testDelete() {
        Date startDate = new Date();
        Date endDate = new Date();
        Alert alert =
                new Alert("userId", classifications1, "user1", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null);
        alertPersistencyService.save(alert);
        alertPersistencyService.delete(alert);
        Alert testAlert = alertPersistencyService.findOne(alert.getId());
        assertNull(testAlert);
    }

    @Test
    public void testFindByQuery() throws ParseException {
        Date startDate = Alert.ALERT_DATE_FORMAT.parse("20170916:100000.00Z");
        Date endDate = Alert.ALERT_DATE_FORMAT.parse("20170916:103000.00Z");

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId1", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId2", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId3", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId4", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId5", classifications1, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId6", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null));
        alertPersistencyService.save(alertList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByUserName(new ArrayList<>(Arrays.asList("normalized_username_ipusr3")))
                        .filterBySeverity(new ArrayList<>(Arrays.asList(AlertSeverity.HIGH.name())))
                        .filterByStartDate(startDate.getTime())
//                        .filterByEndDate(new Date(startDate.getTime() + 1))
                        .sortField(sort)
                        .aggregateByFields(aggregationFields)
                        .filterByClassification(classifications2)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(2L));
    }

    @Test
    public void testFindByQueryWitheClassification1() {

        Date startDate = new Date();
        Date endDate = new Date();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications3, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId", classifications4, "normalized_username_ipusr5@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
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
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId", classifications4, "normalized_username_ipusr5@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
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
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
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
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId", classifications4, "normalized_username_ipusr5@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }
        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Alert.SEVERITY);
        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(sort)
                        .aggregateByFields(aggregationFields)
                        .filterByClassification(classifications5)
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
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, tags));
        alertList.add(
                new Alert("userId", classifications2, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
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
                new Alert("userId1", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId2", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId3", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId4", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId5", classifications1, "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId6", classifications1, "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null));
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
                new Alert("userId1", Arrays.asList("a", "b", "c"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId2", Arrays.asList("a"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId3", Arrays.asList("a"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId4", Arrays.asList("b", "c"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId5", Arrays.asList("c"), "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
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

        assertEquals(buckets.size(), 3L);//3 buckets- a,b,c
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
                new Alert("userId1", Arrays.asList("a", "b", "c"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId2", Arrays.asList("a"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId3", Arrays.asList("a"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null));
        alertList.add(
                new Alert("userId4", Arrays.asList("b", "c"), "normalized_username_ipusr3@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.LOW, null));
        alertList.add(
                new Alert("userId5", Arrays.asList("c"), "normalized_username_ipusr4@somebigcompany.com", startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
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
                new Alert("userId1", null, "normalized_username_ipusr3@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId2", null, "normalized_username_ipusr3@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH, null));
        alertList.add(
                new Alert("userId3", null, "normalized_username_ipusr3@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null));
        alertList.add(
                new Alert("userId4", null, "normalized_username_ipusr3@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null));
        alertList.add(
                new Alert("userId5", null, "normalized_username_ipusr4@somebigcompany.com", startDate1, startDate1, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null));

        //alerts for second day (instant2):
        alertList.add(
                new Alert("userId1", null, "normalized_username_ipusr3@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null));
        alertList.add(
                new Alert("userId2", null, "normalized_username_ipusr3@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM, null));
        alertList.add(
                new Alert("userId3", null, "normalized_username_ipusr3@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null));
        alertList.add(
                new Alert("userId4", null, "normalized_username_ipusr3@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.CRITICAL, null));
        alertList.add(
                new Alert("userId5", null, "normalized_username_ipusr4@somebigcompany.com", startDate2, startDate2, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.LOW, null));
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
            assertEquals(5L, entry.getDocCount()); //bucket per day- 2 buckets
        }

    }

}