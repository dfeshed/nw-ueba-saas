package presidio.output.proccesor.services.user;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.elasticsearch.ScrolledPage;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.commons.services.user.UserSeverityServiceImpl;
import presidio.output.domain.records.AbstractElasticDocument;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.UserSeveritiesRangeDocument;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.repositories.UserSeveritiesRangeRepository;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.proccesor.spring.OutputProcessorTestConfiguration;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.services.user.UserService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OutputProcessorTestConfiguration.class, TestConfig.class, ElasticsearchTestConfig.class, MongodbTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserScoreServiceModuleTest {

    @Autowired
    private UserSeveritiesRangeRepository userSeveritiesRangeRepository;

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private UserService userService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Autowired
    public Client client;

    @Autowired
    private AlertSeverityService alertSeverityService;

    @Autowired
    private UserSeverityService userSeverityService;

    @After
    public void cleanTestData() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + User.USER_DOC_TYPE)
                .get();
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + Alert.ALERT_TYPE)
                .get();
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + UserSeveritiesRangeDocument.USER_SEVERITY_RANGE_DOC_TYPE)
                .get();
    }

    @Test
    public void testSingleUserScoreCalculation() {
        //Generate one user with 2 critical alerts
        generateUserAndAlerts("userId1", "userName1", AlertEnums.AlertSeverity.CRITICAL, AlertEnums.AlertSeverity.CRITICAL);


        UserQuery.UserQueryBuilder queryBuilder = new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(10).filterByUsersIds(Arrays.asList("userId1"));
        Page<User> usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, usersPageResult.getContent().size());
        Assert.assertEquals("userId1", usersPageResult.getContent().get(0).getUserId());
        Assert.assertEquals("userName1", usersPageResult.getContent().get(0).getUserName());
        Assert.assertEquals(0, usersPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertEquals(null, usersPageResult.getContent().get(0).getSeverity());

        userService.updateAllUsersAlertData();
        userSeverityService.updateSeverities();

        usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, usersPageResult.getContent().size());
        Assert.assertEquals("userId1", usersPageResult.getContent().get(0).getUserId());
        Assert.assertEquals("userName1", usersPageResult.getContent().get(0).getUserName());
        Assert.assertEquals(40, usersPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertEquals(UserSeverity.LOW, usersPageResult.getContent().get(0).getSeverity());

    }

    @Test
    public void testSingleUserScoreCalculationSomeMoreThen90Days() {
        //Generate one user with 3 alerts
        User user1 = new User("userId1", "userName1", "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
        user1.setSeverity(null);
        Iterable<User> userItr = userPersistencyService.save(Arrays.asList(user1));
        User savedUser = userItr.iterator().next();
        String userId = savedUser.getId();


        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert(userId, "smartId", null, "userName1", getMinusDay(10), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 15D));
        alerts.add(new Alert(userId, "smartId", null, "userName1", getMinusDay(10), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 5D));
        alerts.add(new Alert(userId, "smartId", null, "userName1", getMinusDay(100), getMinusDay(99), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 5D));
        alertPersistencyService.save(alerts);


        Map<UserSeverity, PresidioRange<Double>> severityToScoreRangeMap = new LinkedHashMap<>();
        severityToScoreRangeMap.put(UserSeverity.LOW, new PresidioRange<>(0d, 50d));
        severityToScoreRangeMap.put(UserSeverity.MEDIUM, new PresidioRange<>(500d, 100d));
        severityToScoreRangeMap.put(UserSeverity.HIGH, new PresidioRange<>(100d, 150d));
        severityToScoreRangeMap.put(UserSeverity.CRITICAL, new PresidioRange<>(150d, 200d));
        userSeveritiesRangeRepository.save(new UserSeveritiesRangeDocument(severityToScoreRangeMap));
        userService.updateAllUsersAlertData();
        userSeverityService.updateSeverities();

        User updatedUser = userPersistencyService.findUserById(userId);
        Assert.assertEquals("userId1", updatedUser.getUserId());
        Assert.assertEquals("userName1", updatedUser.getUserName());
        Assert.assertEquals(20, updatedUser.getScore(), 0.00001);
        Assert.assertEquals(UserSeverity.LOW, updatedUser.getSeverity());

        UserSeveritiesRangeDocument userSeveritiesRangeDocument = userSeveritiesRangeRepository.findOne(UserSeveritiesRangeDocument.USER_SEVERITIES_RANGE_DOC_ID);
        Assert.assertEquals(new Double(0), userSeveritiesRangeDocument.getSeverityToScoreRangeMap().get(UserSeverity.LOW).getLowerBound());
        Assert.assertEquals(new Double(20), userSeveritiesRangeDocument.getSeverityToScoreRangeMap().get(UserSeverity.LOW).getUpperBound());
        Assert.assertEquals(new Double(22), userSeveritiesRangeDocument.getSeverityToScoreRangeMap().get(UserSeverity.MEDIUM).getLowerBound());
        Assert.assertEquals(new Double(22), userSeveritiesRangeDocument.getSeverityToScoreRangeMap().get(UserSeverity.MEDIUM).getUpperBound());
        Assert.assertEquals(new Double(28.6), userSeveritiesRangeDocument.getSeverityToScoreRangeMap().get(UserSeverity.HIGH).getLowerBound());
        Assert.assertEquals(new Double(28.6), userSeveritiesRangeDocument.getSeverityToScoreRangeMap().get(UserSeverity.HIGH).getUpperBound());
        Assert.assertEquals(new Double(42.900000000000006), userSeveritiesRangeDocument.getSeverityToScoreRangeMap().get(UserSeverity.CRITICAL).getLowerBound());
        Assert.assertEquals(new Double(42.900000000000006), userSeveritiesRangeDocument.getSeverityToScoreRangeMap().get(UserSeverity.CRITICAL).getUpperBound());
    }

    @Test
    public void testSingleUserScoreCalculationAllAlertsMoreThen90Days() {
        //Generate one user with 2 critical alerts
        User user1 = new User("userId1", "userName1", "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
        user1.setSeverity(null);
        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert(user1.getId(), "smartId", null, "userName1", getMinusDay(105), getMinusDay(104), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 25D));
        alerts.add(new Alert(user1.getId(), "smartId", null, "userName1", getMinusDay(100), getMinusDay(99), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 10D));
        alerts.add(new Alert(user1.getId(), "smartId", null, "userName1", getMinusDay(120), getMinusDay(119), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 25D));


        List<User> userList = new ArrayList<>();
        userList.add(user1);

        userPersistencyService.save(userList);
        alertPersistencyService.save(alerts);


        UserQuery.UserQueryBuilder queryBuilder = new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(10).filterByUsersIds(Arrays.asList("userId1"));
        Page<User> usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, usersPageResult.getContent().size());
        Assert.assertEquals("userId1", usersPageResult.getContent().get(0).getUserId());
        Assert.assertEquals("userName1", usersPageResult.getContent().get(0).getUserName());
        Assert.assertEquals(0, usersPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertEquals(null, usersPageResult.getContent().get(0).getSeverity());

        userService.updateAllUsersAlertData();
        userSeverityService.updateSeverities();

        usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, usersPageResult.getContent().size());
        Assert.assertEquals("userId1", usersPageResult.getContent().get(0).getUserId());
        Assert.assertEquals("userName1", usersPageResult.getContent().get(0).getUserName());
        Assert.assertEquals(0, usersPageResult.getContent().get(0).getScore(), 0.00001);

    }

    @Test
    public void testBulkUserScore() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            AlertEnums.AlertSeverity[] severities = new AlertEnums.AlertSeverity[i + 1];
            for (int j = 0; j <= i; j++) {
                severities[j] = AlertEnums.AlertSeverity.HIGH;
            }
            generateUserAndAlerts("userId" + i, "username" + i, severities);

        }

        Page<User> users = userPersistencyService.find(new UserQuery.UserQueryBuilder().pageSize(1).pageNumber(0).build());
        Assert.assertEquals(100, users.getTotalElements());
        Page<Alert> alerts = alertPersistencyService.find(new AlertQuery.AlertQueryBuilder().setPageSize(1).setPageNumber(0).build());
        Assert.assertEquals(5050, alerts.getTotalElements());

        userService.updateAllUsersAlertData();

        userSeverityService.updateSeverities();


        User user0 = getUserById("userId0");
        Assert.assertEquals(15D, user0.getScore(), 0.00001); //one medium alert
        Assert.assertEquals(UserSeverity.LOW, user0.getSeverity());

        User user60 = getUserById("userId60");
        Assert.assertEquals(915D, user60.getScore(), 0.00001); //61 medium alert
        Assert.assertEquals(UserSeverity.LOW, user60.getSeverity());


        User user99 = getUserById("userId99");
        Assert.assertEquals(1500D, user99.getScore(), 0.00001); //100 Medium Alerts
        Assert.assertEquals(UserSeverity.LOW, user99.getSeverity());
    }

    @Test
    public void testBulkUserScoreLargeScale() throws InterruptedException {
        final int DAYS_COUNT = 110;
        final int USERS_COUNT = 1000;

        List<User> userList = new ArrayList<>();
        List<LocalDateTime> dates = getListOfLastXdays(DAYS_COUNT);

        //For each user generate user and list of alerts - 2 alerts per days
        List<Alert> alertsAllUsers = new ArrayList<>();
        for (int i = 0; i < USERS_COUNT; i++) {
            User user1 = new User("userId" + i, "username" + i, "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
            user1.setSeverity(null);
            //For each day generate to alerts
            for (LocalDateTime day : dates) {
                Date alert1StartTime = new Date(Date.from(day.plusHours(3).atZone(ZoneId.systemDefault()).toInstant()).getTime());
                Date alert1EndTime = new Date(Date.from(day.plusHours(4).atZone(ZoneId.systemDefault()).toInstant()).getTime());

                Date alert2StartTime = Date.from(day.plusHours(5).atZone(ZoneId.systemDefault()).toInstant());
                Date alert2EndTime = Date.from(day.plusHours(6).atZone(ZoneId.systemDefault()).toInstant());
                //Alerts per user per day
                alertsAllUsers.add(new Alert("userId" + i, "smartId", null, "userName" + i, alert1StartTime, alert1EndTime, 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.CRITICAL, null, 30D));
                alertsAllUsers.add(new Alert("userId" + i, "smartId", null, "userName" + i, alert2StartTime, alert2EndTime, 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 25D));
            }


            userList.add(user1);
        }
        //Save all the user's alerts
        alertPersistencyService.save(alertsAllUsers);

        //Save all the users
        userPersistencyService.save(userList);

        System.out.println("Finish Inserting data " + Instant.now().toString());
        long timeBefore = System.currentTimeMillis();
        userService.updateAllUsersAlertData();

        userSeverityService.updateSeverities();
        long timeAfter = System.currentTimeMillis();
        long seconds = (timeAfter - timeBefore) / 1000;
        System.out.println("Total time in seconds: " + seconds);
        Assert.assertTrue(seconds < 120);

    }

    @Test
    public void calculateScorePercentilesTwice_shouldCreatePercentilesDocOnce() {
        //calculate percentiles with 0 users (all users should get low severity)
        userSeverityService.updateSeverities();
        Iterable<UserSeveritiesRangeDocument> all = userSeveritiesRangeRepository.findAll();
        Assert.assertEquals(1, ((ScrolledPage<UserSeveritiesRangeDocument>) all).getNumberOfElements());

        //creating new users
        for (int i = 0; i < 100; i++) {
            AlertEnums.AlertSeverity[] severities = new AlertEnums.AlertSeverity[i + 1];
            for (int j = 0; j <= i; j++) {
                severities[j] = AlertEnums.AlertSeverity.HIGH;
            }
            generateUserAndAlerts("userId" + i, "username" + i, severities);
        }

        //re-calculate percentiles with new users
        userSeverityService.updateSeverities();

        UserSeverityServiceImpl.UserScoreToSeverity severitiesMap = userSeverityService.getSeveritiesMap(false);
        all = userSeveritiesRangeRepository.findAll();
        Assert.assertEquals(1, ((ScrolledPage<UserSeveritiesRangeDocument>) all).getNumberOfElements());

    }

    private List<LocalDateTime> getListOfLastXdays(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startTime = endDate.minusDays(days);
        List<LocalDateTime> dates = new ArrayList<>();
        for (LocalDate d = startTime; !d.isAfter(endDate); d = d.plusDays(1)) {
            LocalDateTime time = d.atStartOfDay();
            dates.add(time);
        }
        return dates;
    }

    private User getUserById(String userId) {
        Page<User> users = userPersistencyService.find(new UserQuery.UserQueryBuilder().pageSize(1).pageNumber(0).filterByUsersIds(Arrays.asList(userId)).build());
        Assert.assertEquals(1, users.getTotalElements());

        User user = users.getContent().get(0);

        Assert.assertEquals(userId, user.getUserId());
        return user;
    }

    private void generateUserAndAlerts(String userId, String userName, AlertEnums.AlertSeverity... severities) {
        User user1 = new User(userId, userName, "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
        user1.setId(userId);
        user1.setSeverity(null);
        List<Alert> alerts = new ArrayList<>();

        for (AlertEnums.AlertSeverity severity : severities) {
            alerts.add(new Alert(userId, "smartId", null, userName, getMinusDay(10), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, severity, null, alertSeverityService.getUserScoreContributionFromSeverity(severity)));
        }

        List<User> userList = new ArrayList<>();
        userList.add(user1);

        userPersistencyService.save(userList);
        alertPersistencyService.save(alerts);
    }

    private Date getMinusDay(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1 * days);
        return new Date(c.getTime().getTime());

    }

}