package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.EventResult;
import fortscale.utils.transform.FolderPathByOperationTypeTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.spring.TransformerConfigTest;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.io.IOException;
import java.time.Instant;

@RunWith(SpringRunner.class)
@Import({TransformerConfigTest.class})
public class FolderPathByOperationTypeTransformerTest extends TransformerJsonTest {

    @Test
    public void testFolderPathTransformation_windows_FileOperation() throws IOException {
        String filePath = "C:\\Users\\alexp\\Desktop\\file.txt";
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0L, "resultCode");

        FolderPathByOperationTypeTransformer folderPathByOperationTypeTransformer = new FolderPathByOperationTypeTransformer("name","srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType");
        FileTransformedEvent fileTransformedEvent = (FileTransformedEvent) transformEvent(fileRawEvent, folderPathByOperationTypeTransformer, FileTransformedEvent.class);
        Assert.assertEquals("C:\\Users\\alexp\\Desktop\\", fileTransformedEvent.getSrcFolderPath());
        Assert.assertEquals(filePath, fileTransformedEvent.getSrcFilePath());
    }

    @Test
    public void testFolderPathTransformation_linux_FileOperation() throws IOException {
        String filePath = String.format("%sfolder%sfile.txt", "/", "/");
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0L, "resultCode");
        FolderPathByOperationTypeTransformer folderPathByOperationTypeTransformer = new FolderPathByOperationTypeTransformer("name", "srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType");
        FileTransformedEvent fileTransformedEvent = (FileTransformedEvent) transformEvent(fileRawEvent, folderPathByOperationTypeTransformer, FileTransformedEvent.class);
        Assert.assertEquals("/folder/", fileTransformedEvent.getSrcFolderPath());
        Assert.assertEquals(filePath, fileTransformedEvent.getSrcFilePath());
    }

    @Test
    public void testFolderPathTransformation_noField_FileOperation() throws IOException {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, null, false,
                null, false, 0L, "resultCode");

        FolderPathByOperationTypeTransformer folderPathByOperationTypeTransformer = new FolderPathByOperationTypeTransformer("name","srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType");
        FileTransformedEvent fileTransformedEvent = (FileTransformedEvent) transformEvent(fileRawEvent, folderPathByOperationTypeTransformer, FileTransformedEvent.class);
        Assert.assertNull(fileTransformedEvent.getSrcFolderPath());
    }

    @Test
    public void testFolderPathTransformation_fieldEmpty_FileOperation() throws IOException {
        String filePath = "";
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0L, "resultCode");
        FolderPathByOperationTypeTransformer folderPathByOperationTypeTransformer = new FolderPathByOperationTypeTransformer("name","srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType");
        FileTransformedEvent fileTransformedEvent = (FileTransformedEvent) transformEvent(fileRawEvent, folderPathByOperationTypeTransformer, FileTransformedEvent.class);
        Assert.assertNull(fileTransformedEvent.getSrcFolderPath());
    }

    @Override
    String getResourceFilePath() {
        return "FolderPathByOperationTypeTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return FolderPathByOperationTypeTransformer.class;
    }
}
