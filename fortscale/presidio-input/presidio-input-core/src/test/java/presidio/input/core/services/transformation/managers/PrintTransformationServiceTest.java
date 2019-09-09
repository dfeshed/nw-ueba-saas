package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.utils.transform.IJsonObjectTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.transformation.DeserializerTransformationService;
import presidio.input.core.services.transformation.TransformationService;
import presidio.input.core.spring.InputConfigTest;
import presidio.input.core.spring.InputCoreConfigurationTest;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.PrintRawEvent;
import presidio.sdk.api.domain.transformedevents.PrintTransformedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {InputConfigTest.class, InputCoreConfigurationTest.class})
public class PrintTransformationServiceTest {
    @Autowired
    private TransformationService transformationService;

    @Autowired
    private DeserializerTransformationService deserializerTransformationService;

    private List<IJsonObjectTransformer> transformers = new ArrayList<>();

    private Instant endDate = Instant.now();

    @Test
    public void testFilePathTransformation() {
        PrintRawEvent printEvent = createPrintEvent("/usr/someuser/somesubdir/1/File.jar", "", "", "", "");
        List<AbstractInputDocument> transformedEvents = transformationService.run(Collections.singletonList(printEvent), Schema.PRINT, endDate,
                deserializerTransformationService.getTransformers(Schema.PRINT, endDate, endDate));
        Assert.assertEquals(1, transformedEvents.size());
        Assert.assertEquals(".jar", ((PrintTransformedEvent)transformedEvents.get(0)).getSrcFileExtension());
        Assert.assertEquals("/usr/someuser/somesubdir/1/", ((PrintTransformedEvent)transformedEvents.get(0)).getSrcFolderPath());
    }

    @Test
    public void testRunSrcMachineTransformations_unresolvedMachineNameAndId() {
        PrintRawEvent printEvent = createPrintEvent("/usr/someuser/somesubdir/1/File.jar", "10.20.3.40", "1.34.56.255", "12.4.6.74", "10.65.20.88");
        List<AbstractInputDocument> events = Collections.singletonList(printEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.PRINT, endDate,
                deserializerTransformationService.getTransformers(Schema.PRINT, endDate, endDate));
        Assert.assertEquals("", ((PrintTransformedEvent)transformedEvents.get(0)).getSrcMachineCluster());
        Assert.assertEquals("", ((PrintTransformedEvent)transformedEvents.get(0)).getSrcMachineId());
        Assert.assertEquals("", ((PrintTransformedEvent)transformedEvents.get(0)).getPrinterCluster());
        Assert.assertEquals("", ((PrintTransformedEvent)transformedEvents.get(0)).getPrinterId());
    }

    @Test
    public void testRunSrcMachineTransformations_resolvedMachineNameAndId() {
        PrintRawEvent printEvent = createPrintEvent("/usr/someuser/somesubdir/1/File.jar", "nameSPBGDCW01.prod.quest.corp", "idSPBGDCW01.prod.quest.corp", "nameSPBGDCW02.prod.quest.corp", "idSPBGDCW02.prod.quest.corp");
        List<AbstractInputDocument> events = Collections.singletonList(printEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.PRINT, endDate,
                deserializerTransformationService.getTransformers(Schema.PRINT, endDate, endDate));
        Assert.assertEquals("nameSPBGDCW.prod.quest.corp", ((PrintTransformedEvent)transformedEvents.get(0)).getSrcMachineCluster());
        Assert.assertEquals("idSPBGDCW01.prod.quest.corp", ((PrintTransformedEvent)transformedEvents.get(0)).getSrcMachineId());
        Assert.assertEquals("nameSPBGDCW.prod.quest.corp", ((PrintTransformedEvent)transformedEvents.get(0)).getPrinterCluster());
        Assert.assertEquals("idSPBGDCW02.prod.quest.corp", ((PrintTransformedEvent)transformedEvents.get(0)).getPrinterId());
    }

    public PrintRawEvent createPrintEvent(String srcFilePath, String srcMachineName, String srcMachineId, String printerName, String printerId) {
        return new PrintRawEvent(Instant.now(), "eventId", "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null, "resultCode", srcMachineId, srcMachineName,
                printerId, printerName, srcFilePath, false, 10L, 10L);
    }
}