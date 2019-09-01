package presidio.input.core.services.transformation.transformer;


import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

@RunWith(SpringRunner.class)
public class RegexTransformerTest extends TransformerJsonTest {

    @Test
    public void testFolderPathTransformation() throws IOException {
        String filePath = "C:\\Users\\alexp\\Desktop\\file.txt";
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0L, "resultCode");

        RegexTransformer regexTransformer = new RegexTransformer("name","srcFilePath", "srcFolderPath", ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
        FileTransformedEvent fileTransformedEvent = (FileTransformedEvent)transformEvent(fileRawEvent, regexTransformer, FileTransformedEvent.class);
        Assert.assertEquals(String.format("C:\\Users\\alexp\\Desktop\\", File.separator), fileTransformedEvent.getSrcFolderPath());
        Assert.assertEquals(filePath, fileTransformedEvent.getSrcFilePath());
    }

    @Test
    public void testTransformation_SrcFieldNull() throws IOException {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, null, false,
                null, false, 0L, "resultCode");

        RegexTransformer regexTransformer = new RegexTransformer("name", "srcFilePath", "srcFolderPath", ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
        FileTransformedEvent fileTransformedEvent = (FileTransformedEvent)transformEvent(fileRawEvent, regexTransformer, FileTransformedEvent.class);
        Assert.assertNull(fileTransformedEvent.getSrcFolderPath());
        Assert.assertNull(fileTransformedEvent.getSrcFilePath());
    }

    @Test
    public void testTransformation_noDstField() throws IOException {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, null, false,
                null, false, 0L, "resultCode");

        RegexTransformer regexTransformer = new RegexTransformer("name", "srcFilePath", "srcFolderPath", ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
        FileTransformedEvent fileTransformedEvent = (FileTransformedEvent)transformEvent(fileRawEvent, regexTransformer, FileTransformedEvent.class);
        Assert.assertNull(fileTransformedEvent.getSrcFilePath());
    }

    @Override
    String getResourceFilePath() {
        return "RegexTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return RegexTransformer.class;
    }
}
