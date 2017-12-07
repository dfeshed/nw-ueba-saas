package presidio.output.proccesor.services.user;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.commons.services.user.UserSeverityServiceImpl;
import presidio.output.domain.records.UserScorePercentilesDocument;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.repositories.UserScorePrcentilesRepository;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.spring.AlertEnumsConfig;
import presidio.output.processor.spring.UserServiceConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shays on 27/08/2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        TestConfig.class,
        UserServiceConfig.class,
        EventPersistencyServiceConfig.class,
        PresidioOutputPersistencyServiceConfig.class,
        MongodbTestConfig.class,
        AlertEnumsConfig.class,
        ElasticsearchTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserScoreServiceImplTest {

    @MockBean
    private UserPersistencyService mockUserPresistency;

    @MockBean
    private UserScorePrcentilesRepository mockPercentilesRepository;

    @Autowired
    private UserSeverityService userSeverityService;

    @Test
    public void testGetSeveritiesMap_RecalcSeverities() {

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

        Mockito.when(mockUserPresistency.find(Mockito.any(UserQuery.class))).thenAnswer(new Answer<Page>() {
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


        UserSeverityServiceImpl.UserScoreToSeverity severityTreeMap = userSeverityService.getSeveritiesMap(true);

        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(55D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(2D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(56D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(120D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(185D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(122D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(186D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(240D));

        //Special cases
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(-5D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(300D));
    }

    @Test
    public void testGetSeveritiesMap_NoRecalcSeverities_DefaultSeverities() {

        Iterable<UserScorePercentilesDocument> percentileScores = new ArrayList<>();
        Mockito.when(mockPercentilesRepository.findAll()).thenReturn(percentileScores);

        UserSeverityServiceImpl.UserScoreToSeverity severityTreeMap = userSeverityService.getSeveritiesMap(false);

        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(55D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(2D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(56D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(120D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(185D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(122D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(186D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(240D));

        //Special cases
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(-5D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(300D));
    }

    @Test
    public void testGetSeveritiesMap_NoRecalcSeverities_ExistingSeverities() {

        UserScorePercentilesDocument percentileDoc = new UserScorePercentilesDocument();
        percentileDoc.setCeilScoreForHighSeverity(150);
        percentileDoc.setCeilScoreForMediumSeverity(100);
        percentileDoc.setCeilScoreForLowSeverity(50);
        Mockito.when(mockPercentilesRepository.findAll()).thenReturn(Arrays.asList(percentileDoc));

        UserSeverityServiceImpl.UserScoreToSeverity severityTreeMap = userSeverityService.getSeveritiesMap(false);

        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(50D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(2D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(56D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(100D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(150));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(122D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(188D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(240D));

        //Special cases
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(-5D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(0D));
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

        Mockito.when(mockUserPresistency.find(Mockito.any(UserQuery.class))).thenAnswer(new Answer<Page>() {
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

        double[] scores = Whitebox.invokeMethod(userSeverityService, "getScoresArray");

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


        UserSeverityServiceImpl.UserScoreToSeverity userScoreToSeverity = new UserSeverityServiceImpl.UserScoreToSeverity(20D, 40D, 80D);
        Whitebox.invokeMethod(userSeverityService, "updateSeveritiesForUsersList", userScoreToSeverity, page1.getContent(), true);


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
