package fortscale.utils.process.standardProcess;

import fortscale.utils.logging.Logger;
import fortscale.utils.process.processInfo.ProcessInfoService;
import fortscale.utils.process.processInfo.ProcessInfoServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Properties;

/**
 * Standard process infrastracture to handle common ops. such as loading spring context
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public abstract class StandardProcessBase {
    private static final Logger logger = Logger.getLogger(StandardProcessBase.class);
    private String processName;
    private long pid;
    private String groupName;
    private ProcessInfoService processInfoService;
    private final String PID_BASE_FILE_PATH = "/var/run/fortscale";
    private final String PID_FILE_EXTENSION = "pid";

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
        logger.info("Spring context refresh duration: {}", duration);
    }

    /**
     * add standard properties to spring environment. i.e.: process name and pid
     *
     * @param annotationConfigApplicationContext spring context
     */
    protected void addStandardProperties(AnnotationConfigApplicationContext annotationConfigApplicationContext) {
        Properties properties = new Properties();

        properties.put("fortscale.process.name", processName);
        properties.put("fortscale.process.pid", pid);
        properties.put("fortscale.process.group.name", groupName);
        PropertySource propertiesSource = new PropertiesPropertySource(StandardProcessBase.class.getName(), properties);

        annotationConfigApplicationContext.getEnvironment().getPropertySources().addLast(propertiesSource);
    }

    /**
     * initiate standard process
     */
    protected void mainEntry(String[] args) throws Exception {

        processName = getProcessName();
        groupName = getProcessGroupName();

        // create pid file
        String pidFilePath = Paths.get(PID_BASE_FILE_PATH, groupName, String.format("%s.%s", processName, PID_FILE_EXTENSION)).toString();
        processInfoService = new ProcessInfoServiceImpl(pidFilePath);
        processInfoService.createPidFile();

        // get current pid
        pid = processInfoService.getCurrentPid();

        logger.info("Process PID: {} , process name: {}, group name: {}", pid, processName, groupName);

        logger.info("Process arguments: {}", Arrays.toString(args));
        logger.info("Process classpath: \n{}", getClassPath().replaceAll(File.pathSeparator, ",\n"));


        baseContextInit();

        try {
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("Failed to join current thread", e);
            processInfoService.deletePidFile();
            throw e;
        }
        // when process threads joins, its time for shudown
        Shutdown();

        // return code for successful process finish
        int returnCode = 0;
        logger.info("Process finished with return code: {}", returnCode);
        System.exit(returnCode);
    }

    /**
     * cleaning up before shutting down
     */
    protected void Shutdown() {
        // delete pid file at shutdown
        processInfoService.deletePidFile();
    }

    /**
     * @return process group name
     */
    protected String getProcessGroupName() {
        return "";
    }

    /**
     * get process classpath
     *
     * @return string containing full classpath. can be splited using .split(File.pathSeparator)
     */
    public static String getClassPath() {
        return System.getProperty("java.class.path");
    }

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
     * enables process group to make specific changes at spring context before loading the context accross the system
     * typically should not be overridden. this implementation does nothing by default.
     * this method is called before springContextProcessFixUp
     *
     * @param springContext spring context to edit
     */
    protected void springContextGroupFixUp(AnnotationConfigApplicationContext springContext) {
    }

    /**
     * enables process to make specific changes at spring context before loading the context accross the system
     * typically should not be overridden. this implementation does nothing by default.
     * this method is called after springContextGroupFixUp
     *
     * @param springContext spring context to edit
     */
    protected void springContextProcessFixUp(AnnotationConfigApplicationContext springContext) {
    }


}
