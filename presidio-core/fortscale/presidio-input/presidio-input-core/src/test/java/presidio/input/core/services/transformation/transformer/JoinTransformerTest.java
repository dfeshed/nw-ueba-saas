package presidio.input.core.services.transformation.transformer;


import fortscale.utils.transform.JoinTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.rawevents.ProcessRawEvent;
import presidio.sdk.api.domain.transformedevents.ProcessTransformedEvent;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringRunner.class)
public class JoinTransformerTest extends TransformerJsonTest {

    @Test
    public void testTransformation() throws IOException {

        String srcProcessDirectory = "C:\\Windows\\System32";
        String srcProcessFileName = "lsass.exe";

        ProcessRawEvent processRawEvent = new ProcessRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType","userName","userDisplayName",null,"machineId",
                "machineName","machineOwener",srcProcessDirectory,srcProcessFileName,null,
                null,"srcProcessCertificateIssuer","dstProcessDirectory","dstProcessFileName",
                null,null,"dstProcessCertificateIssuer");

        JoinTransformer joinTransformer = new JoinTransformer("name", ProcessTransformedEvent.SRC_PROCESS_FILE_PATH_FIELD_NAME, Arrays.asList("${" + ProcessRawEvent.SRC_PROCESS_DIRECTORY_FIELD_NAME + "}", "${" + ProcessRawEvent.SRC_PROCESS_FILE_NAME_FIELD_NAME + "}"),"\\");
        ProcessTransformedEvent processTransformedEvent = (ProcessTransformedEvent) transformEvent(processRawEvent, joinTransformer, ProcessTransformedEvent.class);
        Assert.assertEquals("C:\\Windows\\System32\\lsass.exe", processTransformedEvent.getSrcProcessFilePath());
    }

    @Test
    public void testTransformation_srcProcessDirectoryNull() throws IOException {
        String srcProcessDirectory = null;
        String srcProcessFileName = "lsass.exe";
        ProcessRawEvent processRawEvent = new ProcessRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType","userName","userDisplayName",null,"machineId",
                "machineName","machineOwener",srcProcessDirectory,srcProcessFileName,null,
                null,"srcProcessCertificateIssuer","dstProcessDirectory","dstProcessFileName",
                null,null,"dstProcessCertificateIssuer");

        JoinTransformer joinTransformer = new JoinTransformer("name", ProcessTransformedEvent.SRC_PROCESS_FILE_PATH_FIELD_NAME, Collections.singletonList("${" + ProcessRawEvent.SRC_PROCESS_DIRECTORY_FIELD_NAME + "}"),"\\");
        ProcessTransformedEvent processTransformedEvent = (ProcessTransformedEvent) transformEvent(processRawEvent, joinTransformer, ProcessTransformedEvent.class);
        Assert.assertNull(processTransformedEvent.getSrcProcessFilePath());
    }

    @Override
    String getResourceFilePath() {
        return "JoinTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return JoinTransformer.class;
    }
}
