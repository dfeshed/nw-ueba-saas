package com.rsa.netwitness.presidio.automation.test.adapter.mapping;


import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.file.AdapterFileStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.AdapterFileStoredDataRepository;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class})
public class FileMappingTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private AdapterFileStoredDataRepository inputRawRepository;


    @Test(dataProviderClass = ExpectedFileEvents.class, dataProvider = "expectedEvents")
    public void testEvent(String scenarioName, AdapterFileStoredData expectedAdapterFileStoredData) {
        testMapping(expectedAdapterFileStoredData);
    }


    private void testMapping(AdapterFileStoredData expectedFileStoredData) {
        AdapterFileStoredData actualAdapterActiveDirectoryStoredData = inputRawRepository.findByEventId(expectedFileStoredData.getEventId());
        assertEventFields(actualAdapterActiveDirectoryStoredData, expectedFileStoredData);
    }

    private void assertEventFields(AdapterFileStoredData actualEvent, AdapterFileStoredData expectedEvent) {
        assertThat(actualEvent).hasFieldOrPropertyWithValue("eventId", expectedEvent.getEventId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("userId", expectedEvent.getUserId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("operationType", expectedEvent.getOperationType());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("result", expectedEvent.getResult());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("userName", expectedEvent.getUserName());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("srcFilePath", expectedEvent.getSrcFilePath());
// Drive shared field does not exist in 11.2
//        assertThat(actualEvent, hasProperty("isSrcDriveShared", is(expectedEvent.isSrcDriveShared())));
        assertThat(actualEvent).hasFieldOrPropertyWithValue("srcMachineId", expectedEvent.getSrcMachineId());
        assertThat(actualEvent).hasFieldOrPropertyWithValue("dstMachineName", expectedEvent.getDstMachineName());
    }


}
