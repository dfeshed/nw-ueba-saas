package com.rsa.netwitness.presidio.automation.data.tls;

import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsIndicator;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import presidio.data.domain.event.network.TlsEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TlsAlertsTest {

    TlsAlert alert;
    List<TlsEvent> events;

    @BeforeTest
    public void before() {
        TlsAlerts tlsAlerts = new TlsAlerts(3,1);
        alert = tlsAlerts.get().get(0);
    }

    @Test
    public void FqdnTest() {
        Map<String, List<TlsEvent>> eventsByIndicator = new LinkedHashMap<>();

        for (TlsIndicator indicator :  alert.getIndicators()) {
            eventsByIndicator.put(indicator.name, indicator.generateEvents());
        }

        for (TlsIndicator indicator :  alert.getIndicators()) {
            eventsByIndicator.put(indicator.name, indicator.generateEvents());
        }


        System.out.println("here");

    }
}