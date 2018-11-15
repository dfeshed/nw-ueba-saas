package presidio.input.core.services.transformation.transformer;


import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.ProcessRawEvent;
import presidio.sdk.api.domain.transformedevents.ProcessTransformedEvent;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
public class JoinTransformerTest {

    @Test
    public void testTransformation() {

        String srcProcessDirectory = "C:\\Windows\\System32";
        String srcProcessFileName = "lsass.exe";

        ProcessRawEvent processRawEvent = new ProcessRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType","userName","userDisplayName",null,"machineId",
                "machineName","machineOwener",srcProcessDirectory,srcProcessFileName,null,
                null,"srcProcessCertificateIssuer","dstProcessDirectory","dstProcessFileName",
                null,null,"dstProcessCertificateIssuer");


        JoinTransformer joinTransformer = new JoinTransformer(Arrays.asList(ProcessRawEvent.SRC_PROCESS_DIRECTORY_FIELD_NAME, ProcessRawEvent.SRC_PROCESS_FILE_NAME_FIELD_NAME),ProcessTransformedEvent.SRC_PROCESS_FILE_PATH_FIELD_NAME,"\\");
        List<AbstractInputDocument> transformed =  joinTransformer.transform(Collections.singletonList(new ProcessTransformedEvent(processRawEvent)));

        Assert.assertEquals("C:\\Windows\\System32\\lsass.exe", ((ProcessTransformedEvent) transformed.get(0)).getSrcProcessFilePath());
    }

    @Test
    public void testTransformation_srcProcessDirectoryNull() {
        String srcProcessDirectory = null;
        String srcProcessFileName = "lsass.exe";

        ProcessRawEvent processRawEvent = new ProcessRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType","userName","userDisplayName",null,"machineId",
                "machineName","machineOwener",srcProcessDirectory,srcProcessFileName,null,
                null,"srcProcessCertificateIssuer","dstProcessDirectory","dstProcessFileName",
                null,null,"dstProcessCertificateIssuer");


        JoinTransformer joinTransformer = new JoinTransformer(Arrays.asList(ProcessRawEvent.SRC_PROCESS_DIRECTORY_FIELD_NAME, ProcessRawEvent.SRC_PROCESS_DIRECTORY_FIELD_NAME),ProcessTransformedEvent.SRC_PROCESS_FILE_PATH_FIELD_NAME,"\\");
        List<AbstractInputDocument> transformed =  joinTransformer.transform(Collections.singletonList(new ProcessTransformedEvent(processRawEvent)));

        Assert.assertNull(((ProcessTransformedEvent) transformed.get(0)).getSrcProcessFilePath());
    }

    @Test
    public void testTransformation_srcProcessFileNameNull() {
        String srcProcessDirectory = "C:\\Windows\\System32";
        String srcProcessFileName = null;

        ProcessRawEvent processRawEvent = new ProcessRawEvent(Instant.now(), "id", "dataSource", "userId",
                "operationType","userName","userDisplayName",null,"machineId",
                "machineName","machineOwener",srcProcessDirectory,srcProcessFileName,null,
                null,"srcProcessCertificateIssuer","dstProcessDirectory","dstProcessFileName",
                null,null,"dstProcessCertificateIssuer");


        JoinTransformer joinTransformer = new JoinTransformer(Arrays.asList(ProcessRawEvent.SRC_PROCESS_DIRECTORY_FIELD_NAME, ProcessRawEvent.SRC_PROCESS_DIRECTORY_FIELD_NAME),ProcessTransformedEvent.SRC_PROCESS_FILE_PATH_FIELD_NAME,"\\");
        List<AbstractInputDocument> transformed =  joinTransformer.transform(Collections.singletonList(new ProcessTransformedEvent(processRawEvent)));

        Assert.assertNull(((ProcessTransformedEvent) transformed.get(0)).getSrcProcessFilePath());
    }
}
