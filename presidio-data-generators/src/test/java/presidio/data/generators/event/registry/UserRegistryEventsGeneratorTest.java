package presidio.data.generators.event.registry;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRegistryEventsGeneratorTest {
    private final int EVENTS_GENERATION_CHUNK = 1000000;

    private StopWatch stopWatch = new StopWatch();

    @Test
    public void test() throws GeneratorException {
        stopWatch.start();

        Instant startInstant    = Instant.parse("2010-01-01T06:00:00.00Z");
        Instant endInstant      = Instant.parse("2010-01-01T06:01:00.00Z");

        RegistryPerformanceStabilityScenario scenario = new RegistryPerformanceStabilityScenario(startInstant, endInstant, 1, 1);

        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);

            Map<String, List<Event>> userToEvents = new HashMap<>();
            Map<String, List<Event>> processToEvents = new HashMap<>();
            Map<String, List<Event>> registryKeyToEvents = new HashMap<>();
            Map<String, List<Event>> registryKeyGroupToEvents = new HashMap<>();
            for(Event event: events){
                List<Event> userEvents = userToEvents.computeIfAbsent(((RegistryEvent)event).getUser().getUserId(), k -> new ArrayList<>());
                userEvents.add(event);
                List<Event> processEvents = processToEvents.computeIfAbsent(((RegistryEvent)event).getRegistryOperation().getProcess().getProcessFileName(), k -> new ArrayList<>());
                processEvents.add(event);
                List<Event> registryKeyEvents = registryKeyToEvents.computeIfAbsent(((RegistryEvent)event).getRegistryOperation().getRegistryEntry().getKey(), k -> new ArrayList<>());
                registryKeyEvents.add(event);
                List<Event> registryKeyGroupEvents = registryKeyGroupToEvents.computeIfAbsent(((RegistryEvent)event).getRegistryOperation().getRegistryEntry().getKeyGroup(), k -> new ArrayList<>());
                registryKeyGroupEvents.add(event);
            }

            stopWatch.split();
            System.out.println(stopWatch.toSplitString());
        } while (events.size() == EVENTS_GENERATION_CHUNK);

        stopWatch.split();
        System.out.println(stopWatch.toSplitString());
    }

}
