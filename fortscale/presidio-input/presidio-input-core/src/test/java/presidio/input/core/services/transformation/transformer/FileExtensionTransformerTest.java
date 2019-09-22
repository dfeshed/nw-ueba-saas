package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.EventResult;
import fortscale.utils.transform.RegexTransformer;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.rawevents.PrintRawEvent;
import presidio.sdk.api.domain.transformedevents.PrintTransformedEvent;

import java.io.IOException;
import java.time.Instant;

@RunWith(SpringRunner.class)
public class FileExtensionTransformerTest extends TransformerJsonTest {

    private static final String FILE_REGEX = "\\.[0-9a-z]+$";
    private PrintRawEvent createPrintRawEvent(String filePath) {
        return new PrintRawEvent(Instant.now(), "eventId", "dataSource",
                "userId", "operationType", null, EventResult.SUCCESS,
                "userName", "userDisplayName", null, "resultCode",
                "srcMachineId", "srcMachineName", "printerId", "printerName",
                filePath, false, 10L, 10L);
    }

    @Test
    public void testFileExtensionTransformation_windows() throws IOException {
        String filePath = "C:\\Users\\alexp\\Desktop\\file.txt";
        PrintRawEvent printRawEvent = createPrintRawEvent(filePath);
        RegexTransformer fileExtensionTransformer = new RegexTransformer("name",
                PrintRawEvent.SRC_FILE_PATH_FIELD_NAME, PrintTransformedEvent.SRC_FILE_EXTENSION_FIELD_NAME, FILE_REGEX);
        PrintTransformedEvent printTransformedEvent = transformEvent(printRawEvent, fileExtensionTransformer);
        Assert.assertEquals(".txt", printTransformedEvent.getSrcFileExtension());
    }

    @Test
    public void testFileExtensionTransformation_linux() throws IOException {
        String filePath = String.format("%sfolder%sfile.txt", "/", "/");
        PrintRawEvent printRawEvent = createPrintRawEvent(filePath);
        RegexTransformer fileExtensionTransformer = new RegexTransformer("name",
                PrintRawEvent.SRC_FILE_PATH_FIELD_NAME, PrintTransformedEvent.SRC_FILE_EXTENSION_FIELD_NAME, FILE_REGEX);
        PrintTransformedEvent printTransformedEvent = transformEvent(printRawEvent, fileExtensionTransformer);

        Assert.assertEquals(".txt", printTransformedEvent.getSrcFileExtension());
    }

    @Test
    public void testFileExtensionTransformation_nullSrcValue() throws IOException {
        PrintRawEvent printRawEvent = createPrintRawEvent(null);
        RegexTransformer fileExtensionTransformer = new RegexTransformer("name",
                PrintRawEvent.SRC_FILE_PATH_FIELD_NAME, PrintTransformedEvent.SRC_FILE_EXTENSION_FIELD_NAME, FILE_REGEX);
        PrintTransformedEvent printTransformedEvent = transformEvent(printRawEvent, fileExtensionTransformer);
        Assert.assertNull(printTransformedEvent.getSrcFileExtension());
    }

    @Test
    public void testFolderPathTransformation_fieldEmpty() throws IOException {
        PrintRawEvent printRawEvent = createPrintRawEvent("");
        RegexTransformer fileExtensionTransformer = new RegexTransformer("name",
                PrintRawEvent.SRC_FILE_PATH_FIELD_NAME, PrintTransformedEvent.SRC_FILE_EXTENSION_FIELD_NAME, FILE_REGEX);
        PrintTransformedEvent printTransformedEvent = transformEvent(printRawEvent, fileExtensionTransformer);
        Assert.assertNull(printTransformedEvent.getSrcFileExtension());
    }

    private PrintTransformedEvent transformEvent(PrintRawEvent printRawEvent, RegexTransformer fileExtensionTransformer) throws IOException {
        ObjectMapper mapper = createObjectMapper();
        JSONObject jsonObject = new JSONObject(mapper.writeValueAsString(printRawEvent));
        JSONObject transformed = fileExtensionTransformer.transform(jsonObject);
        return mapper.readValue(transformed.toString(), PrintTransformedEvent.class);
    }

    @Override
    String getResourceFilePath() {
        return "FileExtensionTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return RegexTransformer.class;
    }
}
