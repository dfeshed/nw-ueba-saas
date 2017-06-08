package presidio.input.core.services.impl;

import fortscale.domain.core.AbstractAuditableDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.store.input.ADEInputRecord;
import presidio.input.core.spring.InputCoreConfiguration;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexp on 07-Jun-17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InputCoreConfiguration.class)
public class InputExecutionServiceTest {

    @Autowired
    InputExecutionServiceImpl processService;

    @Test
    public void testConverter(){
        List<ADEInputRecord> adeRecords = new ArrayList<>();
        List<AbstractAuditableDocument> enrichedDocuments = new ArrayList<>();
        DlpFileDataDocument dlpFile = new DlpFileDataDocument(("2017-06-06 10:10:10,executing_application,hostname," +
                "first_name,dddd,last_name,username,malware_scan_result,event_id,source_ip,false,false,destination_path," +
                "destination_file_name,2.23,source_path,source_file_name,source_drive_type,destination_drive_type," +
                "event_type").split(","));
        AbstractAuditableDocument inputDocument = new DlpFileEnrichedDocument(dlpFile, "normalizedUserName", "normalizedMachineName");
//        inputDocument.
        enrichedDocuments.add(inputDocument);
        processService.convert(enrichedDocuments, adeRecords);
    }

}
