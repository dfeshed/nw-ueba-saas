package presidio.input.core.services.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = InputCoreConfiguration.class)
@RunWith(JUnit4.class)
public class DlpFileConverterTest {

    @Test
    public void testConverter_valid(){
        DlpFileConverter dlpFileConverter = new DlpFileConverter();
        DlpFileDataDocument inputRecord = new DlpFileDataDocument(("2017-06-06T10:10:10.00Z,executing_application,hostname," +
                "first_name,dddd,last_name,username,malware_scan_result,event_id,source_ip,false,false,destination_path," +
                "destination_file_name,2.23,source_path,source_file_name,source_drive_type,destination_drive_type," +
                "event_type").split(","));
        DlpFileEnrichedDocument inputEnrichedRecord = new DlpFileEnrichedDocument(inputRecord, "normalizedUsername", "normalizedSrcMachine");
        EnrichedRecord adeRecord = dlpFileConverter.convert(inputEnrichedRecord);
        assertRecords(inputEnrichedRecord, (EnrichedDlpFileRecord) adeRecord);
    }

    private void assertRecords(DlpFileEnrichedDocument inputEnrichedRecord, EnrichedDlpFileRecord adeRecord) {
        Assert.assertEquals(inputEnrichedRecord.getDateTime(), adeRecord.getDate_time());
        Assert.assertEquals(inputEnrichedRecord.getNormalizedUsername(), adeRecord.getNormalized_username());
        Assert.assertEquals(inputEnrichedRecord.getNormalizedSrcMachine(), adeRecord.getNormalized_src_machine());
        Assert.assertEquals(inputEnrichedRecord.getSourcePath(), adeRecord.getSource_path());
        Assert.assertEquals(inputEnrichedRecord.getSourceFileName(), adeRecord.getSource_file_name());
        Assert.assertEquals(inputEnrichedRecord.getSourceDriveType(), adeRecord.getSource_drive_type());
        Assert.assertEquals(inputEnrichedRecord.getDestinationPath(), adeRecord.getDestination_path());
        Assert.assertEquals(inputEnrichedRecord.getDestinationFileName(), adeRecord.getDestination_file_name());
        Assert.assertEquals(inputEnrichedRecord.getDestinationDriveType(), adeRecord.getDestination_drive_type());
        Assert.assertEquals(inputEnrichedRecord.getFileSize().doubleValue(), adeRecord.getFile_size(), 0d);
        Assert.assertEquals(inputEnrichedRecord.getEventType(), adeRecord.getEvent_type());
        Assert.assertEquals(inputEnrichedRecord.getWasBlocked(), adeRecord.isWas_blocked());
        Assert.assertEquals(inputEnrichedRecord.getWasClassified(), adeRecord.isWas_classified());
        Assert.assertEquals(inputEnrichedRecord.getMalwareScanResult(), adeRecord.getMalware_scan_result());
        Assert.assertEquals(inputEnrichedRecord.getExecutingApplication(), adeRecord.getExecuting_application());
    }
}
