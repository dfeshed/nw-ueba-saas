package fortscale.web.demo.services;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.domain.dto.SeveritiesCountDTO;
import fortscale.web.demoservices.DemoBuilder;
import fortscale.web.demoservices.services.MockDemoAlertsServiceImpl;
import fortscale.web.demoservices.services.MockDemoUserServiceImpl;
import fortscale.web.demoservices.services.MockServiceUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

/**
 * Created by shays on 30/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MockDemoAlertServiceImplTest {

    private MockDemoAlertsServiceImpl mockDemoAlertService;
    private MockDemoUserServiceImpl mockDemoUserService;
    private DemoBuilder demoBuilder;

    private static final String SEVERITY_COLUMN_NAME = "Severity";
    private static final String FEEDBACK_COLUMN_NAME = "Feedback";

    @Before
    public void setUp(){
        User user1=new User();
        user1.setMockId("1");
        user1.setSearchField("Mark Avraham");
        user1.setUsername("Mark@Avraham");
        user1.setDisplayName("Mark Avraham");


        User user2=new User();
        user2.setMockId("2");
        user2.setSearchField("Beni Burger");
        user2.setUsername("Beni@Burger");
        user2.setDisplayName("Beni Burger");


        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        c.add(Calendar.DAY_OF_MONTH,-1);
        Date yesterday = c.getTime();

        Set<DataSourceAnomalyTypePair> loginsAnomalyDataSource = new HashSet<>();
        DataSourceAnomalyTypePair dataSourceAnomalyTypePair = new DataSourceAnomalyTypePair();
        dataSourceAnomalyTypePair.setAnomalyType("many_login");
        dataSourceAnomalyTypePair.setDataSource("login");

        loginsAnomalyDataSource.add(dataSourceAnomalyTypePair);


        Alert alert1 = new Alert();
        alert1.setEntityName("Mark@Avraham");
        alert1.setEntityId("1");
        alert1.setSeverity(Severity.High);
        alert1.setScore(60);
        alert1.setMockId("1");
        alert1.setStatus(AlertStatus.Open);
        alert1.setName("alert1");
        alert1.setTimeframe(AlertTimeframe.Daily);
        alert1.setStartDate(yesterday.getTime());
        alert1.setEndDate(now.getTime());
        alert1.setDataSourceAnomalyTypePair(loginsAnomalyDataSource);
        alert1.setFeedback(AlertFeedback.None);

        Alert alert2 = new Alert();
        alert2.setEntityName("Beni@Burger");
        alert2.setEntityId("2");
        alert2.setSeverity(Severity.High);
        alert2.setScore(60);
        alert2.setMockId("1");
        alert2.setStatus(AlertStatus.Open);
        alert2.setName("alert2");
        alert2.setTimeframe(AlertTimeframe.Daily);
        alert2.setStartDate(yesterday.getTime());
        alert2.setEndDate(now.getTime());
        alert2.setDataSourceAnomalyTypePair(loginsAnomalyDataSource);
        alert2.setFeedback(AlertFeedback.None);

        Alert alert3 = new Alert();
        alert3.setEntityName("Mark@Avraham");
        alert3.setEntityId("1");
        alert3.setSeverity(Severity.High);
        alert3.setScore(60);
        alert3.setMockId("3");
        alert3.setStatus(AlertStatus.Closed);
        alert3.setName("alert3");
        alert3.setTimeframe(AlertTimeframe.Daily);
        alert3.setStartDate(yesterday.getTime());
        alert3.setEndDate(now.getTime());
        alert3.setDataSourceAnomalyTypePair(loginsAnomalyDataSource);
        alert3.setFeedback(AlertFeedback.None);


        demoBuilder = new DemoBuilder(users,new LinkedList<>(Arrays.asList(alert1,alert2,alert3)),null);
        mockDemoUserService = new MockDemoUserServiceImpl(null,demoBuilder);
        mockDemoAlertService = new MockDemoAlertsServiceImpl(mockDemoUserService, demoBuilder);


    }

    @Test
    public void testFindAll(){

        Alerts alerts = mockDemoAlertService.findAll(new PageRequest(1,10),true);
        Assert.assertEquals(3,alerts.getAlerts().size());


    }

    @Test
    public void testFindByTimeRange(){

        DateRange dateRange = getDateRange(2);

        List<Alert> alerts = mockDemoAlertService.getAlertsByTimeRange(dateRange, Arrays.asList(Severity.High.name()));
        Assert.assertEquals(3,alerts.size());


    }

    public DateRange getDateRange(int days) {
        Calendar c = Calendar.getInstance();
        Date to = c.getTime();
        c.add(Calendar.DAY_OF_MONTH,days*-1);
        Date from = c.getTime();

        return new DateRange(from.getTime(),to.getTime());
    }

    @Test
    public void testFindByUserName(){

        List<Alert> alerts = mockDemoAlertService.getAlertsByUsername("Mark@Avraham").getAlerts();
        Assert.assertEquals(2,alerts.size());


    }


    @Test
    public void testGetOpenAlertsByUserName(){

        List<Alert> alerts = mockDemoAlertService.getOpenAlertsByUsername("Mark@Avraham");
        Assert.assertEquals(1,alerts.size());


    }

    @Test
    public void testAlertsCountByDayAndSeverity(){

        List<DailySeveiryConuntDTO> dailyAlertsCount = mockDemoAlertService.getAlertsCountByDayAndSeverity(getDateRange(2));
        Assert.assertEquals(3,dailyAlertsCount.size());
        DailySeveiryConuntDTO yesterdayData = dailyAlertsCount.get(1); // Alerts which start yesterday and finished today
        for (SeveritiesCountDTO severitiesCountDTO:yesterdayData.getSeverities()){
            if (severitiesCountDTO.getSeverity().equals(Severity.High)){
                Assert.assertEquals(3,severitiesCountDTO.getCount());
            } else {
                Assert.assertEquals(0,severitiesCountDTO.getCount());
            }
        }

    }


    @Test
    public void testGetAlertById(){

        Alert alert = mockDemoAlertService.getAlertById("1");
        Assert.assertEquals("1",alert.getId());



    }

    @Test
    public void testSaveAlertInRepository(){

        Alert alert = mockDemoAlertService.getAlertById("1");
        Assert.assertEquals("1",alert.getId());
        Assert.assertEquals(AlertFeedback.None,alert.getFeedback());

        alert.setFeedback(AlertFeedback.Approved);
        mockDemoAlertService.saveAlertInRepository(alert);
        Alert alertFromRepo = mockDemoAlertService.getAlertById("1");
        Assert.assertEquals("1",alert.getId());
        Assert.assertEquals(AlertFeedback.Approved,alert.getFeedback());

    }

    @Test
    public void testUpdateAlertStatus(){

        Alert alert = mockDemoAlertService.getAlertById("1");
        Assert.assertEquals("1",alert.getId());
        Assert.assertEquals(AlertFeedback.None,alert.getFeedback());
        Assert.assertEquals(AlertStatus.Open,alert.getStatus());

        mockDemoAlertService.updateAlertStatus(alert,AlertStatus.Closed,AlertFeedback.Approved,"analystName");

        Assert.assertEquals("1",alert.getId());
        Assert.assertEquals(AlertFeedback.Approved,alert.getFeedback());
        Assert.assertEquals(AlertStatus.Closed,alert.getStatus());

    }

    @Test
    public void testCount(){

        long count = mockDemoAlertService.count(null);
        Assert.assertEquals(3L,count);



    }

    @Test
    public void testGetDistinctAnomalyType(){
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        c.add(Calendar.DAY_OF_MONTH,-1);
        Date yesterday = c.getTime();

        Alert alert1 = new Alert();
        alert1.setEntityName("Mark@Avraham");
        alert1.setEntityId("1");
        alert1.setSeverity(Severity.High);
        alert1.setScore(60);
        alert1.setMockId("1");
        alert1.setStatus(AlertStatus.Open);
        alert1.setName("alert1");
        alert1.setTimeframe(AlertTimeframe.Daily);
        alert1.setStartDate(yesterday.getTime());
        alert1.setEndDate(now.getTime());

        Evidence indicator11 = new Evidence();
        indicator11.setDataEntitiesIds(Arrays.asList("login"));
        indicator11.setName("indicator_name");
        indicator11.setAnomalyType("anomalyType11");
        indicator11.setAnomalyTypeFieldName("anomalyType11FieldName");
        indicator11.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);

        Evidence indicator12 = new Evidence();
        indicator12.setDataEntitiesIds(Arrays.asList("login"));
        indicator12.setName("indicator_name");
        indicator12.setAnomalyType("anomalyType12");
        indicator12.setAnomalyTypeFieldName("anomalyType12FieldName");
        indicator12.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);
        Evidence indicator13 = new Evidence();
        indicator13.setDataEntitiesIds(Arrays.asList("login"));
        indicator13.setName("indicator_name");
        indicator13.setAnomalyType("anomalyType100");
        indicator13.setAnomalyTypeFieldName("anomalyType12FieldName");
        indicator13.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);


        alert1.setEvidenceSize(3);
        alert1.setEvidences(Arrays.asList(indicator11,indicator12,indicator13));


        Alert alert2 = new Alert();
        alert2.setEntityName("Beni@Burger");
        alert2.setEntityId("2");
        alert2.setSeverity(Severity.High);
        alert2.setScore(60);
        alert2.setMockId("1");
        alert2.setStatus(AlertStatus.Open);
        alert2.setName("alert2");
        alert2.setTimeframe(AlertTimeframe.Daily);
        alert2.setStartDate(yesterday.getTime());
        alert2.setEndDate(now.getTime());


        Evidence indicator21 = new Evidence();
        indicator21.setDataEntitiesIds(Arrays.asList("login"));
        indicator21.setName("indicator_name");
        indicator21.setAnomalyType("anomalyType21");
        indicator21.setAnomalyTypeFieldName("anomalyType11FieldName");
        indicator21.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);

        Evidence indicator22 = new Evidence();
        indicator22.setDataEntitiesIds(Arrays.asList("login"));
        indicator22.setName("indicator_name");
        indicator22.setAnomalyType("anomalyType22");
        indicator22.setAnomalyTypeFieldName("anomalyType12FieldName");
        indicator22.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);
        Evidence indicator23 = new Evidence();
        indicator23.setDataEntitiesIds(Arrays.asList("login"));
        indicator23.setName("indicator_name");
        indicator23.setAnomalyType("anomalyType100");
        indicator23.setAnomalyTypeFieldName("anomalyType12FieldName");
        indicator23.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);


        alert2.setEvidenceSize(3);
        alert2.setEvidences(Arrays.asList(indicator21,indicator22,indicator23));

        demoBuilder.getAlerts().clear();
        demoBuilder.getAlerts().add(alert1);
        demoBuilder.getAlerts().add(alert2);

        Map<String,Integer>  indicatorsTypesAndCount= mockDemoAlertService.getDistinctAnomalyType();
        Assert.assertEquals(5,indicatorsTypesAndCount.size());

        for (String test:indicatorsTypesAndCount.keySet()){

            Assert.assertTrue(test.startsWith("anomalyType"));

        }
    }

    @Test
    public void testGetCountAnomalyType(){
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        c.add(Calendar.DAY_OF_MONTH,-1);
        Date yesterday = c.getTime();

        Alert alert1 = new Alert();
        alert1.setEntityName("Mark@Avraham");
        alert1.setEntityId("1");
        alert1.setSeverity(Severity.High);
        alert1.setScore(60);
        alert1.setMockId("1");
        alert1.setStatus(AlertStatus.Open);
        alert1.setName("alert1");
        alert1.setTimeframe(AlertTimeframe.Daily);
        alert1.setStartDate(yesterday.getTime());
        alert1.setEndDate(now.getTime());

        Evidence indicator11 = new Evidence();
        indicator11.setDataEntitiesIds(Arrays.asList("login"));
        indicator11.setName("indicator_name");
        indicator11.setAnomalyType("anomalyType11");
        indicator11.setAnomalyTypeFieldName("anomalyType11FieldName");
        indicator11.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);

        Evidence indicator12 = new Evidence();
        indicator12.setDataEntitiesIds(Arrays.asList("login"));
        indicator12.setName("indicator_name");
        indicator12.setAnomalyType("anomalyType12");
        indicator12.setAnomalyTypeFieldName("anomalyType12FieldName");
        indicator12.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);
        Evidence indicator13 = new Evidence();
        indicator13.setDataEntitiesIds(Arrays.asList("login"));
        indicator13.setName("indicator_name");
        indicator13.setAnomalyType("anomalyType100");
        indicator13.setAnomalyTypeFieldName("anomalyType12FieldName");
        indicator13.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);


        alert1.setEvidenceSize(3);
        alert1.setEvidences(Arrays.asList(indicator11,indicator12,indicator13));


        Alert alert2 = new Alert();
        alert2.setEntityName("Beni@Burger");
        alert2.setEntityId("2");
        alert2.setSeverity(Severity.High);
        alert2.setScore(60);
        alert2.setMockId("1");
        alert2.setStatus(AlertStatus.Open);
        alert2.setName("alert2");
        alert2.setTimeframe(AlertTimeframe.Daily);
        alert2.setStartDate(yesterday.getTime());
        alert2.setEndDate(now.getTime());


        Evidence indicator21 = new Evidence();
        indicator21.setDataEntitiesIds(Arrays.asList("login"));
        indicator21.setName("indicator_name");
        indicator21.setAnomalyType("anomalyType21");
        indicator21.setAnomalyTypeFieldName("anomalyType11FieldName");
        indicator21.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);

        Evidence indicator22 = new Evidence();
        indicator22.setDataEntitiesIds(Arrays.asList("login"));
        indicator22.setName("indicator_name");
        indicator22.setAnomalyType("anomalyType22");
        indicator22.setAnomalyTypeFieldName("anomalyType12FieldName");
        indicator22.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);
        Evidence indicator23 = new Evidence();
        indicator23.setDataEntitiesIds(Arrays.asList("login"));
        indicator23.setName("indicator_name");
        indicator23.setAnomalyType("anomalyType100");
        indicator23.setAnomalyTypeFieldName("anomalyType12FieldName");
        indicator23.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);


        alert2.setEvidenceSize(3);
        alert2.setEvidences(Arrays.asList(indicator21,indicator22,indicator23));

        Alert alert3 = new Alert();
        alert3.setEntityName("Beni@Burger");
        alert3.setEntityId("2");
        alert3.setSeverity(Severity.High);
        alert3.setScore(60);
        alert3.setMockId("1");
        alert3.setStatus(AlertStatus.Open);
        alert3.setName("alert1");
        alert3.setTimeframe(AlertTimeframe.Daily);
        alert3.setStartDate(yesterday.getTime());
        alert3.setEndDate(now.getTime());

        demoBuilder.getAlerts().clear();
        demoBuilder.getAlerts().add(alert1);
        demoBuilder.getAlerts().add(alert2);
        demoBuilder.getAlerts().add(alert3);

        Map<String, Integer> alertsTypesCounted = mockDemoAlertService.getAlertsTypesCounted(false);
        Assert.assertEquals(2,alertsTypesCounted.size());

        Assert.assertEquals(2L, alertsTypesCounted.get("alert1").longValue());
        Assert.assertEquals(1L, alertsTypesCounted.get("alert2").longValue());
    }

    @Test
    public void testGroupCountBySeverity(){
        Alert alert1 = new Alert();
        alert1.setEntityName("Mark@Avraham");
        alert1.setEntityId("1");
        alert1.setSeverity(Severity.High);
        alert1.setScore(60);
        alert1.setMockId("1");
        alert1.setStatus(AlertStatus.Closed);
        alert1.setName("alert1");

        Alert alert2 = new Alert();
        alert2.setEntityName("Mark@Avraham");
        alert2.setEntityId("1");
        alert2.setSeverity(Severity.Low);
        alert2.setScore(60);
        alert2.setMockId("1");
        alert2.setStatus(AlertStatus.Open);
        alert2.setName("alert2");

        demoBuilder.getAlerts().add(alert1);
        demoBuilder.getAlerts().add(alert2);
        Map<String, Integer> severitiesCount = mockDemoAlertService.groupCount(SEVERITY_COLUMN_NAME.toLowerCase(),null,AlertStatus.Open.name(),null,null,null,
                null,null,null);

        Assert.assertEquals(2,severitiesCount.get(Severity.High.name()).longValue());
        Assert.assertEquals(1,severitiesCount.get(Severity.Low.name()).longValue());
    }

    @Test
    public void testGroupCountByFeedback(){
        Alert alert1 = new Alert();
        alert1.setEntityName("Mark@Avraham");
        alert1.setEntityId("1");
        alert1.setSeverity(Severity.High);
        alert1.setScore(60);
        alert1.setMockId("1");
        alert1.setStatus(AlertStatus.Closed);
        alert1.setName("alert1");
        alert1.setFeedback(AlertFeedback.Rejected);

        Alert alert2 = new Alert();
        alert2.setEntityName("Mark@Avraham");
        alert2.setEntityId("1");
        alert2.setSeverity(Severity.Low);
        alert2.setScore(60);
        alert2.setMockId("1");
        alert2.setFeedback(AlertFeedback.Approved);
        alert2.setStatus(AlertStatus.Open);
        alert2.setName("alert2");

        demoBuilder.getAlerts().add(alert1);
        demoBuilder.getAlerts().add(alert2);
        Map<String, Integer> severitiesCount = mockDemoAlertService.groupCount(FEEDBACK_COLUMN_NAME.toLowerCase(),null,AlertStatus.Open.name(),null,null,null,
                null,null,null);

        Assert.assertEquals(2,severitiesCount.get(AlertFeedback.None.name()).longValue());
        Assert.assertEquals(1,severitiesCount.get(AlertFeedback.Approved.name()).longValue());
    }

    @Test
    public void testFindAlertsByFilterEntityName(){
        Alerts alerts = mockDemoAlertService.findAlertsByFilters(new PageRequest(0,10),null,null,null,
                null,"Mark@Avraham",null,null,null,true,false);

        Assert.assertEquals(2,alerts.getAlerts().size());

    }

    @Test
    public void testFindAlertsByFilterSeverity(){

        Alert alert3 = new Alert();
        alert3.setEntityName("Mark@Avraham");
        alert3.setEntityId("1");
        alert3.setSeverity(Severity.Low);
        alert3.setScore(60);
        alert3.setMockId("40");
        alert3.setStatus(AlertStatus.Closed);
        alert3.setName("alert3");
        alert3.setTimeframe(AlertTimeframe.Daily);

        alert3.setFeedback(AlertFeedback.None);
        demoBuilder.getAlerts().add(alert3);

        Alerts alerts = mockDemoAlertService.findAlertsByFilters(new PageRequest(0,10),"High,Low",null,null,
                null,null,null,null,null,true,false);

        Assert.assertEquals(4,alerts.getAlerts().size());

    }


    @Test
    public void testFindAlertsByFilterStatus(){
        Alerts alerts = mockDemoAlertService.findAlertsByFilters(new PageRequest(0,10),null,"closed",null,
                null,null,null,null,null,true,false);

        Assert.assertEquals(1,alerts.getAlerts().size());

    }

    @Test
    public void testFindAlertsByFilterStatusNotExistEnum(){
        Alerts alerts = mockDemoAlertService.findAlertsByFilters(new PageRequest(0,10),null,"closed1",null,
                null,null,null,null,null,true,false);

        Assert.assertEquals(0,alerts.getAlerts().size());

    }

    @Test
    public void testFindAlertsEntityId(){
        Alerts alerts = mockDemoAlertService.findAlertsByFilters(new PageRequest(0,10),null,null,null,
                null,null,null,"2",null,true,false);

        Assert.assertEquals(1,alerts.getAlerts().size());

    }

    @Test
    public void testCountAlertsEntityId(){
        Long alertsCount = mockDemoAlertService.countAlertsByFilters(null,null,null,
                null,null,null,"2",null);

        Assert.assertEquals(1L,alertsCount.longValue());

    }


    @Test
    public void testFindAlertsPagingMoreThenOnePage(){
        List<Alert> originalAlerts  = new ArrayList<>();
        for (int i=0; i<30;i++){
            Alert a= new Alert();
            a.setMockId(i+"");
            if (i<20) {
                a.setFeedback(AlertFeedback.Approved);
            } else {
                a.setFeedback(AlertFeedback.None);
            }
            originalAlerts.add(a);
        }

        demoBuilder.getAlerts().clear();
        demoBuilder.getAlerts().addAll(originalAlerts);


        Alerts alerts = mockDemoAlertService.findAlertsByFilters(new PageRequest(2,5),null,null,AlertFeedback.Approved.name(),
                null,null,null,null,null,true,false);

        Assert.assertEquals(5,alerts.getAlerts().size()); //Should be total 20 but 5 in page
        Assert.assertEquals("10",alerts.getAlerts().get(0).getId()); //Should be total 20 but 5 in page
        Assert.assertEquals("11",alerts.getAlerts().get(1).getId()); //Should be total 20 but 5 in page
        Assert.assertEquals("12",alerts.getAlerts().get(2).getId()); //Should be total 20 but 5 in page
        Assert.assertEquals("13",alerts.getAlerts().get(3).getId()); //Should be total 20 but 5 in page
        Assert.assertEquals("14",alerts.getAlerts().get(4).getId()); //Should be total 20 but 5 in page


    }


    @Test
    public void testAlertSortingByStartDate(){
        Alert a1 = new Alert();
        a1.setStartDate(20);

        Alert a2 = new Alert();
        a2.setStartDate(30);

        Alert a3 = new Alert();
        a3.setStartDate(10);

        Alert a4 = new Alert();
        a4.setStartDate(40);
        Alert a5 = new Alert();
        a5.setStartDate(6);


        List<Alert> alerts = Arrays.asList(a1,a2,a3,a4,a5);
        Sort s = new Sort(new Sort.Order(Sort.Direction.ASC,Alert.startDateField));
        PageRequest pageRequest = new PageRequest(1,10,s);

        List<Alert> sorted = MockServiceUtils.sort(alerts,pageRequest, Alert.class);
        Assert.assertEquals(5,sorted.size());
        Assert.assertEquals(6,sorted.get(0).getStartDate());
        Assert.assertEquals(10,sorted.get(1).getStartDate());
        Assert.assertEquals(20,sorted.get(2).getStartDate());
        Assert.assertEquals(30,sorted.get(3).getStartDate());
        Assert.assertEquals(40,sorted.get(4).getStartDate());

    }

    @Test
    public void testAlertReverseSortingByStartDate(){
        Alert a1 = new Alert();
        a1.setStartDate(20);

        Alert a2 = new Alert();
        a2.setStartDate(30);

        Alert a3 = new Alert();
        a3.setStartDate(10);

        Alert a4 = new Alert();
        a4.setStartDate(40);
        Alert a5 = new Alert();
        a5.setStartDate(6);


        List<Alert> alerts = Arrays.asList(a1,a2,a3,a4,a5);
        Sort s = new Sort(new Sort.Order(Sort.Direction.DESC,Alert.startDateField));
        PageRequest pageRequest = new PageRequest(1,10,s);

        List<Alert> sorted = MockServiceUtils.sort(alerts,pageRequest, Alert.class);
        Assert.assertEquals(5,sorted.size());
        Assert.assertEquals(40,sorted.get(0).getStartDate());
        Assert.assertEquals(30,sorted.get(1).getStartDate());
        Assert.assertEquals(20,sorted.get(2).getStartDate());
        Assert.assertEquals(10,sorted.get(3).getStartDate());
        Assert.assertEquals(6,sorted.get(4).getStartDate());

    }

    @Test
    public void testAlertSortingByNumberOfIndicators(){
        Alert a1 = new Alert();
        a1.setEvidenceSize(10);

        Alert a2 = new Alert();
        a2.setEvidenceSize(30);

        Alert a3 = new Alert();
        a3.setEvidenceSize(20);

        Alert a4 = new Alert();
        a4.setEvidenceSize(40);

        Alert a5 = new Alert();
        a5.setEvidenceSize(6);


        List<Alert> alerts = Arrays.asList(a1,a2,a3,a4,a5);
        Sort s = new Sort(new Sort.Order(Sort.Direction.ASC,"evidenceSize"));
        PageRequest pageRequest = new PageRequest(1,10,s);

        List<Alert> sorted = MockServiceUtils.sort(alerts,pageRequest, Alert.class);
        Assert.assertEquals(5,sorted.size());
        Assert.assertEquals(6 ,sorted.get(0).getEvidenceSize().intValue());
        Assert.assertEquals(10,sorted.get(1).getEvidenceSize().intValue());
        Assert.assertEquals(20,sorted.get(2).getEvidenceSize().intValue());
        Assert.assertEquals(30,sorted.get(3).getEvidenceSize().intValue());
        Assert.assertEquals(40,sorted.get(4).getEvidenceSize().intValue());

    }


    @Test
    public void testAlertSortingByStartDateAndIndicator(){

        Alert a5 = new Alert();
        a5.setEvidenceSize(30);
        a5.setStartDate(10);
        a5.setMockId("5");

        Alert a6 = new Alert();
        a6.setEvidenceSize(30);
        a6.setStartDate(50);
        a6.setMockId("6");

        Alert a1 = new Alert();
        a1.setEvidenceSize(10);
        a1.setStartDate(10);
        a1.setMockId("1");

        Alert a3 = new Alert();
        a3.setEvidenceSize(20);
        a3.setStartDate(50);
        a3.setMockId("3");

        Alert a4 = new Alert();
        a4.setEvidenceSize(20);
        a4.setStartDate(10);
        a4.setMockId("4");

        Alert a2 = new Alert();
        a2.setEvidenceSize(10);
        a2.setStartDate(50);
        a2.setMockId("2");




        List<Alert> alerts = Arrays.asList(a5,a6,a1,a3,a4,a2);


        Sort s = new Sort(new Sort.Order(Sort.Direction.ASC,Alert.evidencesSizeField),
                new Sort.Order(Sort.Direction.ASC,Alert.startDateField));
        PageRequest pageRequest = new PageRequest(1,10,s);

        List<Alert> sorted = MockServiceUtils.sort(alerts,pageRequest, Alert.class);
        Assert.assertEquals(6,sorted.size());
        Assert.assertEquals("1",sorted.get(0).getId());
        Assert.assertEquals("2",sorted.get(1).getId());
        Assert.assertEquals("4",sorted.get(2).getId());
        Assert.assertEquals("3",sorted.get(3).getId());
        Assert.assertEquals("5",sorted.get(4).getId());
        Assert.assertEquals("6",sorted.get(5).getId());

    }
}