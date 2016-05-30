package fortscale.utils.process.standardProcess;

import fortscale.utils.logging.Logger;
import fortscale.utils.process.pidService.PidService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.Duration;
import java.time.Instant;

/**
 * Standard process infrastracture to handle common ops. such as loading spring context
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public abstract class StandardProcessBase {
    private static final Logger logger = Logger.getLogger(StandardProcessBase.class);

    /**
     * update spring context with configuration class
     */
    protected void baseContextInit(){
        logger.info("Loading spring context");

        // get process daemon configuration class
        Class configurationClass = getProcessConfigurationClasses();
        logger.info("Process configuration class: {}",configurationClass.toString());

        // create new context
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();

        // register shutdown hook
        annotationConfigApplicationContext.registerShutdownHook();

        // register configuration class to spring context
        annotationConfigApplicationContext.register(configurationClass);

        logger.info("Executing spring context group fixup");
        // let process group edit spring context
        springContextGroupFixUp(annotationConfigApplicationContext);

        logger.info("Executing spring context process fixup");
        // let process edit spring context
        springContextProcessFixUp(annotationConfigApplicationContext);

        // refresh context
        Instant beforeRefresh = Instant.now();
        annotationConfigApplicationContext.refresh();
        Duration duration = Duration.between(beforeRefresh, Instant.now());
        logger.info("Spring context refresh duration: {}",duration);

    }

    /**
     * initiate standard process
     */
    protected int mainEntry(String [] args) throws Exception {
        logger.info("Process arguments: {}",args);
        logger.info("Process classpath: {}", getClassPath());
        logger.info("Process PID: {}", PidService.getCurrentPid());

        baseContextInit();
        try {
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("Failed to join current thread",e);
            throw e;
        }

        logger.info("Process is shutting down");
        // return code for successful process finish
        return 0;
    }

    /**
     * get process classpath
     * @return string containing full classpath. can be splited using .split(File.pathSeparator)
     */
    public static String getClassPath()
    {
        return System.getProperty("java.class.path");
    }

    /**
     * update process specific configuration classes
     */
    protected abstract Class getProcessConfigurationClasses();

    /**
     * edit spring context at group level
     * @param springContext spring context to edit
     */
    protected void springContextGroupFixUp(AnnotationConfigApplicationContext springContext)
    {
    }

    /**
     * edit spirng context at process level
     * @param springContext spring context to edit
     */
    protected void springContextProcessFixUp(AnnotationConfigApplicationContext springContext)
    {
    }



}
