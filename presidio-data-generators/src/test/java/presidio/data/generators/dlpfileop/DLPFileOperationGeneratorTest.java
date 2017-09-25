package presidio.data.generators.dlpfileop;


import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.event.dlpfile.DLPFileOperation;
import presidio.data.generators.fileentity.FileSizeIncrementalGenerator;

public class DLPFileOperationGeneratorTest {

    /***
     * Test DLPFileOperationGenerator with default fields generators
     */
    @Test
    public void FileGeneratorDefaultTest() {
        String  expected_source_file         = "File.jar";
        String  expected_destination_file    = "File.jar";
        String  expected_source_path         = "/usr/someuser/somesubdir/1/";
        String  expected_destination_path    = "/usr/someuser/somesubdir/1/";
        long    expected_size                  = 5242880;
        String  expected_type                 = DEFAULT_EVENT_TYPE.FILE_MOVE.value;

        DLPFileOperationGenerator generator = new DLPFileOperationGenerator();
        DLPFileOperation f = generator.getNext();

        Assert.assertEquals (f.getSource_file_name(),expected_source_file);
        Assert.assertEquals (f.getDestination_file_name(),expected_destination_file);
        Assert.assertEquals (f.getSource_path(),expected_source_path);
        Assert.assertEquals (f.getDestination_path(),expected_destination_path);
        Assert.assertEquals (f.getFile_size(),expected_size);
        Assert.assertEquals (f.getEvent_type(),expected_type);
    }

    /***
     * Test DLPFileOperationGenerator with custom FileSizeIncrementalGenerator generators
     */
    @Test
    public void FileOperationGeneratorCustomTest() {
        long expected_size = 10;
        String expected_type = DEFAULT_EVENT_TYPE.FILE_DELETE.value;

        OperationTypeCyclicGenerator eventTypeGen = new OperationTypeCyclicGenerator(new String[] {DEFAULT_EVENT_TYPE.FILE_DELETE.value});
        FileSizeIncrementalGenerator fileSizeGen = new FileSizeIncrementalGenerator(10,100,50);

        // Custom Event Type
        DLPFileOperationGenerator generator = new DLPFileOperationGenerator();

        generator.setFileSizeGenerator(fileSizeGen);
        generator.setEventTypeGenerator(eventTypeGen);

        DLPFileOperation f = generator.getNext();

        Assert.assertEquals (f.getFile_size(),expected_size);
        Assert.assertEquals (f.getEvent_type(),expected_type);
    }
}
