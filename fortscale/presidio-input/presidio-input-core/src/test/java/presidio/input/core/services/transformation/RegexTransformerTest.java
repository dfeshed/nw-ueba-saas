package presidio.input.core.services.transformation;


import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.transformation.transformer.RegexTransformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
public class RegexTransformerTest {

    @Test
    public void testFolderPathTransformation() {
        String filePath = "C:\\Users\\alexp\\Desktop\\file.txt";
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0l, "resultCode");

        RegexTransformer regexTransformer = new RegexTransformer("srcFilePath", "srcFolderPath", ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
        List<AbstractInputDocument> transformed = regexTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertEquals(String.format("C:\\Users\\alexp\\Desktop\\", File.separator), ((FileTransformedEvent) transformed.get(0)).getSrcFolderPath());
        Assert.assertEquals(filePath, ((FileTransformedEvent) transformed.get(0)).getSrcFilePath());
    }

    @Test
    public void testTransformation_SrcFieldNull() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, null, false,
                null, false, 0l, "resultCode");

        RegexTransformer regexTransformer = new RegexTransformer("srcFilePath", "srcFolderPath", ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
        List<AbstractInputDocument> transformed = regexTransformer.transform(Arrays.asList(new FileTransformedEvent(fileRawEvent)));

        Assert.assertNull(((FileTransformedEvent) transformed.get(0)).getSrcFolderPath());
        Assert.assertNull(((FileTransformedEvent) transformed.get(0)).getSrcFilePath());
    }

    @Test
    public void testTransformation_noDstField() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, null, false,
                null, false, 0l, "resultCode");

        RegexTransformer regexTransformer = new RegexTransformer("srcFilePath", "srcFolderPath", ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
        List<AbstractInputDocument> transformed = regexTransformer.transform(Arrays.asList(fileRawEvent));

        Assert.assertNull(((FileRawEvent) transformed.get(0)).getSrcFilePath());
    }
}
