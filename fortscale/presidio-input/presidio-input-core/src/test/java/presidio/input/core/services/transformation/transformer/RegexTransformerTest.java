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

@RunWith(SpringRunner.class)
public class RegexTransformerTest extends TransformerJsonTest {

    @Test
    public void testFolderPathTransformation() {
        String filePath = "C:\\Users\\alexp\\Desktop\\file.txt";
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, filePath, false,
                filePath, false, 0L, "resultCode");

        RegexTransformer regexTransformer = new RegexTransformer("srcFilePath", "srcFolderPath", ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
        AbstractInputDocument transformed = regexTransformer.transform(new FileTransformedEvent(fileRawEvent));

        Assert.assertEquals(String.format("C:\\Users\\alexp\\Desktop\\", File.separator), ((FileTransformedEvent) transformed).getSrcFolderPath());
        Assert.assertEquals(filePath, ((FileTransformedEvent) transformed).getSrcFilePath());
    }

    @Test
    public void testTransformation_SrcFieldNull() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, null, false,
                null, false, 0L, "resultCode");

        RegexTransformer regexTransformer = new RegexTransformer("srcFilePath", "srcFolderPath", ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
        AbstractInputDocument transformed = regexTransformer.transform(new FileTransformedEvent(fileRawEvent));

        Assert.assertNull(((FileTransformedEvent) transformed).getSrcFolderPath());
        Assert.assertNull(((FileTransformedEvent) transformed).getSrcFilePath());
    }

    @Test
    public void testTransformation_noDstField() {
        FileRawEvent fileRawEvent = new FileRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType", null, EventResult.SUCCESS, "userName",
                "displayName", null, null, false,
                null, false, 0L, "resultCode");

        RegexTransformer regexTransformer = new RegexTransformer("srcFilePath", "srcFolderPath", ".*\\\\(?!.*\\\\)|.*/(?!.*/)");
        AbstractInputDocument transformed = regexTransformer.transform(fileRawEvent);

        Assert.assertNull(((FileRawEvent) transformed).getSrcFilePath());
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
