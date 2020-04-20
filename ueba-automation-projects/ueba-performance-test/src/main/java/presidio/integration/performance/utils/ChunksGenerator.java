package presidio.integration.performance.utils;

import fortscale.common.general.Schema;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunksGenerator {

    private final PerformanceScenario dailyScenario;
    private final int EVENTS_GENERATION_CHUNK;
    private final Schema schema;

    public ChunksGenerator(PerformanceScenario dailyScenario, int size) {
        this.dailyScenario = dailyScenario;
        this.EVENTS_GENERATION_CHUNK = size;
        this.schema = dailyScenario.getSchema();

        System.out.println(dailyScenario.getStartDay() + " day -- Creating events for " + schema );
    }


    public List<? extends Event> getNextChunk() {
        try {
            if (schema.equals(Schema.TLS)) {
                return dailyScenario.getScenario().generateEvents(EVENTS_GENERATION_CHUNK);
            }


            if (schema.equals(Schema.PROCESS)) {
                return dailyScenario.getScenario().generateEvents(EVENTS_GENERATION_CHUNK);
            }

            if (schema.equals(Schema.REGISTRY)) {
                return dailyScenario.getScenario().generateEvents(EVENTS_GENERATION_CHUNK);
            }

            if (schema.equals(Schema.ACTIVE_DIRECTORY)) {
                return dailyScenario.getScenario().generateEvents(EVENTS_GENERATION_CHUNK);
            }

            if (schema.equals(Schema.AUTHENTICATION)) {
                List<Event> events;
                events = dailyScenario.getScenario().generateEvents(EVENTS_GENERATION_CHUNK);
                Map<String, List<Event>> srcMachineToEvents = new HashMap<>();
                Map<String, List<Event>> dstMachineToEvents = new HashMap<>();
                for (Event event : events) {
                    List<Event> srcMachineEvents = srcMachineToEvents.computeIfAbsent(((AuthenticationEvent) event).getSrcMachineEntity().getMachineId(), k -> new ArrayList<>());
                    srcMachineEvents.add(event);
                    List<Event> dstMachineEvents = dstMachineToEvents.computeIfAbsent(((AuthenticationEvent) event).getDstMachineEntity().getMachineId(), k -> new ArrayList<>());
                    dstMachineEvents.add(event);
                }
                return events;
            }

            if (schema.equals(Schema.FILE)) {
                List<Event> events;
                events = dailyScenario.getScenario().generateEvents(EVENTS_GENERATION_CHUNK);
                Map<String, List<Event>> srcMachineToEvents = new HashMap<>();
                Map<String, List<Event>> srcProcessToEvents = new HashMap<>();
                Map<String, List<Event>> dstProcessToEvents = new HashMap<>();

                for (Event event : events) {
                    List<Event> srcMachineEvents = srcMachineToEvents.computeIfAbsent(((FileEvent)event).getMachineEntity().getMachineId(), k -> new ArrayList<>());
                    srcMachineEvents.add(event);
                    List<Event> srcProcessEvents = srcProcessToEvents.computeIfAbsent(((FileEvent)event).getFileOperation().getSourceFile().getFileName(), k -> new ArrayList<>());
                    srcProcessEvents.add(event);
                    List<Event> dstProcessEvents = dstProcessToEvents.computeIfAbsent(((FileEvent)event).getFileOperation().getDestinationFile().getFileName(), k -> new ArrayList<>());
                    dstProcessEvents.add(event);
                }
                return events;
            }

        } catch (GeneratorException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("No such schema " + schema);
    }


}
