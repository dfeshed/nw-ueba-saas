package presidio.input.core.services.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.data.AdeDataService;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.sdk.api.services.PresidioInputPersistencyService;

@RunWith(SpringRunner.class)
public class InputExecutionServiceTest {

    @MockBean
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @MockBean
    private AdeDataService adeDataService;

    @MockBean
    private OutputDataServiceSDK outputDataServiceSDK;

    private InputExecutionServiceImpl processService = new InputExecutionServiceImpl(presidioInputPersistencyService,
            adeDataService,
            outputDataServiceSDK);

    @Test
    public void testConverter() {
//        List<AbstractAuditableDocument> inputRecords = new ArrayList<>();
//        DlpFileDataDocument dlpFile = new DlpFileDataDocument(("2017-06-06T10:10:10.00Z,executing_application,hostname," +
//                "first_name,dddd,last_name,username,malware_scan_result,event_id,source_ip,false,false,destination_path," +
//                "destination_file_name,2.23,source_path,source_file_name,source_drive_type,destination_drive_type," +
//                "event_type").split(","));
//        AbstractAuditableDocument inputDocument = new DlpFileEnrichedDocument(dlpFile, "normalizedUserName", "normalizedMachineName");
//        inputRecords.add(inputDocument);
//        List<EnrichedRecord> adeRecords = processService.convert(inputRecords, new DlpFileConverter());
//        Assert.assertEquals(inputRecords.size(), adeRecords.size());
    }
}
