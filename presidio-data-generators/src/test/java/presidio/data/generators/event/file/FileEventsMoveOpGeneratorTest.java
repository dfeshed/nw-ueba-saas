package presidio.data.generators.event.file;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.fileop.FileOperationGeneratorTemplateFactory;
import presidio.data.generators.fileop.IFileOperationGenerator;

import java.util.List;

public class FileEventsMoveOpGeneratorTest {

    List<FileEvent> events;

    /** Default values:
     * time: 8:00 to 16:00, every 10 min, 30 to 1 days back
     * dataSource: "DefaultDS"
     * file operation type: MOVE - src and dst file exist
     * eventId - unique
     * event count: 1392 = 6 per hour * 8 work hours * 29 days
     */

    @Before
    public void prepare() throws GeneratorException {
        FileEventsGenerator generator = new FileEventsGenerator();

        FileOperationGeneratorTemplateFactory opGenTemplateFactory = new FileOperationGeneratorTemplateFactory();
        IFileOperationGenerator opGen = opGenTemplateFactory.createMoveFileOperationsGenerator();
        generator.setFileOperationGenerator(opGen);
        events = generator.generate();
    }

    @Test
    public void EventsCountTest () {
        Assert.assertEquals(events.size(), 1392);
    }

    @Test
    public void DataSourceTest () {
        Assert.assertEquals("File System", events.get(0).getDataSource());
    }

    @Test
    public void ResultsTest () {
        // All should succeed
        boolean anySuccess = true; // expect to remain "true"
        for (FileEvent ev : events) {
            anySuccess = anySuccess && ev.getFileOperation().getOperationResult().equalsIgnoreCase("SUCCESS");
        }
        Assert.assertTrue(anySuccess);
    }

    @Test
    public void ResultCodeTest () {
        Assert.assertEquals(6, events.get(0).getFileOperation().getOperationResultCode().length());
    }

    @Test
    public void SrcAndDestinationFileFieldsTest () {
        FileEntity srcFile = events.get(10).getFileOperation().getSourceFile();
        FileEntity destFile = events.get(10).getFileOperation().getDestinationFile();
        Assert.assertNotNull(srcFile.getAbsoluteFilePath());
        Assert.assertNotNull(srcFile.getFileName());
        Assert.assertNotNull(srcFile.getFilePath());
        Assert.assertNotNull(srcFile.getFileSize());
        Assert.assertNotNull(destFile.getAbsoluteFilePath());
        Assert.assertNotNull(destFile.getFileName());
        Assert.assertNotNull(destFile.getFilePath());
        Assert.assertNotNull(destFile.getFileSize());
    }
}
