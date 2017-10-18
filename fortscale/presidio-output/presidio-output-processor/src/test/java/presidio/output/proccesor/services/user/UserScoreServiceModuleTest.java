package presidio.output.proccesor.services.user;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserScoreServiceImpl;
import presidio.output.processor.services.user.UserService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig.class)
public class UserScoreServiceModuleTest {

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private UserService userService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    @Autowired
    private AlertPersistencyService alertPersistencyService;


    private UserScoreService userScoreService;


    @Before
    public void before() {
        esTemplate.deleteIndex(User.class);
        esTemplate.createIndex(User.class);
        esTemplate.putMapping(User.class);
        esTemplate.refresh(User.class);

        userScoreService = new UserScoreServiceImpl(
                userPersistencyService,
                alertPersistencyService,
                10,
                30,
                75,
                50,
                25,
                20,
                15,
                10,
                5

        );
    }

    @After
    public void after() {
        esTemplate.deleteIndex(User.class);
        esTemplate.createIndex(User.class);
        esTemplate.putMapping(User.class);
        esTemplate.refresh(User.class);

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
        userScoreService.updateSeverities();

        usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, usersPageResult.getContent().size());
        Assert.assertEquals("userId1", usersPageResult.getContent().get(0).getUserId());
        Assert.assertEquals("userName1", usersPageResult.getContent().get(0).getUserName());
        Assert.assertEquals(40, usersPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertNotEquals(null, usersPageResult.getContent().get(0).getSeverity());

    }

    @Test
    public void testSingleUserScoreCalculationSomeMoreThen30Days() {
        //Generate one user with 2 critical alerts
        User user1 = new User("userId1", "userName1", "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
        user1.setSeverity(null);
        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert("userId1", "smartId", null, "userName1", getMinusDay(10), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 0D));
        alerts.add(new Alert("userId1", "smartId", null, "userName1", getMinusDay(10), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 0D));
        alerts.add(new Alert("userId1", "smartId", null, "userName1", getMinusDay(40), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 0D));


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
        userScoreService.updateSeverities();

        usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, usersPageResult.getContent().size());
        Assert.assertEquals("userId1", usersPageResult.getContent().get(0).getUserId());
        Assert.assertEquals("userName1", usersPageResult.getContent().get(0).getUserName());
        Assert.assertEquals(20, usersPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertNotEquals(null, usersPageResult.getContent().get(0).getSeverity());

    }

    @Test
    public void testSingleUserScoreCalculationAllAlertsMoreThen30Days() {
        //Generate one user with 2 critical alerts
        User user1 = new User("userId1", "userName1", "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
        user1.setSeverity(null);
        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert("userId1", "smartId", null, "userName1", getMinusDay(60), getMinusDay(59), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 0D));
        alerts.add(new Alert("userId1", "smartId", null, "userName1", getMinusDay(80), getMinusDay(79), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 0D));
        alerts.add(new Alert("userId1", "smartId", null, "userName1", getMinusDay(40), getMinusDay(39), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 0D));


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
        userScoreService.updateSeverities();

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

        userScoreService.updateSeverities();


        User user0 = getUserById("userId0");
        Assert.assertEquals(15D, user0.getScore(), 0.00001); //one medium alert
        Assert.assertEquals(UserSeverity.LOW, user0.getSeverity());

        User user60 = getUserById("userId60");
        Assert.assertEquals(915D, user60.getScore(), 0.00001); //61 medium alert
        Assert.assertEquals(UserSeverity.HIGH, user60.getSeverity());


        User user99 = getUserById("userId99");
        Assert.assertEquals(1500D, user99.getScore(), 0.00001); //100 Medium Alerts
        Assert.assertEquals(UserSeverity.CRITICAL, user99.getSeverity());


    }


    @Test
    public void testBulkUserScoreLargeScale() throws InterruptedException {
        final int DAYS_COUNT = 110;
        final int USERS_COUNT = 4000;
        userScoreService = new UserScoreServiceImpl(
                userPersistencyService,
                alertPersistencyService,
                500,
                DAYS_COUNT + 10,
                75,
                50,
                25,
                20,
                15,
                10,
                5

        );


        List<User> userList = new ArrayList<>();
        List<LocalDateTime> dates = getListOfLastXdays(DAYS_COUNT);

        //For each user generate user and list of alerts - 2 alerts per days
        for (int i = 0; i < USERS_COUNT; i++) {
            User user1 = new User("userId" + i, "username" + 1, "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
            user1.setSeverity(null);
            List<Alert> alerts = new ArrayList<>();
            //For each day generate to alerts
            for (LocalDateTime day : dates) {
                Date alert1StartTime = new Date(Date.from(day.plusHours(3).atZone(ZoneId.systemDefault()).toInstant()).getTime());
                Date alert1EndTime = new Date(Date.from(day.plusHours(4).atZone(ZoneId.systemDefault()).toInstant()).getTime());

                Date alert2StartTime = Date.from(day.plusHours(5).atZone(ZoneId.systemDefault()).toInstant());
                Date alert2EndTime = Date.from(day.plusHours(6).atZone(ZoneId.systemDefault()).toInstant());
                //Alerts per user per day
                alerts.add(new Alert("userId" + i, "smartId", null, "userName" + 1, alert1StartTime, alert1EndTime, 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.CRITICAL, null, 0D));
                alerts.add(new Alert("userId" + i, "smartId", null, "userName" + 1, alert2StartTime, alert2EndTime, 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 0D));
            }


            userList.add(user1);

            //Save all the user's alerts
            alertPersistencyService.save(alerts);
        }
        //Save all the users
        userPersistencyService.save(userList);

        System.out.println("Finish Inserting data " + Instant.now().toString());
        long timeBefore = System.currentTimeMillis();
        userService.updateAllUsersAlertData();

        userScoreService.updateSeverities();
        long timeAfter = System.currentTimeMillis();
        long seconds = (timeAfter - timeBefore) / 1000;
        System.out.println("Total time in seconds: " + seconds);
        Assert.assertTrue(seconds < 120);

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
        user1.setSeverity(null);
        List<Alert> alerts = new ArrayList<>();

        for (AlertEnums.AlertSeverity severity : severities) {
            alerts.add(new Alert(userId, "smartId", null, userName, getMinusDay(10), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, severity, null, 0D));
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