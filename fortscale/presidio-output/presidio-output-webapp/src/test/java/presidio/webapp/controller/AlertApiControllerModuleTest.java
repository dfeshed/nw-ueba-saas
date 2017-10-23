package presidio.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.elasticsearch.config.ElasticsearchTestUtils;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.repositories.AlertRepository;
import presidio.webapp.controllers.alerts.AlertsApi;
import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertQueryEnums;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.spring.ApiControllerModuleTestConfig;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiControllerModuleTestConfig.class)
@Category(ModuleTestCategory.class)
public class AlertApiControllerModuleTest {

    private static final String ALERTS_URI = "/alerts";
    private static final String ALERT_BY_ID_URI = "/alerts/{alertId}";

    private MockMvc alertsApiMVC;

    private static ElasticsearchTestUtils embeddedElasticsearchUtils = new ElasticsearchTestUtils();

    @Autowired
    private AlertsApi alertsApi;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private AlertRepository alertRepository;

    private ObjectMapper objectMapper;

    private presidio.output.domain.records.alerts.Alert alert1;
    private presidio.output.domain.records.alerts.Alert alert2;

    private Comparator<Alert> defaultAlertComparator = new Comparator<Alert>() {
        @Override
        public int compare(Alert o1, Alert o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    @BeforeClass
    public static void setupElasticsearch() {
        try {
            embeddedElasticsearchUtils.setupLocalElasticsearch();
        } catch (Exception e) {
            Assert.fail("Failed to start elasticsearch");
            embeddedElasticsearchUtils.stopEmbeddedElasticsearch();
        }
    }

    @Before
    public void setup() {
        //starting up the webapp server
        alertsApiMVC = MockMvcBuilders.standaloneSetup(alertsApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();

        this.objectMapper = ObjectMapperProvider.customJsonObjectMapper();

        //save alerts in elastic
        Date date = new Date();
        alert1 = generateAlert("userId1", "smartId1", Arrays.asList("a"), "userName1", 90d, AlertEnums.AlertSeverity.CRITICAL, date);
        alert2 = generateAlert("userId2", "smartId2", Arrays.asList("a"), "userName2", 90d, AlertEnums.AlertSeverity.CRITICAL, date);
        alertRepository.save(Arrays.asList(alert1, alert2));
    }

    @AfterClass
    public static void stopElasticsearch() throws Exception {
        embeddedElasticsearchUtils.stopEmbeddedElasticsearch();
    }

    @After
    public void tearDown() {
        //delete the created users
        alertRepository.delete(alert1);
        alertRepository.delete(alert2);
    }

    @Test
    public void getAllAlerts() throws Exception {

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
    public void getUserById() throws Exception {

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
        return restAlert;
    }
}
