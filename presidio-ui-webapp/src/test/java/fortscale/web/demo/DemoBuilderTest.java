package fortscale.web.demo;

import fortscale.domain.core.*;
import fortscale.web.demoservices.DemoBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

/**
 * Created by shays on 23/07/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class DemoBuilderTest {

    private DemoBuilder demoBuilder;

    @Before
    public void setUp(){
        demoBuilder = new DemoBuilder();
    }

    @Test
    public void testBuild(){

        Assert.assertTrue(demoBuilder.getAlerts().size()>0);
    }

    @Test
    public void loadUserAlertAndEvidencesBuild(){

        User user = demoBuilder.getUserByName("nheaseman6h");
        Assert.assertNotNull(user);
        Assert.assertEquals("nheaseman6h",user.getUsername());
        Assert.assertEquals(11,user.getAlertsCount());


        List<Alert> alerts= demoBuilder.getAlertsByUserName("asuggeyc");
        Assert.assertEquals(8,alerts.size());

        Alert a = alerts.get(6);
        //Multiple failed logons (Hourly),nheaseman6h,Jun 28 2017 06:00:00 GMT+0000,8,High
        Assert.assertEquals("Brute Force Attempt",a.getName());
        Assert.assertEquals(AlertTimeframe.Hourly,a.getTimeframe());
        Assert.assertEquals(AlertStatus.Open,a.getStatus());
        Assert.assertEquals(AlertFeedback.None,a.getFeedback());
        Assert.assertEquals(1509170400000L,a.getStartDate());
        Assert.assertEquals(1509174000000L,a.getEndDate());
        Assert.assertEquals(Severity.Critical,a.getSeverity());
        Assert.assertNotNull(a.getEvidences());
        Assert.assertEquals(4,a.getEvidences().size());
        Evidence e = a.getEvidences().get(0);

        Assert.assertEquals("asuggeyc",e.getEntityName());
        Assert.assertEquals("Multiple Failed Authentications",e.getAnomalyType());
        Assert.assertEquals("multiple_failed_authentications",e.getAnomalyTypeFieldName());
        Assert.assertEquals(1509170400000L,e.getStartDate().longValue());
        Assert.assertEquals(1509174000000L,e.getEndDate().longValue());

        Assert.assertEquals(Severity.Low,e.getSeverity());
        Assert.assertEquals("9",e.getAnomalyValue());
        Assert.assertArrayEquals(new String[]{"authentication"},e.getDataEntitiesIds().toArray());


    }

    @Test
    public void loadUserAlertAndEvidencesBuildScenario2(){

        User user = demoBuilder.getUserByName("ogreddeni");
        Assert.assertNotNull(user);
        Assert.assertEquals("ogreddeni",user.getUsername());
        Assert.assertEquals(9,user.getAlertsCount());


        List<Alert> alerts= demoBuilder.getAlertsByUserName("ogreddeni");
        Assert.assertEquals(9,alerts.size());

        Alert a = alerts.get(0);
        //Multiple failed logons (Hourly),nheaseman6h,Jun 28 2017 06:00:00 GMT+0000,8,High
        Assert.assertEquals("Snooping User",a.getName());
        Assert.assertEquals(AlertTimeframe.Hourly,a.getTimeframe());
        Assert.assertEquals(AlertStatus.Open,a.getStatus());
        Assert.assertEquals(AlertFeedback.None,a.getFeedback());
        Assert.assertEquals(1509087600000L,a.getStartDate());
        Assert.assertEquals(1509091200000L,a.getEndDate());
        Assert.assertEquals(Severity.Critical,a.getSeverity());
        Assert.assertNotNull(a.getEvidences());
        Assert.assertEquals(4,a.getEvidences().size());

        Evidence e = a.getEvidences().get(0);
        Assert.assertEquals("ogreddeni",e.getEntityName());
        Assert.assertEquals("Abnormal Computer Accessed Remotely",e.getAnomalyType());
        Assert.assertEquals("abnormal_computer_accessed_remotely",e.getAnomalyTypeFieldName());
        Assert.assertEquals("CORP-FS",e.getAnomalyValue());
        Assert.assertArrayEquals(new String[]{"authentication"},e.getDataEntitiesIds().toArray());

        e = a.getEvidences().get(1);
        Assert.assertEquals("ogreddeni",e.getEntityName());
        Assert.assertEquals("Abnormal Computer Accessed Remotely",e.getAnomalyType());
        Assert.assertEquals("abnormal_computer_accessed_remotely",e.getAnomalyTypeFieldName());
        Assert.assertEquals("ACME-FS",e.getAnomalyValue());
        Assert.assertArrayEquals(new String[]{"authentication"},e.getDataEntitiesIds().toArray());

        e = a.getEvidences().get(2);
        Assert.assertEquals("ogreddeni",e.getEntityName());
        Assert.assertEquals("Abnormal Computer Accessed Remotely",e.getAnomalyType());
        Assert.assertEquals("abnormal_computer_accessed_remotely",e.getAnomalyTypeFieldName());
        Assert.assertEquals("CRM-FS",e.getAnomalyValue());
        Assert.assertEquals(Severity.Low,e.getSeverity());
        Assert.assertArrayEquals(new String[]{"authentication"},e.getDataEntitiesIds().toArray());

        e = a.getEvidences().get(3);
        Assert.assertEquals("ogreddeni",e.getEntityName());
        Assert.assertEquals("Multiple Folder Open Events",e.getAnomalyType());
        Assert.assertEquals("multiple_folder_open_events",e.getAnomalyTypeFieldName());
        Assert.assertEquals("44",e.getAnomalyValue());
        Assert.assertEquals(Severity.Low,e.getSeverity());
        Assert.assertArrayEquals(new String[]{"file"},e.getDataEntitiesIds().toArray());




         a = alerts.get(1);
        //Multiple failed logons (Hourly),nheaseman6h,Jun 28 2017 06:00:00 GMT+0000,8,High
        Assert.assertEquals("Data Exfiltration",a.getName());
        Assert.assertEquals(AlertTimeframe.Hourly,a.getTimeframe());
        Assert.assertEquals(AlertStatus.Open,a.getStatus());
        Assert.assertEquals(AlertFeedback.None,a.getFeedback());
        Assert.assertEquals(1509094800000L,a.getStartDate());
        Assert.assertEquals(1509098400000L,a.getEndDate());
        Assert.assertEquals(Severity.Critical,a.getSeverity());
        Assert.assertNotNull(a.getEvidences());
        Assert.assertEquals(2,a.getEvidences().size());


        e = a.getEvidences().get(0);
        Assert.assertEquals("ogreddeni",e.getEntityName());
        Assert.assertEquals("Multiple Failed File Access Events",e.getAnomalyType());
        Assert.assertEquals("multiple_failed_file_access_events",e.getAnomalyTypeFieldName());
        Assert.assertEquals("215",e.getAnomalyValue());
        Assert.assertEquals(Severity.Low,e.getSeverity());
        Assert.assertArrayEquals(new String[]{"file"},e.getDataEntitiesIds().toArray());

        e = a.getEvidences().get(1);
        Assert.assertEquals("ogreddeni",e.getEntityName());
        Assert.assertEquals("Multiple File Delete Events",e.getAnomalyType());
        Assert.assertEquals("multiple_file_delete_events",e.getAnomalyTypeFieldName());
        Assert.assertEquals("741",e.getAnomalyValue());
        Assert.assertEquals(Severity.Low,e.getSeverity());
        Assert.assertArrayEquals(new String[]{"file"},e.getDataEntitiesIds().toArray());

    }
}
