package presidio.input.core.services.converters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.sdk.api.domain.DlpFileDataDocument;
import presidio.sdk.api.domain.DlpFileEnrichedDocument;

@RunWith(SpringRunner.class)
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
        Assert.assertEquals(inputEnrichedRecord.getDateTime(), adeRecord.getStartInstant());
        Assert.assertEquals(inputEnrichedRecord.getNormalizedUsername(), adeRecord.getNormalizedUsername());
        Assert.assertEquals(inputEnrichedRecord.getNormalizedSrcMachine(), adeRecord.getNormalizedSrcMachine());
        Assert.assertEquals(inputEnrichedRecord.getSourcePath(), adeRecord.getSourcePath());
        Assert.assertEquals(inputEnrichedRecord.getSourceFileName(), adeRecord.getSourceFileName());
        Assert.assertEquals(inputEnrichedRecord.getSourceDriveType(), adeRecord.getSourceDriveType());
        Assert.assertEquals(inputEnrichedRecord.getDestinationPath(), adeRecord.getDestinationPath());
        Assert.assertEquals(inputEnrichedRecord.getDestinationFileName(), adeRecord.getDestinationFileName());
        Assert.assertEquals(inputEnrichedRecord.getDestinationDriveType(), adeRecord.getDestinationDriveType());
        Assert.assertEquals(inputEnrichedRecord.getFileSize().doubleValue(), adeRecord.getFileSize(), 0d);
        Assert.assertEquals(inputEnrichedRecord.getEventType(), adeRecord.getOperationType());
        Assert.assertEquals(inputEnrichedRecord.getWasBlocked(), adeRecord.isWasBlocked());
        Assert.assertEquals(inputEnrichedRecord.getWasClassified(), adeRecord.isWasClassified());
        Assert.assertEquals(inputEnrichedRecord.getMalwareScanResult(), adeRecord.getMalwareScanResult());
        Assert.assertEquals(inputEnrichedRecord.getExecutingApplication(), adeRecord.getExecutingApplication());
    }
}
