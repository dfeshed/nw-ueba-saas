package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.DataEntity;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by avivs on 21/01/16.
 */

public class EvidenceEmailPrettifierTest {

    @Mock
    private DataEntitiesConfig dataEntitiesConfig;

    @InjectMocks
    private EvidenceEmailPrettifier evidenceEmailPrettifier;

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

    @SuppressWarnings("resource")
    @BeforeClass
    public static void setUpClass() {
//        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/fortscale-services-email-prettifier.xml");
    }

    private Evidence createNewEvidence () {
        return new Evidence(entityType, entityTypeFieldName, entityName, evidenceType, startDate, endDate,
                anomalyTypeFieldName, anomalyValue, dataEntitiesIds, score, severity, totalAmountOfEvents, timeframe);
    }


    @Before
    public void setUpInternal() {
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

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testPrettifyReturnsDecoratedEvidenceInstance() throws Exception {
        Assert.isInstanceOf(EmailEvidenceDecorator.class, evidenceEmailPrettifier.prettify(createNewEvidence()));
    }

    @Test
    public void testPrettifyDataSource() throws Exception {
        // Mock entity
        Evidence evidence = createNewEvidence();
        // Mock dataEntity
        DataEntity dataEntity = new DataEntity();
        dataEntity.setName("Kerberose");
        // Mock dataEntitiesConfig
        when(dataEntitiesConfig.getLogicalEntity(evidence.getDataEntitiesIds().get(0))).thenReturn(dataEntity);

        // Test evidence
        EmailEvidenceDecorator emailEvidence = evidenceEmailPrettifier.prettify(evidence);
        assertEquals("Kerberose", emailEvidence.getDataSource());
    }
}