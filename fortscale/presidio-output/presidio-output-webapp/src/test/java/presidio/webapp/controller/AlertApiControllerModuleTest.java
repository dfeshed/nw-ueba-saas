package presidio.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.test.category.ModuleTestCategory;
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
import presidio.output.domain.records.UserScorePercentilesDocument;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.users.*;
import presidio.output.domain.records.users.User;
import presidio.output.domain.repositories.AlertRepository;
import presidio.output.domain.repositories.UserRepository;
import presidio.output.domain.repositories.UserScorePercentilesRepository;
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
    private static final String UPDATE_ALERT_FEEDBACK_URI = "/alerts/updateFeedback";

    private MockMvc alertsApiMVC;

    @Autowired
    private AlertsApi alertsApi;

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlertPersistencyServiceImpl alertPersistencyService;

    @Autowired
    private UserScorePercentilesRepository userScorePercentilesRepository;

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
        //delete the created alerts
        Iterable<presidio.output.domain.records.alerts.Alert> allAlerts = alertRepository.findAll();
        alertRepository.delete(allAlerts);

        //delete the created users
        userRepository.delete(userRepository.findAll());

        //delete the created user score percentile documents
        userScorePercentilesRepository.delete(userScorePercentilesRepository.findAll());
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
        presidio.output.domain.records.alerts.Alert alert2 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
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
                .param("sortDirection", String.valueOf(Sort.Direction.ASC)))
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
    public void testGetIndicatorEvents() throws Exception {

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
        for (int i = 1; i <= eventsNum; i++) {
            IndicatorEvent event = new IndicatorEvent();
            event.setSchema(Schema.ACTIVE_DIRECTORY);
            event.setEventTime(new Date());
            event.setIndicatorId(indicatorId);
            event.setFeatures(new HashMap<>());
            events.add(event);
        }
        return events;
    }

    @Test
    public void testUpdateAlertFeedback_NONEtoRISK_noExistingPercentilesDoc() throws Exception {

        Mockito.verify(Mockito.spy(UserScorePercentilesRepository.class), Mockito.times(0)).findAll();

        //save user in elastic
        presidio.output.domain.records.users.User user = new User();
        user.setScore(150);
        user.setUserName("testUser");
        user.setSeverity(UserSeverity.HIGH);
        User savedUser = userRepository.save(user);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert = generateAlert(savedUser.getId(), "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alertRepository.save(alert);


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.RISK);
        requestBody.setAlertIds(Arrays.asList(alert.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        //feedback NONE -> RISK : alert score and contribution shouldn't be changed and same for user score
        presidio.output.domain.records.alerts.Alert updatedAlert = alertRepository.findOne(alert.getId());
        Assert.assertEquals(alert.getScore(), updatedAlert.getScore(), 0.01);
        Assert.assertEquals(alert.getContributionToUserScore(), updatedAlert.getContributionToUserScore(), 0.01);
        Assert.assertEquals(alert.getSeverity(), updatedAlert.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.RISK, updatedAlert.getFeedback());

        User updatedUser = userRepository.findOne(savedUser.getId());
        Assert.assertEquals(savedUser.getScore(), updatedUser.getScore(), 0.01);
        Assert.assertEquals(user.getSeverity(), updatedUser.getSeverity());
    }

    @Test
    public void testUpdateFeedbackForMultipleAlertSameUser_existingPercentilesDoc() throws Exception {

        UserScorePercentilesDocument percentilesDoc = new UserScorePercentilesDocument(150, 100, 50);
        userScorePercentilesRepository.save(percentilesDoc);

        //save user in elastic
        presidio.output.domain.records.users.User user = new User();
        user.setScore(150);
        user.setUserName("testUser");
        user.setSeverity(UserSeverity.MEDIUM);
        User savedUser = userRepository.save(user);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert(savedUser.getId(), "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.HIGH, date);
        alert1.setContributionToUserScore(15D);

        presidio.output.domain.records.alerts.Alert alert2 = generateAlert(savedUser.getId(), "smartId1", Arrays.asList("a"), "userName1", 5d, AlertEnums.AlertSeverity.LOW, date);
        alert2.setFeedback(AlertEnums.AlertFeedback.RISK);
        alert2.setContributionToUserScore(5D);
        alertRepository.save(Arrays.asList(alert1, alert2));


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

        presidio.output.domain.records.alerts.Alert updatedAlert1 = alertRepository.findOne(alert1.getId());
        Assert.assertEquals(alert1.getScore(), updatedAlert1.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert1.getContributionToUserScore(), 0.01);
        Assert.assertEquals(alert1.getSeverity(), updatedAlert1.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert1.getFeedback());

        presidio.output.domain.records.alerts.Alert updatedAlert2 = alertRepository.findOne(alert2.getId());
        Assert.assertEquals(alert2.getScore(), updatedAlert2.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert2.getContributionToUserScore(), 0.01);
        Assert.assertEquals(alert2.getSeverity(), updatedAlert2.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert2.getFeedback());

        User updatedUser = userRepository.findOne(savedUser.getId());
        Assert.assertEquals(130, updatedUser.getScore(), 0.01);
        Assert.assertEquals(UserSeverity.HIGH, updatedUser.getSeverity());
    }

    @Test
    public void testUpdateFeedbackForMultipleAlertSameUser_noExistingPercentilesDoc() throws Exception {

        Mockito.verify(Mockito.spy(UserScorePercentilesRepository.class), Mockito.times(0)).findAll();

        //save user in elastic
        presidio.output.domain.records.users.User user = new User();
        user.setScore(150);
        user.setUserName("testUser");
        user.setSeverity(UserSeverity.HIGH);
        User savedUser = userRepository.save(user);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert1 = generateAlert(savedUser.getId(), "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.HIGH, date);
        alert1.setContributionToUserScore(15D);

        presidio.output.domain.records.alerts.Alert alert2 = generateAlert(savedUser.getId(), "smartId1", Arrays.asList("a"), "userName1", 5d, AlertEnums.AlertSeverity.LOW, date);
        alert2.setFeedback(AlertEnums.AlertFeedback.RISK);
        alert2.setContributionToUserScore(5D);
        alertRepository.save(Arrays.asList(alert1, alert2));


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

        presidio.output.domain.records.alerts.Alert updatedAlert1 = alertRepository.findOne(alert1.getId());
        Assert.assertEquals(alert1.getScore(), updatedAlert1.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert1.getContributionToUserScore(), 0.01);
        Assert.assertEquals(alert1.getSeverity(), updatedAlert1.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert1.getFeedback());

        presidio.output.domain.records.alerts.Alert updatedAlert2 = alertRepository.findOne(alert2.getId());
        Assert.assertEquals(alert2.getScore(), updatedAlert2.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert2.getContributionToUserScore(), 0.01);
        Assert.assertEquals(alert2.getSeverity(), updatedAlert2.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert2.getFeedback());

        User updatedUser = userRepository.findOne(savedUser.getId());
        Assert.assertEquals(130, updatedUser.getScore(), 0.01);
        Assert.assertEquals(UserSeverity.LOW, updatedUser.getSeverity());
    }

    @Test
    public void testUpdateAlertFeedback_RISKtoNOT_RISK_userScorePercentilesExists() throws Exception {
        UserScorePercentilesDocument percentilesDoc = new UserScorePercentilesDocument(150, 100, 50);
        userScorePercentilesRepository.save(percentilesDoc);

        //save user in elastic
        presidio.output.domain.records.users.User user = new User();
        user.setScore(170);
        user.setUserName("testUser");
        user.setSeverity(UserSeverity.LOW);
        User savedUser = userRepository.save(user);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert = generateAlert(savedUser.getId(), "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert.setFeedback(AlertEnums.AlertFeedback.RISK);
        alert.setContributionToUserScore(10D);
        alertRepository.save(alert);


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.NOT_RISK);
        requestBody.setAlertIds(Arrays.asList(alert.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        //feedback RISK -> NOT_RISK: alert score and contribution should be updated and also the user score
        presidio.output.domain.records.alerts.Alert updatedAlert = alertRepository.findOne(alert.getId());
        Assert.assertEquals(alert.getScore(), updatedAlert.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert.getContributionToUserScore(), 0.01);
        Assert.assertEquals(alert.getSeverity(), updatedAlert.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert.getFeedback());

        User updatedUser = userRepository.findOne(savedUser.getId());
        Assert.assertEquals(savedUser.getScore() - alert.getContributionToUserScore(), updatedUser.getScore(), 0.01);
        Assert.assertEquals(UserSeverity.CRITICAL, updatedUser.getSeverity());
    }

    @Test
    public void testUpdateAlertFeedback_NOT_RISKtoRISK_userScorePercentilesExists() throws Exception {
        UserScorePercentilesDocument percentilesDoc = new UserScorePercentilesDocument(150, 100, 50);
        userScorePercentilesRepository.save(percentilesDoc);

        //save user in elastic
        presidio.output.domain.records.users.User user = new User();
        user.setScore(170);
        user.setUserName("testUser");
        user.setSeverity(UserSeverity.LOW);
        User savedUser = userRepository.save(user);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert = generateAlert(savedUser.getId(), "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert.setFeedback(AlertEnums.AlertFeedback.NOT_RISK);
        alert.setContributionToUserScore(0D);
        alertRepository.save(alert);


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.RISK);
        requestBody.setAlertIds(Arrays.asList(alert.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        //feedback NOT_RISK -> RISK: alert score and contribution should be updated and also the user score
        presidio.output.domain.records.alerts.Alert updatedAlert = alertRepository.findOne(alert.getId());
        Assert.assertEquals(alert.getScore(), updatedAlert.getScore(), 0.01);
        Assert.assertEquals(10D, updatedAlert.getContributionToUserScore(), 0.01);
        Assert.assertEquals(alert.getSeverity(), updatedAlert.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.RISK, updatedAlert.getFeedback());

        User updatedUser = userRepository.findOne(savedUser.getId());
        Assert.assertEquals(savedUser.getScore() + updatedAlert.getContributionToUserScore(), updatedUser.getScore(), 0.01);
        Assert.assertEquals(UserSeverity.CRITICAL, updatedUser.getSeverity());
    }

    @Test
    public void testUpdateAlertFeedback_RISKtoNOT_RISK_userScorePercentilesDoesntExist() throws Exception {
        //save user in elastic
        presidio.output.domain.records.users.User user = new User();
        user.setScore(170);
        user.setUserName("testUser");
        user.setSeverity(UserSeverity.LOW);
        User savedUser = userRepository.save(user);

        //save alerts in elastic
        Date date = new Date();
        presidio.output.domain.records.alerts.Alert alert = generateAlert(savedUser.getId(), "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.MEDIUM, date);
        alert.setFeedback(AlertEnums.AlertFeedback.RISK);
        alert.setContributionToUserScore(10D);
        alertRepository.save(alert);


        //building the request-  update feedback from NONE to RISK
        UpdateFeedbackRequest requestBody = new UpdateFeedbackRequest();
        requestBody.setAlertFeedback(AlertQueryEnums.AlertFeedback.NOT_RISK);
        requestBody.setAlertIds(Arrays.asList(alert.getId()));
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestBody);

        //trigger the actual API
        MvcResult mvcResult = alertsApiMVC.perform(post(UPDATE_ALERT_FEEDBACK_URI)
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        //feedback RISK -> NOT_RISK: alert score and contribution should be updated and also the user score
        presidio.output.domain.records.alerts.Alert updatedAlert = alertRepository.findOne(alert.getId());
        Assert.assertEquals(alert.getScore(), updatedAlert.getScore(), 0.01);
        Assert.assertEquals(0, updatedAlert.getContributionToUserScore(), 0.01);
        Assert.assertEquals(alert.getSeverity(), updatedAlert.getSeverity());
        Assert.assertEquals(AlertEnums.AlertFeedback.NOT_RISK, updatedAlert.getFeedback());

        User updatedUser = userRepository.findOne(savedUser.getId());
        Assert.assertEquals(savedUser.getScore() - alert.getContributionToUserScore(), updatedUser.getScore(), 0.01);
        Assert.assertEquals(UserSeverity.LOW, updatedUser.getSeverity());
    }
}
