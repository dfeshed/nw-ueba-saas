package presidio.data.generators.event.performance;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.performance.scenario.ActiveDirectoryPerformanceStabilityScenario;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActiveDirectoryPerformanceStabilityTest {

    private final int EVENTS_GENERATION_CHUNK = 1000000;


    private StopWatch stopWatch = new StopWatch();

    @Test
    public void test() throws GeneratorException {
        stopWatch.start();

        Instant startInstant    = Instant.parse("2010-01-01T09:00:00.00Z");
        Instant endInstant      = Instant.parse("2010-01-01T09:31:00.00Z");

        ActiveDirectoryPerformanceStabilityScenario scenario =
                new ActiveDirectoryPerformanceStabilityScenario(startInstant, endInstant, 1000, 50, 50, 1);
        scenario.init();

        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);

            Map<String, List<Event>> userToEvents = new HashMap<>();
            Map<String, Map<String, Integer>> userToOperationType = new HashMap<>();
            for(Event event: events){
                List<Event> userEvents = userToEvents.computeIfAbsent(((ActiveDirectoryEvent)event).getUser().getUserId(), k -> new ArrayList<>());
                userEvents.add(event);
                Map<String, Integer> userOperationTypesMap = userToOperationType.computeIfAbsent(((ActiveDirectoryEvent)event).getUser().getUserId(), k -> new HashMap<>());
                userOperationTypesMap.compute(((ActiveDirectoryEvent)event).getOperation().getOperationType().getName(), (k, v) -> v==null ? 1 : v+1);
            }

            stopWatch.split();
            System.out.println(stopWatch.toSplitString());
        } while (events.size() == EVENTS_GENERATION_CHUNK);

        stopWatch.split();
        System.out.println(stopWatch.toSplitString());
    }





}
