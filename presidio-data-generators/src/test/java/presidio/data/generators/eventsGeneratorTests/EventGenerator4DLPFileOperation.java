package presidio.data.generators.eventsGeneratorTests;

import org.junit.Test;
import org.slf4j.LoggerFactory;
import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.FloatingTimeGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.dlpfile.DLPFileEventsGeneratorTemplateFactory;
import presidio.data.generators.event.dlpfile.ExecutingApplicationCyclicGenerator;
import presidio.data.generators.machine.HostnameFromUsernameGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.event.dlpfile.DLPFileEventsGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.io.IOException;
import java.time.LocalTime;

public class EventGenerator4DLPFileOperation {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(EventGenerator4DLPFileOperation.class.getName());

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
        TimeGenerator TG = new FloatingTimeGenerator(LocalTime.of(1,0), LocalTime.of(2,0),7,2,0);
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

    @Test
    public void FieldGeneratorsTest() throws GeneratorException, IOException {
        DLPFileEventsGenerator EVGEN = new DLPFileEventsGenerator();

        /***
         * Date Time
         */
        TimeGenerator myTimeGenerator =
                new TimeGenerator(LocalTime.of(8,00), LocalTime.of(10,00), 10, 1, 1);
        EVGEN.setTimeGenerator(myTimeGenerator);

        /***
         * Executing Application
         */

        String[] myApps = {"Word.exe", "Excel.exe", "PowerPoint.exe", "Access.exe", "Outlook.exe", "OneNote.exe"};
        ExecutingApplicationCyclicGenerator myExecutingApplicationGen = new ExecutingApplicationCyclicGenerator(myApps);
        EVGEN.setExecutingApplicationListGenerator(myExecutingApplicationGen);


        /***
         * Source MachineEntity
         */

        String[] IP = new String[myTimeGenerator.getSize()];

        for(int i = 1; i <= myTimeGenerator.getSize(); i++){
            StringBuilder hostip = new StringBuilder();
            IP[i - 1] = hostip.append("192.168.0.").append(i).toString();
        }

        HostnameFromUsernameGenerator HG = new HostnameFromUsernameGenerator("LiranAv",myTimeGenerator.getSize());

        FixedIPsGenerator SIPG = new FixedIPsGenerator(IP);

        SimpleMachineGenerator mySrcMachineGen = new SimpleMachineGenerator(HG,SIPG);
        EVGEN.setSourceMachineGenerator(mySrcMachineGen);

        EVGEN.generate();
    }
}
