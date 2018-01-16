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
import presidio.output.commons.services.spring.AlertSeverityServiceConfig;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.commons.services.user.UserSeverityServiceImpl;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.UserSeveritiesRangeDocument;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.repositories.UserSeveritiesRangeRepository;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.spring.UserServiceConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        AlertSeverityServiceConfig.class,
        ElasticsearchTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserScoreServiceImplTest {

    @MockBean
    private UserPersistencyService mockUserPresistency;

    @MockBean
    private UserSeveritiesRangeRepository userSeveritiesRangeRepository;

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

        Mockito.when(mockUserPresistency.find(Mockito.any(UserQuery.class))).thenAnswer(invocation -> {
            UserQuery query = (UserQuery) invocation.getArguments()[0];
            if (query.getPageNumber() == 0) {
                return page1;
            } else if (query.getPageNumber() == 1) {
                return page2;
            } else {
                return page3;
            }
        });

        Mockito.verify(Mockito.spy(UserSeveritiesRangeRepository.class), Mockito.times(0)).findOne(UserSeveritiesRangeDocument.USER_SEVERITIES_RANGE_DOC_ID);
        UserSeverityServiceImpl.UserScoreToSeverity severityTreeMap = userSeverityService.getSeveritiesMap(true);

        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(55D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(270D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(350D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(520D));

        //Special cases
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(-5D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(700D));
    }

    @Test
    public void testGetSeveritiesMap_NoRecalcSeverities_DefaultSeverities() {

        Iterable<UserSeveritiesRangeDocument> percentileScores = new ArrayList<>();
        Mockito.when(userSeveritiesRangeRepository.findAll()).thenReturn(percentileScores);

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

        UserSeveritiesRangeDocument userSeveritiesRangeDocument = new UserSeveritiesRangeDocument();
        Map<UserSeverity, PresidioRange<Double>> map = new LinkedHashMap<>();
        map.put(UserSeverity.LOW, new PresidioRange<>(0d, 240d));
        map.put(UserSeverity.MEDIUM, new PresidioRange<>(264d, 264d));
        map.put(UserSeverity.HIGH, new PresidioRange<>(343.2d, 343.2d));
        map.put(UserSeverity.CRITICAL, new PresidioRange<>(518.8d, 514.8d));
        userSeveritiesRangeDocument.setSeverityToScoreRangeMap(map);

        Mockito.when(userSeveritiesRangeRepository.findOne(UserSeveritiesRangeDocument.USER_SEVERITIES_RANGE_DOC_ID)).thenReturn(userSeveritiesRangeDocument);

        UserSeverityServiceImpl.UserScoreToSeverity severityTreeMap = userSeverityService.getSeveritiesMap(false);

        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(50D));
        Assert.assertEquals(UserSeverity.LOW, severityTreeMap.getUserSeverity(250D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(270D));
        Assert.assertEquals(UserSeverity.MEDIUM, severityTreeMap.getUserSeverity(340D));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(350));
        Assert.assertEquals(UserSeverity.HIGH, severityTreeMap.getUserSeverity(500D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(520D));
        Assert.assertEquals(UserSeverity.CRITICAL, severityTreeMap.getUserSeverity(600D));

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


        Map<UserSeverity, PresidioRange<Double>> map = new LinkedHashMap<>();
        map.put(UserSeverity.LOW, new PresidioRange<>(0d, 300d));
        map.put(UserSeverity.MEDIUM, new PresidioRange<>(30d, 50d));
        map.put(UserSeverity.HIGH, new PresidioRange<>(50d, 90d));
        map.put(UserSeverity.CRITICAL, new PresidioRange<>(90d, 90d));
        UserSeverityServiceImpl.UserScoreToSeverity userScoreToSeverity = new UserSeverityServiceImpl.UserScoreToSeverity(map);
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
