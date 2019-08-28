package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.PrintRawEvent;
import presidio.sdk.api.domain.transformedevents.PrintTransformedEvent;

import java.time.Instant;

@RunWith(SpringRunner.class)
public class FileExtensionTransformerTest extends TransformerJsonTest {
    private PrintRawEvent createPrintRawEvent(String filePath) {
        return new PrintRawEvent(Instant.now(), "eventId", "dataSource",
                "userId", "operationType", null, EventResult.SUCCESS,
                "userName", "userDisplayName", null, "resultCode",
                "srcMachineId", "srcMachineName", "printerId", "printerName",
                filePath, false, 10L, 10L);
    }

    @Test
    public void testFileExtensionTransformation_windows() {
        String filePath = "C:\\Users\\alexp\\Desktop\\file.txt";
        PrintRawEvent printRawEvent = createPrintRawEvent(filePath);

        FileExtensionTransformer fileExtensionTransformer = new FileExtensionTransformer(PrintRawEvent.SRC_FILE_PATH_FIELD_NAME, PrintTransformedEvent.SRC_FILE_EXTENSION_FIELD_NAME);

        AbstractInputDocument transformed = fileExtensionTransformer.transform(new PrintTransformedEvent(printRawEvent));

        Assert.assertEquals(".txt", ((PrintTransformedEvent) transformed).getSrcFileExtension());
    }

    @Test
    public void testFileExtensionTransformation_linux() {
        String filePath = String.format("%sfolder%sfile.txt", "/", "/");
        PrintRawEvent printRawEvent = createPrintRawEvent(filePath);

        FileExtensionTransformer fileExtensionTransformer = new FileExtensionTransformer(PrintRawEvent.SRC_FILE_PATH_FIELD_NAME, PrintTransformedEvent.SRC_FILE_EXTENSION_FIELD_NAME);

        AbstractInputDocument transformed = fileExtensionTransformer.transform(new PrintTransformedEvent(printRawEvent));

        Assert.assertEquals(".txt", ((PrintTransformedEvent) transformed).getSrcFileExtension());
    }

    @Test
    public void testFileExtensionTransformation_nullSrcValue() {
        PrintRawEvent printRawEvent = createPrintRawEvent(null);

        FileExtensionTransformer fileExtensionTransformer = new FileExtensionTransformer(PrintRawEvent.SRC_FILE_PATH_FIELD_NAME, PrintTransformedEvent.SRC_FILE_EXTENSION_FIELD_NAME);

        AbstractInputDocument transformed = fileExtensionTransformer.transform(new PrintTransformedEvent(printRawEvent));

        Assert.assertNull(((PrintTransformedEvent) transformed).getSrcFileExtension());
    }

    @Test
    public void testFolderPathTransformation_fieldEmpty() {
        PrintRawEvent printRawEvent = createPrintRawEvent("");

        FileExtensionTransformer fileExtensionTransformer = new FileExtensionTransformer(PrintRawEvent.SRC_FILE_PATH_FIELD_NAME, PrintTransformedEvent.SRC_FILE_EXTENSION_FIELD_NAME);

        AbstractInputDocument transformed = fileExtensionTransformer.transform(new PrintTransformedEvent(printRawEvent));

        Assert.assertNull(((PrintTransformedEvent) transformed).getSrcFileExtension());
    }

    @Override
    String getResourceFilePath() {
        return "FileExtensionTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return FileExtensionTransformer.class;
    }
}
