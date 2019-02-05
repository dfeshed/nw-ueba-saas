package presidio.data.generators.event;

import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.FixedRangeTimeGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomMultiEventGenerator extends AbstractEventGenerator<Event>{
    private Iterator<EventGeneratorProbability> eventGeneratorProbabilityIterator;
    private List<EventGeneratorProbability> eventGeneratorProbabilityList;
    private Random random;
    public RandomMultiEventGenerator(List<EventGeneratorProbability> eventGeneratorProbabilityList,
                                     Instant startInstant, Instant endInstant, Duration interval) throws GeneratorException {
        this.eventGeneratorProbabilityList = eventGeneratorProbabilityList;
        resetEventGeneratorProbabilityIterator();
        for (EventGeneratorProbability eventGeneratorProbability: eventGeneratorProbabilityList){
            timeGenerator = new FixedRangeTimeGenerator(startInstant, endInstant, interval);
            eventGeneratorProbability.getEventGenerator().setTimeGenerator(timeGenerator);
        }
        random = new Random(0);
    }

    public RandomMultiEventGenerator(List<EventGeneratorProbability> eventGeneratorProbabilityList,
                                     Instant startInstant, Instant endInstant, List<MultiRangeTimeGenerator.ActivityRange> activityRanges, Duration interval) throws GeneratorException {
        this.eventGeneratorProbabilityList = eventGeneratorProbabilityList;
        resetEventGeneratorProbabilityIterator();
        for (EventGeneratorProbability eventGeneratorProbability: eventGeneratorProbabilityList){
            timeGenerator = new MultiRangeTimeGenerator(startInstant, endInstant, activityRanges, interval);
            eventGeneratorProbability.getEventGenerator().setTimeGenerator(timeGenerator);
        }
        random = new Random(0);
    }

    private void resetEventGeneratorProbabilityIterator(){
        eventGeneratorProbabilityIterator = eventGeneratorProbabilityList.iterator();
    }

    @Override
    public List<Event> generate() throws GeneratorException {
        List<Event> ret = new ArrayList<>();
        while(timeGenerator.hasNext() != null){
            ret.add(generateNext());
        }
        return ret;
    }

    @Override
    public Event generateNext() throws GeneratorException {
        Event ret = null;
        while(ret == null && timeGenerator.hasNext() != null) {
            EventGeneratorProbability eventGeneratorProbability = eventGeneratorProbabilityIterator.next();
            if (!eventGeneratorProbabilityIterator.hasNext()) {
                resetEventGeneratorProbabilityIterator();
            }
            if (random.nextDouble() <= eventGeneratorProbability.getProbablility()) {
                ret = eventGeneratorProbability.getEventGenerator().generateNext();
            } else{
                //advancing the time with out generating data.
                eventGeneratorProbability.getEventGenerator().getTimeGenerator().getNext();
            }
        }

        return ret;
    }

    public static class EventGeneratorProbability{
        private AbstractEventGenerator<Event> eventGenerator;
        private double probablility;

        public EventGeneratorProbability(AbstractEventGenerator<Event> eventGenerator, double probablility){
            this.eventGenerator = eventGenerator;
            this.probablility = probablility;
        }

        public AbstractEventGenerator<Event> getEventGenerator() {
            return eventGenerator;
        }

        public double getProbablility() {
            return probablility;
        }
    }
}