package com.rsa.netwitness.presidio.automation.test.adapter.mapping;


import com.rsa.netwitness.presidio.automation.domain.authentication.AdapterAuthenticationStoredData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.AdapterAuthenticationStoredDataRepository;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class})
public class AuthenticationMappingTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private AdapterAuthenticationStoredDataRepository inputRawRepository;


    @Test (dataProviderClass = ExpectedAuthenticationEvents.class, dataProvider = "expectedEvents")
    public void testEvent(String scenarioName, AdapterAuthenticationStoredData expectedAdapterAuthenticationStoredData) {
        testMapping(expectedAdapterAuthenticationStoredData);
    }


    private void testMapping( AdapterAuthenticationStoredData expectedAdapterAuthenticationStoredData) {
        AdapterAuthenticationStoredData actualAdapterAuthenticationStoredData  = inputRawRepository.findByEventId(expectedAdapterAuthenticationStoredData.getEventId());
        assertEventFields(actualAdapterAuthenticationStoredData, expectedAdapterAuthenticationStoredData);
    }

    private void assertEventFields(AdapterAuthenticationStoredData actualEvent, AdapterAuthenticationStoredData expectedEvent ) {
        assertThat(actualEvent).hasFieldOrPropertyWithValue("eventId", expectedEvent.getEventId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("userId", expectedEvent.getUserId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("operationType", expectedEvent.getOperationType());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("result", expectedEvent.getResult());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("userName", expectedEvent.getUserName());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("srcMachineId", expectedEvent.getSrcMachineId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("srcMachineName", expectedEvent.getSrcMachineName());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("userDisplayName", expectedEvent.getUserDisplayName());
    }


}
