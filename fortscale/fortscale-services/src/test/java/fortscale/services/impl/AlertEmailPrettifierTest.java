package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.services.LocalizationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by avivs on 20/01/16.
 */
public class AlertEmailPrettifierTest {

    @Mock
    private EvidenceEmailPrettifier evidenceEmailPrettifier;

    @Mock
    LocalizationService localizationService;

    @InjectMocks
    private AlertEmailPrettifier alertEmailPrettifier;

    private String name;
    private long startDate;
    private long endDate;
    private EntityType entityType;
    private String entityName;
    private List<Evidence> evidences;
    private int evidenceSize;
    private int score;
    private Severity severity;
    private AlertStatus alertStatus;
    private AlertFeedback feedback;
    private String comment;
    private String entityId;


    private EntityType evidenceEntityType;
    private String evidenceEntityTypeFieldName;
    private String evidenceEntityName;
    private EvidenceType evidenceEvidenceType;
    private long evidenceStartDate;
    private long evidenceEndDate;
    private String evidenceAnomalyTypeFieldName;
    private String evidenceAnomalyValue;
    private List<String> evidenceDataEntitiesIds;
    private Integer evidenceEcore;
    private Severity evidenceSeverity;
    private Integer evidenceTotalAmountOfEvents;
    private EvidenceTimeframe evidenceTimeframe;

    private Alert createNewAlert() {
        return new Alert(name, startDate, endDate, entityType, entityName, evidences, evidenceSize, score, severity,
                alertStatus, feedback, comment, entityId, AlertTimeframe.Daily,0.0, true);
    }

    private Evidence createNewEvidence () {
        return new Evidence(evidenceEntityType, evidenceEntityTypeFieldName, evidenceEntityName, evidenceEvidenceType,
                evidenceStartDate, evidenceEndDate, evidenceAnomalyTypeFieldName, evidenceAnomalyValue,
                evidenceDataEntitiesIds, evidenceEcore, evidenceSeverity, evidenceTotalAmountOfEvents,
                evidenceTimeframe);
    }

    @Before
    public void setUpInternal() {
        MockitoAnnotations.initMocks(this);

        name = "Suspicious User Activity";
        startDate = 1454407200000L;
        endDate = 1455998400000L;
        entityType = EntityType.User;
        entityName = "some@user.name";
        evidences = new ArrayList<>();
        evidenceSize = 0;
        score = 100;
        severity = Severity.Critical;
        alertStatus = AlertStatus.Open;
        feedback = AlertFeedback.None;
        comment = "";
        entityId = "";


        evidenceEntityType = EntityType.User;
        evidenceEntityTypeFieldName = "normalized_username";
        evidenceEntityName = "some@user.name";
        evidenceEvidenceType = EvidenceType.AnomalySingleEvent;
        evidenceStartDate = 1451642400000L;
        evidenceEndDate = 1451642400000L;
        evidenceAnomalyTypeFieldName = "event_time";
        evidenceAnomalyValue = "2016-01-01 10:00:00.0";
        evidenceDataEntitiesIds = new ArrayList<>();
        evidenceDataEntitiesIds.add("kerberos_logins");
        evidenceEcore = 100;
        evidenceSeverity = Severity.Critical;
        evidenceTotalAmountOfEvents = 5;
        evidenceTimeframe = EvidenceTimeframe.Daily;


        //Return the alert name as the localized name
        Mockito.when(localizationService.getAlertName(Mockito.any(Alert.class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ((Alert) args[0]).getName();
            }
        });
    }

    @Test
    public void testPrettifyReturnsDecoratedAlertInstance() throws Exception {
        Assert.isInstanceOf(EmailAlertDecorator.class, alertEmailPrettifier.prettify(createNewAlert()));
    }

    @Test
    public void testAlertStartDateLong() throws Exception {
        EmailAlertDecorator emailAlert = alertEmailPrettifier.prettify(createNewAlert());
        assertEquals("02/02/16 10:00:00", emailAlert.getStartDateLong());
    }

    @Test
    public void testAlertName() throws Exception {
        EmailAlertDecorator emailAlert = alertEmailPrettifier.prettify(createNewAlert());
        assertEquals("Suspicious User Activity (Daily)", emailAlert.getName());
    }

    @Test
    public void testAlertStartDateShort() throws Exception {
        EmailAlertDecorator emailAlert = alertEmailPrettifier.prettify(createNewAlert());
        assertEquals("Tue, 02/02/16", emailAlert.getStartDateShort());
    }

    @Test
    public void testAlertEndDateLong() throws Exception {
        EmailAlertDecorator emailAlert = alertEmailPrettifier.prettify(createNewAlert());
        assertEquals("02/20/16 20:00:00", emailAlert.getEndDateLong());
    }

    @Test
    public void testAlertEndDateShort() throws Exception {
        EmailAlertDecorator emailAlert = alertEmailPrettifier.prettify(createNewAlert());
        assertEquals("Sat, 02/20/16", emailAlert.getEndDateShort());
    }

    @Test
    public void testEmailEvidencesPopulated() throws Exception {
        // Add 3 indicators
        Evidence evidence = createNewEvidence();

        for (int i=0; i<3; i+=1) {
            evidences.add(createNewEvidence());
        }

        Alert alert = createNewAlert();

        when(evidenceEmailPrettifier.prettify(any(Evidence.class))).thenReturn(new EmailEvidenceDecorator(evidence));

        EmailAlertDecorator emailAlert = alertEmailPrettifier.prettify(alert);
        Assert.isInstanceOf(EmailEvidenceDecorator.class, emailAlert.getEmailEvidences().get(0));
        Assert.isInstanceOf(EmailEvidenceDecorator.class, emailAlert.getEmailEvidences().get(1));
        Assert.isInstanceOf(EmailEvidenceDecorator.class, emailAlert.getEmailEvidences().get(2));

    }

    @Test
    public void testEmailEvidenceSorting() throws Exception {
        // Add 3 indicators
        Evidence evidence1 = createNewEvidence();
        evidence1.setStartDate(1451642400000L);
        Evidence evidence2 = createNewEvidence();
        evidence2.setStartDate(1651642400000L);
        Evidence evidence3 = createNewEvidence();
        evidence3.setStartDate(1551642400000L);
        evidences.add(evidence1);
        evidences.add(evidence2);
        evidences.add(evidence3);
        Alert alert = createNewAlert();

        //validate
        when(evidenceEmailPrettifier.prettify(evidence1)).thenReturn(new EmailEvidenceDecorator(evidence1));
        when(evidenceEmailPrettifier.prettify(evidence2)).thenReturn(new EmailEvidenceDecorator(evidence2));
        when(evidenceEmailPrettifier.prettify(evidence3)).thenReturn(new EmailEvidenceDecorator(evidence3));

        EmailAlertDecorator emailAlert = alertEmailPrettifier.prettify(alert);
        assertEquals(evidence2.getStartDate(), emailAlert.getEmailEvidences().get(0).getStartDate());
        assertEquals(evidence3.getStartDate(), emailAlert.getEmailEvidences().get(1).getStartDate());
        assertEquals(evidence1.getStartDate(), emailAlert.getEmailEvidences().get(2).getStartDate());
    }
}