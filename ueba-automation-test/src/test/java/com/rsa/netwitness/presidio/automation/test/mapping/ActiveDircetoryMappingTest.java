package com.rsa.netwitness.presidio.automation.test.mapping;

import com.rsa.netwitness.presidio.automation.domain.activedirectory.AdapterActiveDirectoryStoredData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.AdapterActiveDirectoryStoredDataRepository;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class})
public class ActiveDircetoryMappingTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private AdapterActiveDirectoryStoredDataRepository inputRawRepository;


    @Test (dataProviderClass = ExpectedActiveDirectoryEvents.class, dataProvider = "expectedEvents")
    public void testEvent(String scenarioName, AdapterActiveDirectoryStoredData expectedAdapterActiveDirectoryStoredData) {
        testMapping(expectedAdapterActiveDirectoryStoredData);
    }


    private void testMapping(AdapterActiveDirectoryStoredData expectedAdapterActiveDirectoryStoredData) {
        AdapterActiveDirectoryStoredData actualAdapterActiveDirectoryStoredData  = inputRawRepository.findByEventId(expectedAdapterActiveDirectoryStoredData.getEventId());
        assertEventFields(actualAdapterActiveDirectoryStoredData, expectedAdapterActiveDirectoryStoredData);
    }

    private void assertEventFields(AdapterActiveDirectoryStoredData actualEvent, AdapterActiveDirectoryStoredData expectedEvent ) {
        assertThat(actualEvent).hasFieldOrPropertyWithValue("eventId", expectedEvent.getEventId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("userId", expectedEvent.getUserId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("operationType", expectedEvent.getOperationType());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("result", expectedEvent.getResult());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("userName", expectedEvent.getUserName());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("objectId", expectedEvent.getObjectId());
    }

}
