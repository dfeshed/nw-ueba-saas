package presidio.ade.test.utils.tests;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.test.utils.EnrichedEventsGenerator;
import presidio.ade.test.utils.EnrichedEventsGeneratorConfig;
import presidio.ade.test.utils.generators.EnrichedFileGeneratorConfig;
import presidio.data.generators.common.GeneratorException;

public abstract class EnrichedFileSourceBaseAppTest extends BaseAppTest {
    @Autowired
    protected EnrichedEventsGenerator enrichedEventsGenerator;

    /**
     * Generate 2 events per hour along 24 hours for 2 days.
     * Operation type of all the events is "open"
     * execute sanitytest command
     */
    @Test
    public void sanityTest() throws GeneratorException {
        enrichedEventsGenerator.generateAndPersistSanityData();
        executeAndAssertCommandSuccess(getSanityTestExecutionCommand());
        assertSanityTest();
    }

    protected abstract String getSanityTestExecutionCommand();

    protected abstract void assertSanityTest();

    @Import({EnrichedFileGeneratorConfig.class, BaseAppTest.springConfig.class})
    @Configuration
    protected static class EnrichedSourceSpringConfig {

    }
}
