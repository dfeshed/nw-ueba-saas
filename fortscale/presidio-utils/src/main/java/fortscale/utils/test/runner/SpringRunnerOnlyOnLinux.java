package fortscale.utils.test.runner;

import org.apache.commons.lang3.SystemUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by barak_schuster on 10/23/17.
 */
public class SpringRunnerOnlyOnLinux extends SpringJUnit4ClassRunner {


    /**
     * Construct a new {@code SpringJUnit4ClassRunner} and initialize a
     * {@link TestContextManager} to provide Spring testing functionality to
     * standard JUnit tests.
     *
     * @param clazz the test class to be run
     * @see #createTestContextManager(Class)
     */
    public SpringRunnerOnlyOnLinux(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    public void run(RunNotifier notifier) {
        if (!SystemUtils.IS_OS_WINDOWS) {
            super.run(notifier);
        }
    }
}
