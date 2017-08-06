package presidio.ade.test.utils.tests;

import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.test.utils.EnrichedEventsGenerator;
import presidio.ade.test.utils.EnrichedEventsGeneratorConfig;
import presidio.data.generators.common.GeneratorException;

@Category(ModuleTestCategory.class)
public abstract class EnricheSourceBaseAppTest extends BaseAppTest {
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

    @Import({EnrichedEventsGeneratorConfig.class, BaseAppTest.springConfig.class})
    @Configuration
    protected static class EnrichedSourceSpringConfig {

    }
}

