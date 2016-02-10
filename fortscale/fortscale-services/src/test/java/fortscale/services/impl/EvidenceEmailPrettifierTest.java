package fortscale.services.impl;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataentity.DataEntity;
import fortscale.domain.core.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    @Mock
    private ApplicationConfigurationServiceImpl applicationConfigurationService;

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
        startDate = 1451642400000L;
        endDate = 1451642400000L;
        anomalyTypeFieldName = "event_time";
        anomalyValue = "2016-01-01 10:00:00.0";
        dataEntitiesIds = new ArrayList<>();
        dataEntitiesIds.add("kerberos_logins");
        score = 100;
        severity = Severity.Critical;
        totalAmountOfEvents = 5;
        timeframe = EvidenceTimeframe.Daily;

        MockitoAnnotations.initMocks(this);

        try {
            when(dataEntitiesConfig.getLogicalEntity(anyString())).thenReturn(new DataEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Test
    public void testPrettyNameForAnomalySingleEvent() throws Exception {
        ApplicationConfiguration evidenceNameMessage = new ApplicationConfiguration();
        evidenceNameMessage.setValue("Activity Time Anomaly");
        when(applicationConfigurationService.getApplicationConfigurationByKey("messages.en.evidence.event_time"))
                .thenReturn(evidenceNameMessage);
        EmailEvidenceDecorator emailEvidence = evidenceEmailPrettifier.prettify(createNewEvidence());
        assertEquals("Activity Time Anomaly (Daily)", emailEvidence.getName());
    }

    @Test
    public void testPrettyNameForAnomalyAggregatedEvent() throws Exception {
        ApplicationConfiguration evidenceNameMessage = new ApplicationConfiguration();
        evidenceNameMessage.setValue("High Number of Authentications");
        when(applicationConfigurationService.getApplicationConfigurationByKey("messages.en.evidence.number_of_successful_kerberos_logins_hourly"))
                .thenReturn(evidenceNameMessage);

        anomalyTypeFieldName = "number_of_successful_kerberos_logins_hourly";
        timeframe = EvidenceTimeframe.Hourly;
        EmailEvidenceDecorator emailEvidence = evidenceEmailPrettifier.prettify(createNewEvidence());
        assertEquals("High Number of Authentications (Hourly)", emailEvidence.getName());
    }


    @Test
    public void testAnomalyValueForDataBucket() throws Exception {
        anomalyTypeFieldName = "data_bucket";
        anomalyValue = "1024";
        EmailEvidenceDecorator emailEvidence = evidenceEmailPrettifier.prettify(createNewEvidence());
        assertEquals("1.0 KB/s", emailEvidence.getPrettifiedAnomalyValue());
    }

    @Test
    public void testAnomalyValueForEventTime() throws Exception {
        EmailEvidenceDecorator emailEvidence = evidenceEmailPrettifier.prettify(createNewEvidence());
        assertEquals("2016/01/01 10:00", emailEvidence.getPrettifiedAnomalyValue());
    }

    @Test
    public void testAnomalyValueForAggregatedEvent() throws Exception {
        evidenceType = EvidenceType.AnomalyAggregatedEvent;
        anomalyValue = "4.0";
        EmailEvidenceDecorator emailEvidence = evidenceEmailPrettifier.prettify(createNewEvidence());
        assertEquals("4", emailEvidence.getPrettifiedAnomalyValue());
    }

    @Test
    public void testStartDatePrettified() throws Exception {
        EmailEvidenceDecorator emailEvidence = evidenceEmailPrettifier.prettify(createNewEvidence());
        assertEquals("2016/01/01 10:00", emailEvidence.getPrettyStartDate());
    }
}