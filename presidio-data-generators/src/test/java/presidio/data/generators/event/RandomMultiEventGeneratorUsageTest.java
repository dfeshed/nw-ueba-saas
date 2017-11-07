package presidio.data.generators.event;

import org.junit.Test;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;

import java.util.List;

public class RandomMultiEventGeneratorUsageTest {

    @Test
    public void AuthenticationBulkEventsGenerator() throws GeneratorException {
        List<AuthenticationEvent> events = null;
        AuthenticationEventsGenerator eventGenerator = new AuthenticationEventsGenerator();
//        RandomMultiEventGenerator.EventGeneratorProbability probability =
//                new RandomMultiEventGenerator.EventGeneratorProbability(eventGenerator, 20.0);
    }
}
