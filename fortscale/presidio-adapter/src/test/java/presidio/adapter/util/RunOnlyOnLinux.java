package presidio.adapter.util;

import org.apache.commons.lang3.SystemUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class RunOnlyOnLinux extends BlockJUnit4ClassRunner {


    /**
     * Creates a BlockJUnit4ClassRunner to run {@code type}
     *
     * @param type
     * @throws InitializationError if the test class is malformed.
     */
    public RunOnlyOnLinux(Class type) throws InitializationError {
        super(type);
    }

    @Override
    public void run(RunNotifier notifier) {
        if (!SystemUtils.IS_OS_WINDOWS) {
            super.run(notifier);
        }
    }
}
