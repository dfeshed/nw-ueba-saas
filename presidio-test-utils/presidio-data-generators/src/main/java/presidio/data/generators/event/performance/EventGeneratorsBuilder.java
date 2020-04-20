package presidio.data.generators.event.performance;

import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.RandomMultiEventGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public abstract class EventGeneratorsBuilder {
    private double probabilityMultiplier;


    public EventGeneratorsBuilder(){
        this.probabilityMultiplier = 1;
    }

    public void setProbabilityMultiplier(double probabilityMultiplier) {
        this.probabilityMultiplier = probabilityMultiplier;
    }

    public double getProbabilityMultiplier() {
        return probabilityMultiplier;
    }

    protected RandomMultiEventGenerator createRandomEventGenerator(AbstractEventGenerator<Event> eventGenerator,
                                                                   List<MultiRangeTimeGenerator.ActivityRange> rangesList,
                                                                   double eventProbability,
                                                                   int timeIntervalForAbnormalTime,
                                                                   Instant startInstant,
                                                                   Instant endInstant) {
        List< RandomMultiEventGenerator.EventGeneratorProbability > listOfProbabilities = new ArrayList<>();
        RandomMultiEventGenerator.EventGeneratorProbability eventsProbabilityForNormalUsers =
                new RandomMultiEventGenerator.EventGeneratorProbability(eventGenerator, eventProbability*getProbabilityMultiplier());
        listOfProbabilities.add(eventsProbabilityForNormalUsers);



        RandomMultiEventGenerator randomEventsGenerator = new RandomMultiEventGenerator(listOfProbabilities,
                startInstant, endInstant, rangesList, Duration.ofMillis((int) (timeIntervalForAbnormalTime) ));
        return randomEventsGenerator;
    }

    public abstract List<AbstractEventGenerator<Event>> buildGenerators(Instant startInstant, Instant endInstant) throws GeneratorException;
}
