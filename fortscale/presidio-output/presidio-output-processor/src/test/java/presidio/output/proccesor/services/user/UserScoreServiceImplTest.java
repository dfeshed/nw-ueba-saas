package presidio.output.proccesor.services.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import presidio.output.commons.services.alert.AlertEnumsSeverityService;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.commons.services.alert.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;
import presidio.output.processor.services.user.UserScoreServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shays on 27/08/2017.
 */

public class UserScoreServiceImplTest {

    public static final int PERCENT_THRESHOLD_CRITICAL = 95;
    public static final int PERCENT_THRESHOLD_HIGH = 80;
    public static final int PERCENT_THRESHOLD_MEDIUM = 70;
    public static final int CRITICAL_SCORE = 95;
    public static final int HIGH_SCORE = 90;
    public static final int MEDIUM_SCORE = 80;
    public static final int ALERT_CONTRIBUTION_CRITICAL = 30;
    public static final int ALERT_CONTRIBUTION_HIGH = 25;
    public static final int ALERT_CONTRIBUTION_MEDIUM = 20;
    public static final int ALERT_CONTRIBUTION_LOW = 10;
    public static final int ALERT_EFFECTIVE_DURATION_IN_DAYS = 90;

    private UserScoreServiceImpl userScoreService;
    private UserPersistencyService mockPresistency;
    private AlertSeverityService aertSeverityService;

    @Before
    public void setup() {
        mockPresistency = Mockito.mock(UserPersistencyServiceImpl.class);
        aertSeverityService = new AlertEnumsSeverityService(CRITICAL_SCORE,
                HIGH_SCORE,
                MEDIUM_SCORE,
                ALERT_CONTRIBUTION_CRITICAL,
                ALERT_CONTRIBUTION_HIGH,
                ALERT_CONTRIBUTION_MEDIUM,
                ALERT_CONTRIBUTION_LOW,
                PERCENT_THRESHOLD_CRITICAL,
                PERCENT_THRESHOLD_HIGH,
                PERCENT_THRESHOLD_MEDIUM);

        userScoreService = new UserScoreServiceImpl(mockPresistency, null, aertSeverityService, 1000, 90);
    }

    @Test
    public void testSeveritiesMapLargeScale() throws Exception {


        double[] d = new double[1_000_000];
        for (int i = 0; i < d.length; i++) {
            d[i] = i;
        }

//        AlertEnumsSeverityService.UserScoreToSeverity severityTreeMap = Whitebox.invokeMethod(userScoreService, "getSeveritiesMap", d);
        AlertEnumsSeverityService.UserScoreToSeverity severityTreeMap = aertSeverityService.getSeveritiesMap(d);

        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(100D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(249999D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(250_000D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(499_999D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(500_000D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(749999D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(750_000D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(999_999D));

        //Special cases
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(-5D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(1_150_000D));


    }


    @Test
    public void testSeveritiesMapSmallScale() throws Exception {


        double[] d = new double[20];
        //3 instances with value 100
        d[0] = 100;
        d[1] = 100;
        d[2] = 100;

        //5 instances with value 300
        d[3] = 300;
        d[4] = 300;
        d[5] = 300;
        d[6] = 300;
        d[7] = 300;
        //2 instances with value 500
        d[8] = 500;
        d[9] = 500;

        //2 instances with value 600
        d[10] = 600;
        d[11] = 600;

        //4 instances with value 900
        d[12] = 900;
        d[13] = 900;
        d[14] = 900;
        d[15] = 900;

        //3 instances with value 1000
        d[16] = 1000;
        d[17] = 1000;
        d[18] = 1000;

        //1 instance with value 2000
        d[19] = 2000;

//        AlertEnumsSeverityService.UserScoreToSeverity severityTreeMap = Whitebox.invokeMethod(userScoreService, "getSeveritiesMap", d);
        AlertEnumsSeverityService.UserScoreToSeverity severityTreeMap = aertSeverityService.getSeveritiesMap(d);

        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(100D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(300D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(500D));

        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(600D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(900D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(1_000D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(2_000D));

        //Special cases
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(-1D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(3000D));


    }

    @Test
    public void getScoresArray() throws Exception {
        //Create page1 with 10 users and page2 with 5 users
        List<User> page1Users = new ArrayList<>();
        List<User> page2Users = new ArrayList<>();
        List<User> page3Users = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            User u = new User();
            u.setScore(i * 10D);
            if (i < 10) {

                page1Users.add(u);
            } else if (i < 20) {
                page2Users.add(u);
            } else {
                page3Users.add(u);
            }
        }
        Pageable pageable1 = new PageRequest(0, 10);
        Page<User> page1 = new PageImpl(page1Users, pageable1, 25);
        Pageable pageable2 = new PageRequest(1, 10);
        Page<User> page2 = new PageImpl(page2Users, pageable2, 25);
        Pageable pageable3 = new PageRequest(2, 10);
        Page<User> page3 = new PageImpl(page3Users, pageable3, 25);

        Mockito.when(this.mockPresistency.find(Mockito.any(UserQuery.class))).thenAnswer(new Answer<Page>() {
            @Override
            public Page answer(InvocationOnMock invocation) throws Throwable {
                UserQuery query = (UserQuery) invocation.getArguments()[0];
                if (query.getPageNumber() == 0) {
                    return page1;
                } else if (query.getPageNumber() == 1) {
                    return page2;
                } else {
                    return page3;
                }
            }
        });

        double[] scores = Whitebox.invokeMethod(userScoreService, "getScoresArray");

        Assert.assertEquals(25, scores.length, 0.1);
        Assert.assertEquals(0D, scores[0], 0.1);
        Assert.assertEquals(240D, scores[24], 0.1);

    }

    @Test
    public void testUpdateSeveritiesForUsersPage() throws Exception {

        List<User> page = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User u = new User();
            u.setScore(i * 10D);
            page.add(u);
        }
        Pageable pageable1 = new PageRequest(0, 10);
        Page<User> page1 = new PageImpl(page, pageable1, 10);


        AlertEnumsSeverityService.UserScoreToSeverity userScoreToSeverity = new AlertEnumsSeverityService.UserScoreToSeverity(20D, 40D, 80D);
        Whitebox.invokeMethod(userScoreService, "updateSeveritiesForUsersList", userScoreToSeverity, page1.getContent(), true);


        Assert.assertEquals(UserSeverity.LOW, page.get(0).getSeverity());
        Assert.assertEquals(UserSeverity.LOW, page.get(1).getSeverity());
        Assert.assertEquals(UserSeverity.LOW, page.get(2).getSeverity());
        Assert.assertEquals(UserSeverity.MEDIUM, page.get(3).getSeverity());
        Assert.assertEquals(UserSeverity.MEDIUM, page.get(4).getSeverity());
        Assert.assertEquals(UserSeverity.HIGH, page.get(5).getSeverity());
        Assert.assertEquals(UserSeverity.HIGH, page.get(6).getSeverity());
        Assert.assertEquals(UserSeverity.HIGH, page.get(7).getSeverity());
        Assert.assertEquals(UserSeverity.HIGH, page.get(8).getSeverity());
        Assert.assertEquals(UserSeverity.CRITICAL, page.get(9).getSeverity());

    }
}
