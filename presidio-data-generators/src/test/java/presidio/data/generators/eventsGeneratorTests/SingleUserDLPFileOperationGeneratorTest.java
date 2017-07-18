package presidio.data.generators.eventsGeneratorTests;

import org.slf4j.LoggerFactory;
import org.junit.Test;import presidio.data.generators.event.dlpfile.DLPFileEventsGeneratorTemplateFactory;
import presidio.data.generators.event.dlpfile.DLPFileEventsGenerator;
import presidio.data.generators.common.GeneratorException;

import java.io.IOException;

public class SingleUserDLPFileOperationGeneratorTest {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(SingleUserDLPFileOperationGeneratorTest.class.getName());

    @Test
    public void GenerateEventsSingleUserTest() throws GeneratorException, IOException {
        DLPFileEventsGenerator EVGEN = new DLPFileEventsGeneratorTemplateFactory().getDLPFileEventSingleUserGenerator("AnaPa");
        EVGEN.generate();
    }
}
