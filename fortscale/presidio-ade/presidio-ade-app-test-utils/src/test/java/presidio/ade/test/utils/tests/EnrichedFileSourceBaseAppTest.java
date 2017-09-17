package presidio.ade.test.utils.tests;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.test.utils.EventsGenerator;
import presidio.data.generators.common.GeneratorException;

import java.util.List;

public abstract class EnrichedFileSourceBaseAppTest extends BaseAppTest {

    private static final int INTERVAL=30;

    @Autowired
    protected EventsGenerator eventsGenerator;

    /**
     * Generate 2 events per hour along 24 hours for 2 days.
     * Operation type of all the events is "open"
     * execute sanitytest command
     */
    @Test
    public void sanityTest() throws GeneratorException {
        List generatedData = eventsGenerator.generateAndPersistSanityData(getInterval());
        executeAndAssertCommandSuccess(getSanityTestExecutionCommand());
        assertSanityTest(generatedData);
    }

    protected int getInterval(){
        return INTERVAL;
    }

    protected abstract String getSanityTestExecutionCommand();

    protected abstract void assertSanityTest(List generatedData) throws GeneratorException;

    @Import({BaseAppTest.springConfig.class})
    @Configuration
    protected static class EnrichedSourceSpringConfig {

    }
}
