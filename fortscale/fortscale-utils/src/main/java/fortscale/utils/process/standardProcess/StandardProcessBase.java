package fortscale.utils.process.standardProcess;

import fortscale.utils.logging.Logger;
import fortscale.utils.process.processType.ProcessType;
import fortscale.utils.process.processInfo.ProcessInfoService;
import fortscale.utils.process.processInfo.ProcessInfoServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.net.URL;
import java.net.URLClassLoader;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;


/**
 * Standard process infrastracture to handle common ops. such as loading spring context
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public abstract class StandardProcessBase {
    private static final Logger logger = Logger.getLogger(StandardProcessBase.class);
    private String processName;
    private long pid;
    private String groupName;
    ProcessType processType;
    private ProcessInfoService processInfoService;


    /**
     * update spring context with configuration class
     */
    protected void baseContextInit() {

        // get process daemon configuration class
        Class configurationClass = getProcessConfigurationClasses();
        logger.info("Process spring configuration class: {}", configurationClass.getName());

        // create new context
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();

        // register shutdown hook
        annotationConfigApplicationContext.registerShutdownHook();

        // add standard properties
        addStandardProperties(annotationConfigApplicationContext);

        // register configuration class to spring context
        annotationConfigApplicationContext.register(configurationClass);

        logger.debug("Executing spring context group fixup");

        // let process group edit spring context
        springContextGroupFixUp(annotationConfigApplicationContext);

        logger.debug("Executing spring context process fixup");
        // let process edit spring context
        springContextProcessFixUp(annotationConfigApplicationContext);

        // refresh context
        Instant beforeRefresh = Instant.now();
        annotationConfigApplicationContext.refresh();
        Duration duration = Duration.between(beforeRefresh, Instant.now());
        logger.info("Spring context refresh duration: {}", duration);
    }

    /**
     * add standard properties to spring environment. i.e.: process name and pid
     *
     * @param annotationConfigApplicationContext spring context
     */
    protected void addStandardProperties(AnnotationConfigApplicationContext annotationConfigApplicationContext) {
        processInfoService.registerToSpringContext(annotationConfigApplicationContext);
    }

    /**
     * initiate standard process
     */
    protected void mainEntry(String[] args) throws Exception {

        processName = getProcessName();
        groupName = getProcessGroupName();
        processType = getProcessType();

        // create pid file
        processInfoService = new ProcessInfoServiceImpl(processName, groupName,processType);

        // get current pid
        pid = processInfoService.getCurrentPid();

        logger.info("Process PID: {} , process name: {}, group name: {}, process type: {}",
                pid, processName, groupName,processType);
        logger.info("Process arguments: {}", Arrays.toString(args));
        logger.info("Process classpath: \n{}", getClassPathAsString());

        processInfoService.init();

        baseContextInit();

        try {
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("Failed to join current thread", e);
            throw e;
        }

        // process return code. Assume for successful process execution
        int returnCode = 0;

        logger.info("Process finished with return code: {}", returnCode);
        System.exit(returnCode);
    }


    /**
     * get process classpath
     *
     * @return string containing full classpath. can be splited using .split(File.pathSeparator)
     */
    public static String getClassPathAsString() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) cl).getURLs();

        StringBuilder sb = new StringBuilder();

        for(URL url: urls)
        {
            sb.append(url.getFile());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * get process Type, whether it is a utility or a daemon
     * @return process type
     */
    protected abstract ProcessType getProcessType();

    /**
     * update process specific configuration classes
     */
    protected abstract Class getProcessConfigurationClasses();

    /**
     * Full process name
     *
     * @return process name
     */
    protected abstract String getProcessName();

    /**
     * @return process group name
     */
    protected abstract String getProcessGroupName();

    /**
     * enables process group to make specific changes at spring context before loading the context across the system
     * typically should not be overridden. this implementation does nothing by default.
     * this method is called before springContextProcessFixUp
     *
     * @param springContext spring context to edit
     */
    protected void springContextGroupFixUp(AnnotationConfigApplicationContext springContext) {
    }

    /**
     * enables process to make specific changes at spring context before loading the context across the system
     * typically should not be overridden. this implementation does nothing by default.
     * this method is called after springContextGroupFixUp
     *
     * @param springContext spring context to edit
     */
    protected void springContextProcessFixUp(AnnotationConfigApplicationContext springContext) {
    }


}
