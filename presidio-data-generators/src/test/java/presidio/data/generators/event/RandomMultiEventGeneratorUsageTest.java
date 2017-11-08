package presidio.data.generators.event;

import org.junit.Test;
import org.testng.Assert;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RandomMultiEventGeneratorUsageTest {

    @Test
    public void AuthenticationBulkEventsGenerator() throws GeneratorException {
        List<Event> events = new ArrayList<>();
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();

        List< RandomMultiEventGenerator.EventGeneratorProbability > listOfProbabilities =
                new ArrayList<>();
        RandomMultiEventGenerator.EventGeneratorProbability probability =
                new RandomMultiEventGenerator.EventGeneratorProbability(eventGenerator, 20.0);
        listOfProbabilities.add(probability);

        RandomMultiEventGenerator generator = new RandomMultiEventGenerator(listOfProbabilities,
                Instant.now().minus(10,ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS),
                Instant.now().truncatedTo(ChronoUnit.DAYS), Duration.ofHours(1) );

        // can generate events one by one
        AuthenticationEvent event = (AuthenticationEvent) generator.generateNext();
        events.add(event);

        // or in bulks
        events.addAll(generator.generate(10));
        Assert.assertEquals(events.size(),11);

        // or all at time
        events.addAll(generator.generate());
        Assert.assertEquals(events.size(), 240);
    }
}
