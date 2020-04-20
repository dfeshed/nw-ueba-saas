package presidio.data.generators.event.ioc;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.event.ioc.IocEvent;
import presidio.data.generators.common.GeneratorException;

import java.util.List;

public class IocEventsGeneratorTest {
    @Test
    public void IocAllEventsGenerator() throws GeneratorException {
        List<IocEvent> events;
        IocEventsGenerator generator = new IocEventsGenerator();

        events = generator.generate();
        Assert.assertEquals(1392, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }
}
