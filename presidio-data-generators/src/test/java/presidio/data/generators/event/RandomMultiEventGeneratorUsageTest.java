package presidio.data.generators.event;

import org.junit.Test;
import org.testng.Assert;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;
import presidio.data.generators.user.SingleUserGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RandomMultiEventGeneratorUsageTest {

    @Test
    public void AuthenticationBulkEventsGenerator() throws GeneratorException {
        List<Event> events = new ArrayList<>();
        AuthenticationEventsGenerator eventGenerator1 = new AuthenticationEventsGenerator();
        eventGenerator1.setUserGenerator(new SingleUserGenerator("User1"));
        AuthenticationEventsGenerator eventGenerator2 = new AuthenticationEventsGenerator();
        eventGenerator2.setUserGenerator(new SingleUserGenerator("User2"));

        List< RandomMultiEventGenerator.EventGeneratorProbability > listOfProbabilities =
                new ArrayList<>();
        RandomMultiEventGenerator.EventGeneratorProbability probability1 =
                new RandomMultiEventGenerator.EventGeneratorProbability(eventGenerator1, 1);
        listOfProbabilities.add(probability1);

        RandomMultiEventGenerator.EventGeneratorProbability probability2 =
                new RandomMultiEventGenerator.EventGeneratorProbability(eventGenerator2, 0.5);
        listOfProbabilities.add(probability2);

        RandomMultiEventGenerator generator = new RandomMultiEventGenerator(listOfProbabilities,
                Instant.now().minus(10,ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS),
                Instant.now().truncatedTo(ChronoUnit.DAYS), Duration.ofHours(1) );

        events.addAll(generator.generate());

        
    }
}
