package presidio.data.generators.event.process;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProcessEventsGeneratorTest {

    private final int EVENTS_GENERATION_CHUNK = 1000000;

    private StopWatch stopWatch = new StopWatch();

    @Test
    public void test() throws GeneratorException {
        stopWatch.start();

        Instant startInstant    = Instant.parse("2010-01-01T06:00:00.00Z");
        Instant endInstant      = Instant.parse("2010-01-01T06:01:00.00Z");

        PerformanceStabilityScenario scenario = new PerformanceStabilityScenario(startInstant, endInstant, 1, 1);

        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);

            Map<String, List<Event>> userToEvents = new HashMap<>();
            Map<String, List<Event>> srcProcessToEvents = new HashMap<>();
            Map<String, List<Event>> dstProcessToEvents = new HashMap<>();
            for(Event event: events){
                List<Event> userEvents = userToEvents.computeIfAbsent(((ProcessEvent)event).getUser().getUserId(), k -> new ArrayList<>());
                userEvents.add(event);
                List<Event> srcProcessEvents = srcProcessToEvents.computeIfAbsent(((ProcessEvent)event).getProcessOperation().getSourceProcess().getProcessFileName(), k -> new ArrayList<>());
                srcProcessEvents.add(event);
                List<Event> dstProcessEvents = dstProcessToEvents.computeIfAbsent(((ProcessEvent)event).getProcessOperation().getDestinationProcess().getProcessFileName(), k -> new ArrayList<>());
                dstProcessEvents.add(event);
            }

            stopWatch.split();
            System.out.println(stopWatch.toSplitString());
        } while (events.size() == EVENTS_GENERATION_CHUNK);

        stopWatch.split();
        System.out.println(stopWatch.toSplitString());
    }





}
