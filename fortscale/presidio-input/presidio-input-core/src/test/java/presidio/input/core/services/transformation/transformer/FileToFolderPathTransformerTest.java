package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.transformation.transformer.FileToFolderPathTransformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
public class FileToFolderPathTransformerTest {

    @Test
    public void testFolderPathTransformation_windows() {
        String filePath = "C:\\Users\\alexp\\Desktop\\file.txt";
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0l, "resultCode");

        FileToFolderPathTransformer fileToFolderPathTransformer = new FileToFolderPathTransformer("srcFilePath", "srcFolderPath");

        List<AbstractInputDocument> transformed = fileToFolderPathTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertEquals(String.format("C:\\Users\\alexp\\Desktop\\", File.separator), ((FileTransformedEvent) transformed.get(0)).getSrcFolderPath());
        Assert.assertEquals(filePath, ((FileTransformedEvent) transformed.get(0)).getSrcFilePath());
    }

    @Test
    public void testFolderPathTransformation_linux() {
        String filePath = String.format("%sfolder%sfile.txt", "/", "/");
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0l, "resultCode");

        FileToFolderPathTransformer fileToFolderPathTransformer = new FileToFolderPathTransformer("srcFilePath", "srcFolderPath");

        List<AbstractInputDocument> transformed = fileToFolderPathTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertEquals(String.format("/folder/", File.separator), ((FileTransformedEvent) transformed.get(0)).getSrcFolderPath());
        Assert.assertEquals(filePath, ((FileTransformedEvent) transformed.get(0)).getSrcFilePath());
    }

    @Test
    public void testFolderPathTransformation_noField() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, null, false,
                null, false, 0l, "resultCode");

        FileToFolderPathTransformer fileToFolderPathTransformer = new FileToFolderPathTransformer("srcFilePath", "srcFolderPath");

        List<AbstractInputDocument> transformed = fileToFolderPathTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertNull(((FileTransformedEvent) transformed.get(0)).getSrcFolderPath());
    }

    @Test
    public void testFolderPathTransformation_fieldEmpty() {
        String filePath = "";
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0l, "resultCode");

        FileToFolderPathTransformer fileToFolderPathTransformer = new FileToFolderPathTransformer("srcFilePath", "srcFolderPath");

        List<AbstractInputDocument> transformed = fileToFolderPathTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertNull(((FileTransformedEvent) transformed.get(0)).getSrcFolderPath());
    }
}
