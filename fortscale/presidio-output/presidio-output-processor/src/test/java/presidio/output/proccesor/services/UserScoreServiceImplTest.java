package presidio.output.proccesor.services;

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
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;
import presidio.output.processor.services.user.UserScoreServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shays on 27/08/2017.
 */

public class UserScoreServiceImplTest {

    private UserScoreServiceImpl userScoreService;
    private UserPersistencyService mockPresistency;

    @Before
    public void setup() {
        mockPresistency = Mockito.mock(UserPersistencyServiceImpl.class);

        userScoreService = new UserScoreServiceImpl(mockPresistency,
                null,
                1000,
                1000,
                90,
                75,
                50,
                25,
                30,
                25,
                20,
                10);
    }

    @Test
    public void testSeveritiesMapLargeScale() throws Exception {


        double[] d = new double[1_000_000];
        for (int i = 0; i < d.length; i++) {
            d[i] = i;
        }

        UserScoreServiceImpl.UserScoreToSeverity severityTreeMap = Whitebox.invokeMethod(userScoreService, "getSeveritiesMap", d);

        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getSeverity(100D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getSeverity(249999D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getSeverity(250_000D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getSeverity(499_999D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getSeverity(500_000D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getSeverity(749999D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getSeverity(750_000D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getSeverity(999_999D));

        //Special cases
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getSeverity(-5D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getSeverity(1_150_000D));


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

        UserScoreServiceImpl.UserScoreToSeverity severityTreeMap = Whitebox.invokeMethod(userScoreService, "getSeveritiesMap", d);

        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getSeverity(100D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getSeverity(300D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getSeverity(500D));

        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getSeverity(600D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getSeverity(900D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getSeverity(1_000D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getSeverity(2_000D));

        //Special cases
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getSeverity(-1D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getSeverity(3000D));


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


        UserScoreServiceImpl.UserScoreToSeverity userScoreToSeverity = new UserScoreServiceImpl.UserScoreToSeverity(20D, 40D, 80D);
        Whitebox.invokeMethod(userScoreService, "updateSeveritiesForUsersList", userScoreToSeverity, page1.getContent(), true);


        Assert.assertEquals(UserSeverity.LOW, page.get(0).getUserSeverity());
        Assert.assertEquals(UserSeverity.LOW, page.get(1).getUserSeverity());
        Assert.assertEquals(UserSeverity.LOW, page.get(2).getUserSeverity());
        Assert.assertEquals(UserSeverity.MEDIUM, page.get(3).getUserSeverity());
        Assert.assertEquals(UserSeverity.MEDIUM, page.get(4).getUserSeverity());
        Assert.assertEquals(UserSeverity.HIGH, page.get(5).getUserSeverity());
        Assert.assertEquals(UserSeverity.HIGH, page.get(6).getUserSeverity());
        Assert.assertEquals(UserSeverity.HIGH, page.get(7).getUserSeverity());
        Assert.assertEquals(UserSeverity.HIGH, page.get(8).getUserSeverity());
        Assert.assertEquals(UserSeverity.CRITICAL, page.get(9).getUserSeverity());

    }
}
