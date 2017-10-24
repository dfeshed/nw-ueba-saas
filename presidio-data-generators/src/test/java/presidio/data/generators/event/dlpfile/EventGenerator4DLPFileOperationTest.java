package presidio.data.generators.event.dlpfile;

import org.junit.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.io.IOException;
import java.time.LocalTime;

public class EventGenerator4DLPFileOperationTest {
    /***
     * Default generator with random user in every event
     */
    @Test
    public void GenerateEventsListTest() throws GeneratorException, IOException {
        DLPFileEventsGenerator EVGEN = new DLPFileEventsGenerator();
        EVGEN.generate();
    }

    /***
     * Default generator with shift in start time each day, depending on interval size
     */
    @Test
    public void GenerateEventsListFloatingTimesTest() throws GeneratorException, IOException {
        DLPFileEventsGenerator EVGEN = new DLPFileEventsGenerator();
        TimeGenerator TG = new TimeGenerator(LocalTime.of(1,0), LocalTime.of(2,0),7,2,0);
        EVGEN.setTimeGenerator(TG);
        EVGEN.generate();
    }
    /***
     * Use same user for all events
     */
    @Test
    public void GenerateEventsSingleUserTest() throws GeneratorException, IOException {
        DLPFileEventsGenerator EVGEN = new DLPFileEventsGeneratorTemplateFactory().getDLPFileEventSingleUserGenerator("testuser");
        EVGEN.generate();
    }

    /***
     * Alter username, leave default single user in machine name and EventsID, alter username
     */
    @Test
    public void GenerateEventsOtherUserTest() throws GeneratorException, IOException {
        DLPFileEventsGenerator EVGEN = new DLPFileEventsGenerator();

        SingleUserGenerator UG = new SingleUserGenerator("otheruser");
        EVGEN.setUserGenerator(UG);
        EVGEN.generate();
    }

}
