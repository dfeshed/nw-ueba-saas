package fortscale.domain.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by avivs on 21/01/16.
 */
public class EmailEvidenceDecoratorTest {

    private EntityType entityType;
    private String entityTypeFieldName;
    private String entityName;
    private EvidenceType evidenceType;
    private long startDate;
    private long endDate;
    private String anomalyTypeFieldName;
    private String anomalyValue;
    private List<String> dataEntitiesIds;
    private Integer score;
    private Severity severity;
    private Integer totalAmountOfEvents;
    private EvidenceTimeframe timeframe;

    private Evidence createNewEvidence () {
        return new Evidence(entityType, entityTypeFieldName, entityName, evidenceType, startDate, endDate,
                anomalyTypeFieldName, anomalyValue, dataEntitiesIds, score, severity, totalAmountOfEvents, timeframe);
    }

    @Before
    public void setUp() throws Exception {
        entityType = EntityType.User;
        entityTypeFieldName = "normalized_username";
        entityName = "some@user.name";
        evidenceType = EvidenceType.AnomalySingleEvent;
        startDate = 1451642400;
        endDate = 1451642400;
        anomalyTypeFieldName = "event_time";
        anomalyValue = "2016-01-01 10:00:00.0";
        dataEntitiesIds = new ArrayList<>();
        dataEntitiesIds.add("kerberos_logins");
        score = 100;
        severity = Severity.Critical;
        totalAmountOfEvents = 5;
        timeframe = EvidenceTimeframe.Daily;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testConstructorMakesInstance() {
        EmailEvidenceDecorator decoratedEvidence= new EmailEvidenceDecorator(createNewEvidence());
        Assert.isInstanceOf(EmailEvidenceDecorator.class, decoratedEvidence);
    }

}