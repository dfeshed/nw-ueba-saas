package presidio.data.generators.event;

import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public abstract class PerformanceStabilityScenario {

    private Instant startInstant;
    private Instant endInstant;
    protected double probabilityMultiplier;

    private MultiEventGenerator curDailyEventGenerator;
    private Instant curStartDailyInstant;
    private Instant curEndDailyInstant;

    protected List<EventGeneratorsBuilder> eventGeneratorsBuilders = new ArrayList<>();
    private boolean isInit = false;


    public PerformanceStabilityScenario(Instant startInstant, Instant endInstant,
                                                double probabilityMultiplier) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.probabilityMultiplier = probabilityMultiplier;
    }

    public void init() throws GeneratorException {
        initBuilders();

        this.curStartDailyInstant = startInstant;
        try {
            nextDailyEventGenerator();
        } catch (GeneratorException e) {
            throw new RuntimeException(e);
        }
        isInit = true;
    }



    protected abstract void initBuilders() throws GeneratorException;

    private void nextDailyEventGenerator() throws GeneratorException {
        if(curStartDailyInstant == null){
            curDailyEventGenerator = null;
        } else {
            curEndDailyInstant = curStartDailyInstant.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
            if (curEndDailyInstant.isAfter(endInstant)) {
                curEndDailyInstant = endInstant;
            }

            //get list of event generators
            List<AbstractEventGenerator<Event>> eventGenerators = new ArrayList<>();
            for (EventGeneratorsBuilder builder : eventGeneratorsBuilders) {
                eventGenerators.addAll(builder.buildGenerators(curStartDailyInstant, curEndDailyInstant));
            }

            curDailyEventGenerator = new MultiEventGenerator(eventGenerators);
            if (curEndDailyInstant.equals(endInstant)) {
                curStartDailyInstant = null;
            } else {
                curStartDailyInstant = curEndDailyInstant;
            }
        }
    }

    public List<Event> generateEvents(int numOfEventsToGenerate) throws GeneratorException {
        if(!isInit){
            init();
        }
        List<Event> events = new ArrayList<>();
        // daily loop
        while(curDailyEventGenerator != null && events.size() < numOfEventsToGenerate){
            events.addAll(curDailyEventGenerator.generate(numOfEventsToGenerate - events.size()));
            if(events.size() < numOfEventsToGenerate){
                nextDailyEventGenerator();
            }
        }

        return events;
    }
}
