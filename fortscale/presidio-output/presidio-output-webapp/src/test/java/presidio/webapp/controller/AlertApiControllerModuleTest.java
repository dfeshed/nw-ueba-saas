package presidio.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import presidio.output.client.model.AlertQuery;
import presidio.output.domain.records.alerts.*;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.repositories.AlertRepository;
import presidio.output.domain.services.alerts.AlertPersistencyServiceImpl;
import presidio.webapp.controllers.alerts.AlertsApi;
import presidio.webapp.model.*;
import presidio.webapp.model.Alert;
import presidio.webapp.spring.ApiControllerModuleTestConfig;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiControllerModuleTestConfig.class)
@Category(ModuleTestCategory.class)
@ActiveProfiles("useEmbeddedElastic")
public class AlertApiControllerModuleTest {

    private static final String ALERTS_URI = "/alerts";
    private static final String ALERT_BY_ID_URI = "/alerts/{alertId}";
    private static final String EVENTS_BY_INDICATOR_ID_URI = "/alerts/{alertId}/indicators/{indicatorId}/events";

    private MockMvc alertsApiMVC;

    @Autowired
    private AlertsApi alertsApi;

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertPersistencyServiceImpl alertPersistencyService;

    private ObjectMapper objectMapper;

    private Comparator<Alert> defaultAlertComparator = new Comparator<Alert>() {
        @Override
        public int compare(Alert o1, Alert o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    @Before
    public void setup() {
        //starting up the webapp server
        alertsApiMVC = MockMvcBuilders.standaloneSetup(alertsApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();
        this.objectMapper = ObjectMapperProvider.customJsonObjectMapper();
    }

    @After
    public void cleanTestData() {
        //delete the created users
        Iterable<presidio.output.domain.records.alerts.Alert> allAlerts = alertRepository.findAll();
        alertRepository.delete(allAlerts);
    }

    @Test
    public void getAllAlerts() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("userId1", "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alertRepository.save(Arrays.asList(alert1, alert2));

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

        Collections.sort(expectedResponse.getAlerts(), defaultAlertComparator);
        Collections.sort(actualResponse.getAlerts(), defaultAlertComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAlerts_filterByFeedback() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("userId1", "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date);
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alertRepository.save(Arrays.asList(alert1, alert2));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);
        expectedAlert1.setFeedback(AlertQueryEnums.AlertFeedback.NOT_RISK);
        AlertsWrapper expectedResponse = new AlertsWrapper();
        expectedResponse.setTotal(1);
        List<Alert> alerts = Arrays.asList(expectedAlert1);
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
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("userId1", "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date);
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alertRepository.save(Arrays.asList(alert1, alert2));

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

        Collections.sort(expectedResponse.getAlerts(), defaultAlertComparator);
        Collections.sort(actualResponse.getAlerts(), defaultAlertComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAlerts_sortByFeedback() throws Exception {
        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("userId1", "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date);
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert  alert2 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert2.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert3 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert3.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert4 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert4.setFeedback(AlertEnums.AlertFeedback.RISK);

        alertRepository.save(Arrays.asList(alert1, alert2, alert3, alert4));

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
                .param("sortFieldNames", String.valueOf(AlertQueryEnums.AlertQuerySortFieldName.FEEDBACK.name()))
                .param("sortDirection", String.valueOf(AlertQuery.SortDirectionEnum.ASC.name())))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        AlertsWrapper actualResponse = objectMapper.readValue(actualResponseStr, AlertsWrapper.class);

        Collections.sort(expectedResponse.getAlerts(), new Comparator<Alert>() {
            @Override
            public int compare(Alert o1, Alert o2) {
                return o2.getFeedback().compareTo(o1.getFeedback());
            }
        });
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
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("userId1", "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date);
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert2.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert3 = generateAlert("userId2", "smartId3", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert3.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        presidio.output.domain.records.alerts.Alert alert4 = generateAlert("userId2", "smartId4", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert4.setFeedback(AlertEnums.AlertFeedback.RISK);
        presidio.output.domain.records.alerts.Alert alert5 = generateAlert("userId2", "smartId5", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert5.setFeedback(AlertEnums.AlertFeedback.RISK);
        presidio.output.domain.records.alerts.Alert alert6 = generateAlert("userId2", "smartId6", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert6.setFeedback(AlertEnums.AlertFeedback.NONE);
        alertRepository.save(Arrays.asList(alert1, alert2, alert3, alert4, alert5, alert6));

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
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("userId1", "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date);
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alertRepository.save(Arrays.asList(alert1, alert2));

        // init expected response
        Alert expectedAlert1 = convertDomainAlertToRestAlert(alert1);
        AlertsWrapper expectedResponse = new AlertsWrapper();
        expectedResponse.setTotal(1);
        List<Alert> alerts = Arrays.asList(expectedAlert1);
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
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert("userId1", "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date);
        alert1.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        alertRepository.save(Arrays.asList(alert1));

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


    private presidio.output.domain.records.alerts.Alert generateAlert(String userId, String smartId, List<String> classifications1, String userName, double score, AlertEnums.AlertSeverity severity, Date date) {
        return new presidio.output.domain.records.alerts.Alert(userId, smartId, classifications1,
                userName, date, date, score, 0, AlertEnums.AlertTimeframe.HOURLY, severity, null, 0.0);
    }

    private presidio.webapp.model.Alert convertDomainAlertToRestAlert(presidio.output.domain.records.alerts.Alert alert) {
        presidio.webapp.model.Alert restAlert = new presidio.webapp.model.Alert();
        restAlert.setScore(Double.valueOf(alert.getScore()).intValue());
        restAlert.setEndDate(BigDecimal.valueOf(alert.getEndDate().getTime()));
        restAlert.setStartDate(BigDecimal.valueOf(alert.getStartDate().getTime()));
        restAlert.setId(alert.getId());
        restAlert.setUserScoreContribution(alert.getContributionToUserScore() == null ? BigDecimal.valueOf(0) : new BigDecimal(alert.getContributionToUserScore()));
        restAlert.setClassifiation(alert.getClassifications());
        restAlert.setUsername(alert.getUserName());
        restAlert.setUserId(alert.getUserId());
        restAlert.setSeverity(AlertQueryEnums.AlertSeverity.fromValue(alert.getSeverity().toString()));
        restAlert.setIndicatorsNum(alert.getIndicatorsNum());
        restAlert.setIndicatorsName(alert.getIndicatorsNames() == null ? new ArrayList<>() : alert.getIndicatorsNames());
        restAlert.setTimeframe(Alert.TimeframeEnum.fromValue(alert.getTimeframe().toString()));
        restAlert.setFeedback(AlertQueryEnums.AlertFeedback.fromValue(alert.getFeedback().toString()));
        return restAlert;
    }

    @Test
    public void testGetIndicatorEvents() throws Exception{

        presidio.output.domain.records.alerts.Alert alert = generateAlert("userId1", "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.CRITICAL, new Date());

        //generate indicators
        Indicator indicator = new Indicator(alert.getId());

        //generate events
        List<IndicatorEvent> indicatorEvents = generateEvents(102, indicator.getId());
        indicator.setEvents(indicatorEvents);
        alert.setIndicators(Arrays.asList(indicator));

        alertPersistencyService.save(Arrays.asList(alert));

        // get actual response not paged
        MvcResult mvcResult = alertsApiMVC.perform(get(EVENTS_BY_INDICATOR_ID_URI, alert.getId(), indicator.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        EventsWrapper actualResponse = objectMapper.readValue(actualResponseStr, EventsWrapper.class);

        Assert.assertEquals(102, actualResponse.getTotal().intValue());
        Assert.assertEquals(10, actualResponse.getEvents().size()); //default result size is 10
    }


    private List<IndicatorEvent> generateEvents(int eventsNum, String indicatorId) {
        List<IndicatorEvent> events = new ArrayList<>();
        for(int i = 1; i <= eventsNum; i ++) {
            IndicatorEvent event = new IndicatorEvent();
            event.setSchema(Schema.ACTIVE_DIRECTORY);
            event.setEventTime(new Date());
            event.setIndicatorId(indicatorId);
            event.setFeatures(new HashMap<>());
            events.add(event);
        }
        return events;

    }
}
