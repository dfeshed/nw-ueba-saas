package presidio.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.test.category.ModuleTestCategory;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import presidio.output.domain.records.EntitySeveritiesRangeDocument;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.repositories.AlertRepository;
import presidio.output.domain.repositories.EntityRepository;
import presidio.output.domain.repositories.EntitySeveritiesRangeRepository;
import presidio.output.domain.services.alerts.AlertPersistencyServiceImpl;
import presidio.webapp.controllers.alerts.AlertsApi;
import presidio.webapp.model.*;
import presidio.webapp.spring.ApiControllerModuleTestConfig;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiControllerModuleTestConfig.class)
@Category(ModuleTestCategory.class)
public class AlertApiControllerModuleTest {

    private static final String ALERTS_URI = "/alerts";
    private static final String ALERT_BY_ID_URI = "/alerts/{alertId}";
    private static final String EVENTS_BY_INDICATOR_ID_URI = "/alerts/{alertId}/indicators/{indicatorId}/events";
    private static final String INDICATORS_BY_ALERT_ID_URI = "/alerts/{alertId}/indicators";
    private static final String UPDATE_ALERT_FEEDBACK_URI = "/alerts/updateFeedback";

    private MockMvc alertsApiMVC;


    @Autowired
    private AlertsApi alertsApi;

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private AlertPersistencyServiceImpl alertPersistencyService;

    @Autowired
    private EntitySeveritiesRangeRepository entitySeveritiesRangeRepository;

    private ObjectMapper objectMapper;

    private Comparator<Alert> defaultAlertComparator = Comparator.comparing(Alert::getId);

    @Before
    public void setup() {
        //starting up the webapp server
        alertsApiMVC = MockMvcBuilders.standaloneSetup(alertsApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();
        this.objectMapper = ObjectMapperProvider.customJsonObjectMapper();
    }

    @After
    public void cleanTestData() {
        //delete the created alerts
        Iterable<presidio.output.domain.records.alerts.Alert> allAlerts = alertRepository.findAll();
        alertRepository.deleteAll(allAlerts);

        //delete the created entities
        entityRepository.deleteAll(entityRepository.findAll());

        //delete the created entity score percentile documents
        entitySeveritiesRangeRepository.deleteAll(entitySeveritiesRangeRepository.findAll());
    }

    @Test
    public void getAllAlerts() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date, "ja3");
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alertRepository.saveAll(Arrays.asList(alert1, alert2));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);
        Alert expectedAlert2 = convertDomainAlertToRestAlert(alert2);
        AlertsWrapper expectedResponse = new AlertsWrapper();
        expectedResponse.setTotal(2);
        List<Alert> alerts = Arrays.asList(expectedAlert1, expectedAlert2);
        expectedResponse.setAlerts(alerts);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(ALERTS_URI))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        AlertsWrapper actualResponse = objectMapper.readValue(actualResponseStr, AlertsWrapper.class);

        expectedResponse.getAlerts().sort(defaultAlertComparator);
        actualResponse.getAlerts().sort(defaultAlertComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAllAlertsWithMaxScore() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date, "ja3");
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 91d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alertRepository.saveAll(Arrays.asList(alert1, alert2));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);
        //Alert expectedAlert2 = convertDomainAlertToRestAlert(alert2);
        AlertsWrapper expectedResponse = new AlertsWrapper();
        expectedResponse.setTotal(1);
        List<Alert> alerts = Collections.singletonList(expectedAlert1);
        expectedResponse.setAlerts(alerts);
        expectedResponse.setPage(0);
        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add("sortFieldNames", "SCORE");
        params.add("minScore", "89");
        params.add("maxScore", "90");
        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(ALERTS_URI).params(params))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        AlertsWrapper actualResponse = objectMapper.readValue(actualResponseStr, AlertsWrapper.class);

        expectedResponse.getAlerts().sort(defaultAlertComparator);
        actualResponse.getAlerts().sort(defaultAlertComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }


    @Test
    public void getIndicatorsSortedWithCorrectPage() throws Exception {
        //save alerts in elastic
        Date startDate = new Date();
        Date endDate = new Date();
        List<String> indicatorNames1 = Collections.singletonList("a");
        String firstEntityName = "Z_normalized_entityname_ipusr1@somebigcompany.com";
        presidio.output.domain.records.alerts.Alert alert1 = new presidio.output.domain.records.alerts.Alert("entityId1", "smartId", null, firstEntityName, firstEntityName, startDate, endDate, 95.0d, 3, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 5D, "entityType");
        List<Indicator> indicators = new ArrayList<>();
        Indicator indicator1 = createIndicator(alert1.getId(), alert1.getEntityType(), 0.5, "0.5", startDate, endDate, Schema.ACTIVE_DIRECTORY, 0.3, 0, AlertEnums.IndicatorTypes.FEATURE_AGGREGATION);
        Indicator indicator3 = createIndicator(alert1.getId(), alert1.getEntityType(), 0.1, "0.1", startDate, endDate, Schema.ACTIVE_DIRECTORY, 0.3, 0, AlertEnums.IndicatorTypes.FEATURE_AGGREGATION);
        Indicator indicator4 = createIndicator(alert1.getId(), alert1.getEntityType(), 0.2, "0.2", startDate, endDate, Schema.ACTIVE_DIRECTORY, 0.3, 0, AlertEnums.IndicatorTypes.FEATURE_AGGREGATION);
        Indicator indicator2 = createIndicator(alert1.getId(), alert1.getEntityType(), 0.2, "0.2", startDate, endDate, Schema.ACTIVE_DIRECTORY, 0.3, 0, AlertEnums.IndicatorTypes.FEATURE_AGGREGATION);
        indicators.add(indicator1);
        indicators.add(indicator2);
        indicators.add(indicator3);
        indicators.add(indicator4);
        alert1.setIndicators(indicators);
        alert1.setIndicatorsNames(indicatorNames1);
        alertPersistencyService.save(alert1);
        alertPersistencyService.save(indicator1);
        alertPersistencyService.save(indicator2);
        alertPersistencyService.save(indicator3);
        alertPersistencyService.save(indicator4);
        alertRepository.saveAll(Collections.singletonList(alert1));

        // init expected response
        IndicatorsWrapper expectedResponse = new IndicatorsWrapper();
        expectedResponse.setTotal(4);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(INDICATORS_BY_ALERT_ID_URI, alert1.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        IndicatorsWrapper actualResponse = objectMapper.readValue(actualResponseStr, IndicatorsWrapper.class);
        Assert.assertEquals(expectedResponse.getPage(), actualResponse.getPage());
        Assert.assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        Assert.assertEquals(actualResponse.getIndicators().get(0).getScoreContribution(), 0.5, 0);
    }

    private Indicator createIndicator(String alertId, String entityType, double scoreContribution, String anomalyValue, Date startDate,
                                      Date endDate, Schema schema, double score, int envetsNum, AlertEnums.IndicatorTypes indicatorTypes) {
        Indicator indicator = new Indicator(alertId, entityType);
        indicator.setAnomalyValue(anomalyValue);
        indicator.setScoreContribution(scoreContribution);
        indicator.setStartDate(startDate);
        indicator.setEndDate(endDate);
        indicator.setSchema(schema);
        indicator.setScore(score);
        indicator.setEventsNum(envetsNum);
        indicator.setHistoricalData(null);
        indicator.setType(indicatorTypes);
        return indicator;
    }

    @Test
    public void getAlerts_filterByFeedback() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date, "ja3");
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alertRepository.saveAll(Arrays.asList(alert1, alert2));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);
        expectedAlert1.setFeedback(AlertQueryEnums.AlertFeedback.NOT_RISK);
        AlertsWrapper expectedResponse = new AlertsWrapper();
        expectedResponse.setTotal(1);
        List<Alert> alerts = Collections.singletonList(expectedAlert1);
        expectedResponse.setAlerts(alerts);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(ALERTS_URI).param("feedback", AlertQueryEnums.AlertFeedback.NOT_RISK.name()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        AlertsWrapper actualResponse = objectMapper.readValue(actualResponseStr, AlertsWrapper.class);

        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAlerts_FilterByFeedback_multipleValues() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date, "ja3");
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alertRepository.saveAll(Arrays.asList(alert1, alert2));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);
        expectedAlert1.setFeedback(AlertQueryEnums.AlertFeedback.NOT_RISK);
        Alert expectedAlert2 = convertDomainAlertToRestAlert(alert2);
        expectedAlert2.setFeedback(AlertQueryEnums.AlertFeedback.NONE);
        AlertsWrapper expectedResponse = new AlertsWrapper();
        expectedResponse.setTotal(2);
        List<Alert> alerts = Arrays.asList(expectedAlert1, expectedAlert2);
        expectedResponse.setAlerts(alerts);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(ALERTS_URI)
                .param("feedback", AlertQueryEnums.AlertFeedback.NOT_RISK.name())
                .param("feedback", AlertQueryEnums.AlertFeedback.NONE.name()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        AlertsWrapper actualResponse = objectMapper.readValue(actualResponseStr, AlertsWrapper.class);

        expectedResponse.getAlerts().sort(defaultAlertComparator);
        actualResponse.getAlerts().sort(defaultAlertComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAlerts_sortByFeedback() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date, "ja3");
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert2.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert3 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert3.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert4 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert4.setFeedback(AlertEnums.AlertFeedback.RISK);

        alertRepository.saveAll(Arrays.asList(alert1, alert2, alert3, alert4));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);
        Alert expectedAlert2 = convertDomainAlertToRestAlert(alert2);
        Alert expectedAlert3 = convertDomainAlertToRestAlert(alert3);
        Alert expectedAlert4 = convertDomainAlertToRestAlert(alert4);
        AlertsWrapper expectedResponse = new AlertsWrapper();
        expectedResponse.setTotal(4);
        List<Alert> alerts = Arrays.asList(expectedAlert1, expectedAlert2, expectedAlert3, expectedAlert4);
        expectedResponse.setAlerts(alerts);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(ALERTS_URI)
                .param("sortFieldNames", AlertQueryEnums.AlertQuerySortFieldName.FEEDBACK.name())
                .param("sortDirection", String.valueOf(Sort.Direction.ASC)))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        AlertsWrapper actualResponse = objectMapper.readValue(actualResponseStr, AlertsWrapper.class);

        expectedResponse.getAlerts().sort((o1, o2) -> o2.getFeedback().compareTo(o1.getFeedback()));
        Assert.assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        Assert.assertEquals(expectedResponse.getAlerts().size(), actualResponse.getAlerts().size());
        Assert.assertEquals(AlertQueryEnums.AlertFeedback.NOT_RISK, actualResponse.getAlerts().get(0).getFeedback());
        Assert.assertEquals(AlertQueryEnums.AlertFeedback.NOT_RISK, actualResponse.getAlerts().get(1).getFeedback());
        Assert.assertEquals(AlertQueryEnums.AlertFeedback.NOT_RISK, actualResponse.getAlerts().get(2).getFeedback());
        Assert.assertEquals(AlertQueryEnums.AlertFeedback.RISK, actualResponse.getAlerts().get(3).getFeedback());
    }

    @Test
    public void getAlerts_feedbackAggregations() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date, "ja3");
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert2.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert3 = generateAlert("entityId2", "smartId3", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert3.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert4 = generateAlert("entityId2", "smartId4", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert4.setFeedback(AlertEnums.AlertFeedback.RISK);
        presidio.output.domain.records.alerts.Alert alert5 = generateAlert("entityId2", "smartId5", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert5.setFeedback(AlertEnums.AlertFeedback.RISK);
        presidio.output.domain.records.alerts.Alert alert6 = generateAlert("entityId2", "smartId6", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert6.setFeedback(AlertEnums.AlertFeedback.NONE);
        alertRepository.saveAll(Arrays.asList(alert1, alert2, alert3, alert4, alert5, alert6));

        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(ALERTS_URI)
                .param("aggregateBy", AlertQueryEnums.AlertQueryAggregationFieldName.FEEDBACK.name()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        AlertsWrapper actualResponse = objectMapper.readValue(actualResponseStr, AlertsWrapper.class);

        Map<String, Long> feedbackAggr = actualResponse.getAggregationData().get(AlertQueryEnums.AlertQueryAggregationFieldName.FEEDBACK.name());
        Assert.assertEquals(Long.valueOf(3), feedbackAggr.get(AlertQueryEnums.AlertFeedback.NOT_RISK.name()));
        Assert.assertEquals(Long.valueOf(2), feedbackAggr.get(AlertQueryEnums.AlertFeedback.RISK.name()));
        Assert.assertEquals(Long.valueOf(1), feedbackAggr.get(AlertQueryEnums.AlertFeedback.NONE.name()));
    }

    @Test
    public void getAlerts_filterBySeverity() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date, "ja3");
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alertRepository.saveAll(Arrays.asList(alert1, alert2));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);
        AlertsWrapper expectedResponse = new AlertsWrapper();
        expectedResponse.setTotal(1);
        List<Alert> alerts = Collections.singletonList(expectedAlert1);
        expectedResponse.setAlerts(alerts);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(ALERTS_URI).param("severity", AlertQueryEnums.AlertSeverity.CRITICAL.name()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        AlertsWrapper actualResponse = objectMapper.readValue(actualResponseStr, AlertsWrapper.class);

        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAlertById() throws Exception {

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date, "ja3");
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        alertRepository.saveAll(Collections.singletonList(alert1));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);

        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(ALERT_BY_ID_URI, alert1.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        Alert actualResponse = objectMapper.readValue(actualResponseStr, Alert.class);

        Assert.assertEquals(expectedAlert1, actualResponse);
    }


    private presidio.output.domain.records.alerts.Alert generateAlert(String entityId, String smartId, List<String> classifications1, String entityName, double score, AlertEnums.AlertSeverity severity, Date date, String entityType) {
        return new presidio.output.domain.records.alerts.Alert(entityId, smartId, classifications1,
                entityName,entityName, date, date, score, 0, AlertEnums.AlertTimeframe.HOURLY, severity, null, 0.0, entityType);
    }

    private presidio.webapp.model.Alert convertDomainAlertToRestAlert(presidio.output.domain.records.alerts.Alert alert) {
        presidio.webapp.model.Alert restAlert = new presidio.webapp.model.Alert();
        restAlert.setScore(Double.valueOf(alert.getScore()).intValue());
        restAlert.setEndDate(BigDecimal.valueOf(alert.getEndDate().getTime()));
        restAlert.setStartDate(BigDecimal.valueOf(alert.getStartDate().getTime()));
        restAlert.setId(alert.getId());
        restAlert.setEntityType(alert.getEntityType());
        restAlert.setEntityScoreContribution(alert.getContributionToEntityScore() == null ? BigDecimal.valueOf(0) : new BigDecimal(alert.getContributionToEntityScore()));
        restAlert.setClassifiation(alert.getClassifications());
        restAlert.setEntityName(alert.getEntityName());
        restAlert.setEntityDocumentId(alert.getEntityDocumentId());
        restAlert.setSeverity(AlertQueryEnums.AlertSeverity.fromValue(alert.getSeverity().toString()));
        restAlert.setIndicatorsNum(alert.getIndicatorsNum());
        restAlert.setIndicatorsName(alert.getIndicatorsNames() == null ? new ArrayList<>() : alert.getIndicatorsNames());
        restAlert.setTimeframe(Alert.TimeframeEnum.fromValue(alert.getTimeframe().toString()));
        restAlert.setFeedback(AlertQueryEnums.AlertFeedback.fromValue(alert.getFeedback().toString()));
        return restAlert;
    }

    @Test
    public void testGetIndicatorEvents() throws Exception {

        presidio.output.domain.records.alerts.Alert alert = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, new Date(), "ja3");

        //generate indicators
        Indicator indicator = new Indicator(alert.getId(), alert.getEntityType());

        //generate events
        List<IndicatorEvent> indicatorEvents = generateEvents(102, indicator.getId());
        indicator.setEvents(indicatorEvents);
        alert.setIndicators(Collections.singletonList(indicator));

        alertPersistencyService.save(Collections.singletonList(alert));

        // get actual response not paged
        MvcResult mvcResult = alertsApiMVC.perform(get(EVENTS_BY_INDICATOR_ID_URI, alert.getId(), indicator.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        EventsWrapper actualResponse = objectMapper.readValue(actualResponseStr, EventsWrapper.class);

        Assert.assertEquals(102, actualResponse.getTotal().intValue());
        List<Event> events = actualResponse.getEvents();
        Assert.assertEquals(10, events.size()); //default result size is 10
    }


    private List<IndicatorEvent> generateEvents(int eventsNum, String indicatorId) {
        Date baseDate = new Date();
        List<IndicatorEvent> events = new ArrayList<>();
        for (int i = 1; i <= eventsNum; i++) {
            IndicatorEvent event = new IndicatorEvent();
            event.setSchema(Schema.ACTIVE_DIRECTORY);
            event.setEventTime(DateUtils.addDays(baseDate, i));
            event.setIndicatorId(indicatorId);
            event.setFeatures(new HashMap<>());
            events.add(event);
        }
        return events;
    }

    @Test
    public void testUpdateAlertFeedback_NONEtoRISK_noExistingPercentilesDoc() throws Exception {

        Mockito.verify(Mockito.spy(EntitySeveritiesRangeRepository.class), Mockito.times(0)).findAll();

        //save entity in elastic
        Entity entity = new Entity();
        entity.setScore(150);
        entity.setEntityName("testEntity");
        entity.setSeverity(EntitySeverity.HIGH);
        Entity savedEntity = entityRepository.save(entity);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert = generateAlert(savedEntity.getId(), "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alertRepository.save(alert);


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.RISK);
        requestBody.setAlertIds(Collections.singletonList(alert.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        //feedback NONE -> RISK : alert score and contribution shouldn't be changed and same for entity score
        presidio.output.domain.records.alerts.Alert updatedAlert = alertRepository.findById(alert.getId()).get();
        Assert.assertEquals(alert.getScore(), updatedAlert.getScore(), 0.01);
        Assert.assertEquals(alert.getContributionToEntityScore(), updatedAlert.getContributionToEntityScore(), 0.01);
        Assert.assertEquals(alert.getSeverity(), updatedAlert.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.RISK, updatedAlert.getFeedback());

        Entity updatedEntity = entityRepository.findById(savedEntity.getId()).get();
        Assert.assertEquals(savedEntity.getScore(), updatedEntity.getScore(), 0.01);
        Assert.assertEquals(entity.getSeverity(), updatedEntity.getSeverity());
    }

    @Test
    public void testUpdateFeedbackForMultipleAlertSameEntity_existingPercentilesDoc() throws Exception {

        entitySeveritiesRangeRepository.save(createEntitySeveritiesRangeDocument(50d, 100d, 150d));

        //save entity in elastic
        Entity entity = new Entity();
        entity.setScore(150);
        entity.setEntityName("testEntity");
        entity.setSeverity(EntitySeverity.MEDIUM);
        entity.setEntityType("entityType");
        Entity savedEntity = entityRepository.save(entity);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert(savedEntity.getId(), "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.HIGH, date, "ja3");
        alert1.setContributionToEntityScore(15D);

        presidio.output.domain.records.alerts.Alert alert2 = generateAlert(savedEntity.getId(), "smartId1", Collections.singletonList("a"), "entityName1", 5d, AlertEnums.AlertSeverity.LOW, date, "ja3");
        alert2.setFeedback(AlertEnums.AlertFeedback.RISK);
        alert2.setContributionToEntityScore(5D);
        alertRepository.saveAll(Arrays.asList(alert1, alert2));


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.NOT_RISK);
        requestBody.setAlertIds(Arrays.asList(alert1.getId(), alert2.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        presidio.output.domain.records.alerts.Alert updatedAlert1 = alertRepository.findById(alert1.getId()).get();
        Assert.assertEquals(alert1.getScore(), updatedAlert1.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert1.getContributionToEntityScore(), 0.01);
        Assert.assertEquals(alert1.getSeverity(), updatedAlert1.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert1.getFeedback());

        presidio.output.domain.records.alerts.Alert updatedAlert2 = alertRepository.findById(alert2.getId()).get();
        Assert.assertEquals(alert2.getScore(), updatedAlert2.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert2.getContributionToEntityScore(), 0.01);
        Assert.assertEquals(alert2.getSeverity(), updatedAlert2.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert2.getFeedback());

        Entity updatedEntity = entityRepository.findById(savedEntity.getId()).get();
        Assert.assertEquals(130, updatedEntity.getScore(), 0.01);
        Assert.assertEquals(EntitySeverity.HIGH, updatedEntity.getSeverity());
    }

    @Test
    public void testUpdateFeedbackForMultipleAlertSameEntity_noExistingPercentilesDoc() throws Exception {

        Mockito.verify(Mockito.spy(EntitySeveritiesRangeRepository.class), Mockito.times(0)).findAll();

        //save         Entity updated = entityRepository.findOne(savedEntity.getId()); in elastic
        Entity entity = new Entity();
        entity.setScore(150);
        entity.setEntityName("testEntity");
        entity.setSeverity(EntitySeverity.HIGH);
        Entity savedEntity = entityRepository.save(entity);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert(savedEntity.getId(), "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.HIGH, date, "ja3");
        alert1.setContributionToEntityScore(15D);

        presidio.output.domain.records.alerts.Alert alert2 = generateAlert(savedEntity.getId(), "smartId1", Collections.singletonList("a"), "entityName1", 5d, AlertEnums.AlertSeverity.LOW, date, "ja3");
        alert2.setFeedback(AlertEnums.AlertFeedback.RISK);
        alert2.setContributionToEntityScore(5D);
        alertRepository.saveAll(Arrays.asList(alert1, alert2));


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.NOT_RISK);
        requestBody.setAlertIds(Arrays.asList(alert1.getId(), alert2.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        presidio.output.domain.records.alerts.Alert updatedAlert1 = alertRepository.findById(alert1.getId()).get();
        Assert.assertEquals(alert1.getScore(), updatedAlert1.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert1.getContributionToEntityScore(), 0.01);
        Assert.assertEquals(alert1.getSeverity(), updatedAlert1.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert1.getFeedback());

        presidio.output.domain.records.alerts.Alert updatedAlert2 = alertRepository.findById(alert2.getId()).get();
        Assert.assertEquals(alert2.getScore(), updatedAlert2.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert2.getContributionToEntityScore(), 0.01);
        Assert.assertEquals(alert2.getSeverity(), updatedAlert2.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert2.getFeedback());

        Entity updatedEntity = entityRepository.findById(savedEntity.getId()).get();
        Assert.assertEquals(130, updatedEntity.getScore(), 0.01);
        Assert.assertEquals(EntitySeverity.LOW, updatedEntity.getSeverity());
    }

    @Test
    public void testUpdateAlertFeedback_RISKtoNOT_RISK_entityScorePercentilesExists() throws Exception {

        entitySeveritiesRangeRepository.save(createEntitySeveritiesRangeDocument(50d, 100d, 150d));

        //save entity in elastic
        Entity entity = new Entity();
        entity.setScore(170);
        entity.setEntityName("testEntity");
        entity.setSeverity(EntitySeverity.LOW);
        entity.setEntityType("entityType");
        Entity savedEntity = entityRepository.save(entity);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert = generateAlert(savedEntity.getId(), "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert.setFeedback(AlertEnums.AlertFeedback.RISK);
        alert.setContributionToEntityScore(10D);
        alertRepository.save(alert);


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.NOT_RISK);
        requestBody.setAlertIds(Collections.singletonList(alert.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        //feedback RISK -> NOT_RISK: alert score and contribution should be updated and also the entity score
        presidio.output.domain.records.alerts.Alert updatedAlert = alertRepository.findById(alert.getId()).get();
        Assert.assertEquals(alert.getScore(), updatedAlert.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert.getContributionToEntityScore(), 0.01);
        Assert.assertEquals(alert.getSeverity(), updatedAlert.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert.getFeedback());

        Entity updatedEntity = entityRepository.findById(savedEntity.getId()).get();
        Assert.assertEquals(savedEntity.getScore() - alert.getContributionToEntityScore(), updatedEntity.getScore(), 0.01);
        Assert.assertEquals(EntitySeverity.CRITICAL, updatedEntity.getSeverity());
    }

    @Test
    public void testUpdateAlertFeedback_NOT_RISKtoRISK_entityScorePercentilesExists() throws Exception {

        EntitySeveritiesRangeDocument entitySeveritiesRangeDocument = createEntitySeveritiesRangeDocument(50d, 100d, 150d);
        entitySeveritiesRangeRepository.save(entitySeveritiesRangeDocument);

        //save entity in elastic
        Entity entity = new Entity();
        entity.setScore(170);
        entity.setEntityName("testEntity");
        entity.setSeverity(EntitySeverity.LOW);
        entity.setEntityType("entityType");
        Entity savedEntity = entityRepository.save(entity);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert = generateAlert(savedEntity.getId(), "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        alert.setContributionToEntityScore(0D);
        alertRepository.save(alert);


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.RISK);
        requestBody.setAlertIds(Collections.singletonList(alert.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        //feedback NOT_RISK -> RISK: alert score and contribution should be updated and also the entity score
        presidio.output.domain.records.alerts.Alert updatedAlert = alertRepository.findById(alert.getId()).get();
        Assert.assertEquals(alert.getScore(), updatedAlert.getScore(), 0.01);
        Assert.assertEquals(10D, updatedAlert.getContributionToEntityScore(), 0.01);
        Assert.assertEquals(alert.getSeverity(), updatedAlert.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.RISK, updatedAlert.getFeedback());

        Entity updatedEntity = entityRepository.findById(savedEntity.getId()).get();
        Assert.assertEquals(savedEntity.getScore() + updatedAlert.getContributionToEntityScore(), updatedEntity.getScore(), 0.01);
        Assert.assertEquals(EntitySeverity.CRITICAL, updatedEntity.getSeverity());
    }

    private EntitySeveritiesRangeDocument createEntitySeveritiesRangeDocument(double lowSeverityUpperBound, double mediumSeverityUpperBound, double highSeverityUpperBound) {
        Map<EntitySeverity, PresidioRange<Double>> entitySeveritiesRangeMap = new LinkedHashMap<>();
        entitySeveritiesRangeMap.put(EntitySeverity.LOW, new PresidioRange<>(0d, lowSeverityUpperBound));
        entitySeveritiesRangeMap.put(EntitySeverity.MEDIUM, new PresidioRange<>(lowSeverityUpperBound, mediumSeverityUpperBound));
        entitySeveritiesRangeMap.put(EntitySeverity.HIGH, new PresidioRange<>(mediumSeverityUpperBound, highSeverityUpperBound));
        entitySeveritiesRangeMap.put(EntitySeverity.CRITICAL, new PresidioRange<>(highSeverityUpperBound, highSeverityUpperBound * 1.5));
        return new EntitySeveritiesRangeDocument(entitySeveritiesRangeMap, "entityType");
    }

    @Test
    public void testUpdateAlertFeedback_RISKtoNOT_RISK_entityScorePercentilesDoesntExist() throws Exception {
        //save entity in elastic
        Entity entity = new Entity();
        entity.setScore(170);
        entity.setEntityName("testEntity");
        entity.setSeverity(EntitySeverity.LOW);
        Entity savedEntity = entityRepository.save(entity);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert = generateAlert(savedEntity.getId(), "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "ja3");
        alert.setFeedback(AlertEnums.AlertFeedback.RISK);
        alert.setContributionToEntityScore(10D);
        alertRepository.save(alert);


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.NOT_RISK);
        requestBody.setAlertIds(Collections.singletonList(alert.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        //feedback RISK -> NOT_RISK: alert score and contribution should be updated and also the entity score
        presidio.output.domain.records.alerts.Alert updatedAlert = alertRepository.findById(alert.getId()).get();
        Assert.assertEquals(alert.getScore(), updatedAlert.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert.getContributionToEntityScore(), 0.01);
        Assert.assertEquals(alert.getSeverity(), updatedAlert.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert.getFeedback());

        Entity updatedEntity = entityRepository.findById(savedEntity.getId()).get();
        Assert.assertEquals(savedEntity.getScore() - alert.getContributionToEntityScore(), updatedEntity.getScore(), 0.01);
        Assert.assertEquals(EntitySeverity.LOW, updatedEntity.getSeverity());
    }

    @Test
    public void getAlertsFilterByEntityType() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        String expectedEntityType = "ja3";
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("entityId1", "smartId1", Collections.singletonList("a"), "entityName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date, expectedEntityType);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("entityId2", "smartId2", Collections.singletonList("a"), "entityName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date, "sslSubject");
        alertRepository.saveAll(Arrays.asList(alert1, alert2));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);
        AlertsWrapper expectedResponse = new AlertsWrapper();
        expectedResponse.setTotal(1);
        List<Alert> alerts = Collections.singletonList(expectedAlert1);
        expectedResponse.setAlerts(alerts);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = alertsApiMVC.perform(get(ALERTS_URI).param("entityType", expectedEntityType))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        AlertsWrapper actualResponse = objectMapper.readValue(actualResponseStr, AlertsWrapper.class);

        Assert.assertEquals(expectedResponse, actualResponse);
    }
}
