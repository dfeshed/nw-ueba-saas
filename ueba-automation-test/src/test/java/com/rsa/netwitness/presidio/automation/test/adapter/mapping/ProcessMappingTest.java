package com.rsa.netwitness.presidio.automation.test.adapter.mapping;


import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.process.AdapterProcessStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.AdapterProcessStoredDataRepository;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class})
public class ProcessMappingTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private AdapterProcessStoredDataRepository inputRawRepository;


    @Test (dataProviderClass = ExpectedProcessEvents.class, dataProvider = "expectedEvents")
    public void testEvent(String scenarioName, AdapterProcessStoredData expectedStoredData) {
        testMapping(expectedStoredData);
    }


    private void testMapping(AdapterProcessStoredData expectedStoredData) {
        AdapterProcessStoredData actualStoredData  = inputRawRepository.findByEventId(expectedStoredData.getEventId());
        assertEventFields(actualStoredData, expectedStoredData);
    }

    private void assertEventFields(AdapterProcessStoredData actualEvent, AdapterProcessStoredData expectedEvent ) {
        assertThat(actualEvent).hasFieldOrPropertyWithValue("eventId", expectedEvent.getEventId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("userId", expectedEvent.getUserId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("operationType", expectedEvent.getOperationType());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("userName", expectedEvent.getUserName());
    }


}
