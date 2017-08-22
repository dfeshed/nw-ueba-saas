package presidio.input.core.services.transformation;

import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.AbstractPresidioDocument;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.time.Instant;
import java.util.*;

@RunWith(SpringRunner.class)
public class FolderPathTransformerTest {

    @Test
    public void testFolderPathTransformation_FileOperation(){
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, "C:\\file\\file.txt", false,
                "C:\\dst\\file.txt", false,0l);

        List<String> folderOperations = new ArrayList<>();
        FolderPathTransformer folderPathTransformer = new FolderPathTransformer("srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType", folderOperations);

        List<AbstractPresidioDocument> transformed = folderPathTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertEquals("C:\\file", ((FileTransformedEvent)transformed.get(0)).getSrcFolderPath());
        Assert.assertEquals("C:\\file\\file.txt", ((FileTransformedEvent)transformed.get(0)).getSrcFilePath());
    }

    @Test
    public void testFolderPathTransformation_FolderOperation(){
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "Folder", null, EventResult.SUCCESS, "userName",
                "displayName", null, "C://file", false,
                "C://dst/file.txt", false,0l);

        List<String> folderOperations = new ArrayList<>();
        folderOperations.add("Folder");
        FolderPathTransformer folderPathTransformer = new FolderPathTransformer("srcFilePath",
                "srcFilePath", "srcFolderPath", "operationType", folderOperations);

        List<AbstractPresidioDocument> transformed = folderPathTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertNull(((FileTransformedEvent)transformed.get(0)).getSrcFilePath());
        Assert.assertEquals("C://file", ((FileTransformedEvent)transformed.get(0)).getSrcFolderPath());
    }
}
