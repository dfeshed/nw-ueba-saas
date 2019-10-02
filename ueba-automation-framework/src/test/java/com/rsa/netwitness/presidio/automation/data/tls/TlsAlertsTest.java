package com.rsa.netwitness.presidio.automation.data.tls;

import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsIndicator;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import presidio.data.domain.event.network.NetworkEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TlsAlertsTest {

    TlsAlert alert;
    List<NetworkEvent> events;

    @BeforeTest
    public void before() {
        TlsAlerts data = new TlsAlerts(3,1);
        alert = data.alerts.get().get(0);
        events = alert.getIndicators().stream().flatMap(TlsIndicator::getEvents).collect(Collectors.toList());
        assertThat(events).isNotNull().isNotEmpty();
    }

    @Test
    public void FqdnTest() {
        Map<String, List<NetworkEvent>> eventsByIndicator = new LinkedHashMap<>();

        for (TlsIndicator indicator :  alert.getIndicators()) {
            eventsByIndicator.put(indicator.name, indicator.getEvents().collect(Collectors.toList()));
        }

        System.out.println("here");

    }
}