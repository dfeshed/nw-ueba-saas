package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class FolderPathByOperationTypeTransformerTest extends TransformerJsonTest {

    @Test
    public void testFolderPathTransformation_windows_FileOperation() {
        String filePath = "C:\\Users\\alexp\\Desktop\\file.txt";
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0L, "resultCode");

        FolderPathByOperationTypeTransformer folderPathByOperationTypeTransformer = new FolderPathByOperationTypeTransformer("srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType");

        AbstractInputDocument transformed = folderPathByOperationTypeTransformer.transform(new FileTransformedEvent(fileRawEvent));

        Assert.assertEquals(String.format("C:\\Users\\alexp\\Desktop\\", File.separator), ((FileTransformedEvent) transformed).getSrcFolderPath());
        Assert.assertEquals(filePath, ((FileTransformedEvent) transformed).getSrcFilePath());
    }

    @Test
    public void testFolderPathTransformation_linux_FileOperation() {
        String filePath = String.format("%sfolder%sfile.txt", "/", "/");
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0L, "resultCode");

        FolderPathByOperationTypeTransformer folderPathByOperationTypeTransformer = new FolderPathByOperationTypeTransformer("srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType");

        AbstractInputDocument transformed = folderPathByOperationTypeTransformer.transform(new FileTransformedEvent(fileRawEvent));

        Assert.assertEquals(String.format("/folder/", File.separator), ((FileTransformedEvent) transformed).getSrcFolderPath());
        Assert.assertEquals(filePath, ((FileTransformedEvent) transformed).getSrcFilePath());
    }

    @Test
    public void testFolderPathTransformation_FolderOperation() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "Folder", null, EventResult.SUCCESS, "userName",
                "displayName", null, "C://file", false,
                "C://dst/file.txt", false, 0L, "resultCode");

        List<String> folderOperations = new ArrayList<>();
        folderOperations.add("Folder");
        FolderPathByOperationTypeTransformer folderPathByOperationTypeTransformer = new FolderPathByOperationTypeTransformer("srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType");
        folderPathByOperationTypeTransformer.setFolderOperations(folderOperations);

        AbstractInputDocument transformed = folderPathByOperationTypeTransformer.transform(new FileTransformedEvent(fileRawEvent));

        Assert.assertNull(((FileTransformedEvent) transformed).getSrcFilePath());
        Assert.assertEquals("C://file", ((FileTransformedEvent) transformed).getSrcFolderPath());
    }

    @Test
    public void testFolderPathTransformation_noField_FileOperation() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, null, false,
                null, false, 0L, "resultCode");

        FolderPathByOperationTypeTransformer folderPathByOperationTypeTransformer = new FolderPathByOperationTypeTransformer("srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType");

        AbstractInputDocument transformed = folderPathByOperationTypeTransformer.transform(new FileTransformedEvent(fileRawEvent));

        Assert.assertNull(((FileTransformedEvent) transformed).getSrcFolderPath());
    }

    @Test
    public void testFolderPathTransformation_fieldEmpty_FileOperation() {
        String filePath = "";
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0L, "resultCode");

        FolderPathByOperationTypeTransformer folderPathByOperationTypeTransformer = new FolderPathByOperationTypeTransformer("srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType");

        AbstractInputDocument transformed = folderPathByOperationTypeTransformer.transform(new FileTransformedEvent(fileRawEvent));

        Assert.assertNull(((FileTransformedEvent) transformed).getSrcFolderPath());
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
