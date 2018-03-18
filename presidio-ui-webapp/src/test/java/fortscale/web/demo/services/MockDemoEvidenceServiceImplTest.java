package fortscale.web.demo.services;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.domain.dto.SeveritiesCountDTO;
import fortscale.web.demoservices.DemoBuilder;
import fortscale.web.demoservices.services.MockDemoAlertsServiceImpl;
import fortscale.web.demoservices.services.MockDemoEvidencesServiceImpl;
import fortscale.web.demoservices.services.MockDemoUserServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;

import java.util.*;

/**
 * Created by shays on 30/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MockDemoEvidenceServiceImplTest {


    private MockDemoEvidencesServiceImpl mockDemoEvidencesService;
    private DemoBuilder demoBuilder;


    @Before
    public void setUp(){
        User user1=new User();
        user1.setMockId("1");
        user1.setSearchField("Mark Avraham");
        user1.setUsername("Mark@Avraham");
        user1.setDisplayName("Mark Avraham");

        User user2=new User();
        user2.setMockId("2");
        user2.setSearchField("Beni Burger");
        user2.setUsername("Beni@Burger");
        user2.setDisplayName("Beni Burger");


        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);


//
//        Set<DataSourceAnomalyTypePair> loginsAnomalyDataSource = new HashSet<>();
//        DataSourceAnomalyTypePair dataSourceAnomalyTypePair = new DataSourceAnomalyTypePair();
//        dataSourceAnomalyTypePair.setAnomalyType("many_login");
//        dataSourceAnomalyTypePair.setDataSource("login");
//
//        loginsAnomalyDataSource.add(dataSourceAnomalyTypePair);


        Evidence evidence = new Evidence();
        evidence.setDataEntitiesIds(Arrays.asList("login"));
        evidence.setName("indicator_name");
        evidence.setTimeframe(EvidenceTimeframe.Daily);
        evidence.setStartDate(1501200000L);
        evidence.setEndDate(1501286400L);
        evidence.setMockId("1");
        evidence.setScore(70);
        evidence.setSeverity(Severity.Low);
        evidence.setAnomalyType("anomalyType1");
        evidence.setAnomalyTypeFieldName("anomalyType1FieldName");
        evidence.setAnomalyValue("5");
        evidence.setEntityName("Beni@Burger");
        evidence.setEntityType(EntityType.User);
        evidence.setNumOfEvents(3);
        evidence.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);

        Evidence evidence2 = new Evidence();
        evidence2.setDataEntitiesIds(Arrays.asList("login"));
        evidence2.setName("indicator_name");
        evidence2.setTimeframe(EvidenceTimeframe.Daily);
        evidence2.setStartDate(1501200000L);
        evidence2.setEndDate(1501286400L);
        evidence2.setMockId("2");
        evidence2.setScore(70);
        evidence2.setSeverity(Severity.Low);
        evidence2.setAnomalyType("anomalyType1");
        evidence2.setAnomalyTypeFieldName("anomalyType1FieldName");
        evidence2.setAnomalyValue("50");
        evidence2.setEntityName("Beni@Burger");
        evidence2.setEntityType(EntityType.User);
        evidence2.setNumOfEvents(3);
        evidence2.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);

        Evidence evidence3 = new Evidence();
        evidence3.setDataEntitiesIds(Arrays.asList("login"));
        evidence3.setName("indicator_name");
        evidence3.setTimeframe(EvidenceTimeframe.Daily);
        evidence3.setStartDate(1500940800L);
        evidence3.setEndDate(1501027200L);
        evidence3.setMockId("3");
        evidence3.setScore(77);
        evidence3.setSeverity(Severity.Low);
        evidence3.setAnomalyType("anomalyType1");
        evidence3.setAnomalyTypeFieldName("anomalyType1FieldName");
        evidence3.setAnomalyValue("2");
        evidence3.setEntityName("Beni@Burger");
        evidence3.setEntityType(EntityType.User);
        evidence3.setNumOfEvents(3);
        evidence3.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);


        demoBuilder = new DemoBuilder(users,null,Arrays.asList(evidence,evidence2,evidence3));
        mockDemoEvidencesService = new MockDemoEvidencesServiceImpl(demoBuilder);


    }

    @Test
    public void testFindId(){
        Evidence indicator= mockDemoEvidencesService.findById("2");
        Assert.assertEquals("50", indicator.getAnomalyValue());
    }

    @Test
    public void testFindByNotExistingId(){
        Evidence indicator= mockDemoEvidencesService.findById("10");
        Assert.assertNull(indicator);

    }


    @Test
    public void testCount(){
        long count= mockDemoEvidencesService.count(1500854400L,1501027200L);
        Assert.assertEquals(1,count);

    }

    @Test
    public void testCountNotInRange(){
        long count= mockDemoEvidencesService.count(1200854400L,1301027200L);
        Assert.assertEquals(0,count);

    }

    @Test
    public void testIndicatorType(){
        DemoBuilder demoBuilder2 = new DemoBuilder();
        Evidence e = demoBuilder2.getIndicators().get(0);
        Assert.assertEquals("Multiple Failed Authentications",e.getName());
        Assert.assertEquals("Multiple Failed Authentications",e.getAnomalyType());
        Assert.assertEquals("multiple_failed_authentications",e.getAnomalyTypeFieldName());
        Assert.assertEquals(EvidenceType.AnomalyAggregatedEvent,e.getEvidenceType());

    }
}
