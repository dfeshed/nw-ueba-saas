package presidio.ade.test.utils.tests;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.test.utils.EventsGenerator;
import presidio.data.generators.common.GeneratorException;

public abstract class EnrichedFileSourceBaseAppTest extends BaseAppTest {
    @Autowired
    protected EventsGenerator eventsGenerator;

    /**
     * Generate 2 events per hour along 24 hours for 2 days.
     * Operation type of all the events is "open"
     * execute sanitytest command
     */
    @Test
    public void sanityTest() throws GeneratorException {
        eventsGenerator.generateAndPersistSanityData();
        executeAndAssertCommandSuccess(getSanityTestExecutionCommand());
        assertSanityTest();
    }

    protected abstract String getSanityTestExecutionCommand();

    protected abstract void assertSanityTest();

    @Import({BaseAppTest.springConfig.class})
    @Configuration
    protected static class EnrichedSourceSpringConfig {

    }
}
