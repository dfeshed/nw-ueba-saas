package presidio.output.commons.services.alert;


import org.junit.Test;
import presidio.output.domain.records.alerts.AlertEnums.AlertSeverity;

import static org.junit.Assert.*;

/**
 * Created by efratn on 24/07/2017.
 */
public class AlertSeverityServiceTest {

    private AlertSeverityServiceImpl alertSeverityServiceImpl = new AlertSeverityServiceImpl(95D, 90D, 80D, 0D, 0D, 0D, 0D);


    @Test
    public void severityTest() {
        assertEquals(alertSeverityServiceImpl.getSeverity(0), AlertSeverity.LOW);
        assertEquals(alertSeverityServiceImpl.getSeverity(40), AlertSeverity.LOW);
        assertEquals(alertSeverityServiceImpl.getSeverity(70), AlertSeverity.LOW);
        assertEquals(alertSeverityServiceImpl.getSeverity(81), AlertSeverity.MEDIUM);
        assertEquals(alertSeverityServiceImpl.getSeverity(91), AlertSeverity.HIGH);
        assertEquals(alertSeverityServiceImpl.getSeverity(97), AlertSeverity.CRITICAL);
    }

}