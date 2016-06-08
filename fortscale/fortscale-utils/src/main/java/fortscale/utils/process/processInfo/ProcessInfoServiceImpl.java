package fortscale.utils.process.processInfo;

import fortscale.utils.logging.Logger;
import fortscale.utils.process.processInfo.exceptions.ErrorAccessingPidFile;
import fortscale.utils.process.processType.ProcessType;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * this service should update pid file with current process pid
 */

public class ProcessInfoServiceImpl implements ProcessInfoService {
    private static final Logger logger = Logger.getLogger(ProcessInfoServiceImpl.class);

    // pidfiles base path (NOTE must match paths at LinuxCollectorsServicesImplProperties)
    private final String PID_BASE_FILE_PATH = "/var/run/fortscale";
    private final String CONFIG_PATH = "/home/cloudera/fortscale/config";


    // Process name
    String processName;

    // Process group name
    String processGroupName;

    // Process type
    ProcessType processType;

    // Process PID
    long pid;

    private String pidFilePath;


    /**
     * ctor
     *
     * @param processName process name
     * @param processGroupName process group name
     */
    public ProcessInfoServiceImpl(String processName, String processGroupName, ProcessType processType) {

        // Keep the args
        this.processName = processName;
        this.processGroupName = processGroupName;
        this.processType=processType;

        // Get the process PID
        pid = Long.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);

        // Calc pidfile path
        pidFilePath = Paths.get(PID_BASE_FILE_PATH, processGroupName, String.format("%s.pid", processName)).toString();

        logger.info("Creating ProcessInfoService: processName={} processGroupName={} pid={} pidFilePath={} processType={}",
                    processName, processGroupName, pid, pidFilePath, processType);

        // register shutdown hook for pid cleanup
        registerShutdownHook();
    }


    private void registerShutdownHook()
    {
        ProcessInfoServiceImpl currentInstance = this;

        logger.debug("Registering shut down hook");
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {

                // When process threads is done, its time for shutdown
                logger.info("Shutdown hook is launched. processName={} processGroupName={} pid={} pidFilePath={} processType={}",
                        processName, processGroupName, pid, pidFilePath, processType);
                // delete pid file at shutdown
                currentInstance.shutdown();
            }
        });
    }

    /**
     * Init the process info service.
     *
     * It should be called as early as possible at process init phase
     *
     * It does the following:
     *   - Create the pidfile
     *
     *
     */
    public void init() {

        logger.debug("Init-ing ProcessInfoService: processName={} processGroupName={} pid={}",
                     processName, processGroupName, pid);

        // Create the pidfile
        createPidFile();
    }

    /**
     * Shutdown the process info service.
     *
     * It should be called as late as possible at process shutdown phase
     *
     * It does the following:
     *   - Delete the pidfile
     *
     */
    public void shutdown() {

        logger.info("shutting down ProcessInfoService: processName={} processGroupName={} pid={}",
                processName, processGroupName, pid);

        // Delete the pidfile
        deletePidFile();
    }

    /**
     * Adds the following properties to the spring context.
     *
     *   fortscale.process.name         - process name
     *   fortscale.process.group.name   - process group name
     *   fortscale.process.pid          - process PID
     *
     * NOTE: this function must be called before context is refreshed
     *
     * @param context           - Spring context

     */
    public void registerToSpringContext(AbstractApplicationContext context) {

        // Create and fill properties object
        Properties properties = new Properties();

        properties.put("fortscale.process.name", processName);
        properties.put("fortscale.process.pid", pid);
        properties.put("fortscale.process.group.name", processGroupName);
        properties.put("fortscale.process.type", processType);
        properties.put("fortscale.path.config", CONFIG_PATH);


        // Add the properties object to the spring context
        logger.info("Adding basic process properties to spring context: {}", properties.toString() );

        PropertySource propertiesSource = new PropertiesPropertySource("basicProcess", properties);
        context.getEnvironment().getPropertySources().addLast(propertiesSource);
    }


    /**
     * creates pid file
     */
    protected void createPidFile() {
        long pid = getCurrentPid();
        logger.info("EXECUTING: create pid file {}",pidFilePath);
        PrintWriter writer;
        try {
            File pidFile= new File(pidFilePath);
            if (pidFile.getParentFile()!=null) {
                pidFile.getParentFile().mkdirs();
            }
            if (pidFile.exists())
            {
                logger.warn("Pid file: {} already exist... overriding file",pidFilePath);
            }
            writer = new PrintWriter(pidFile);
            writer.println(pid);
            writer.close();
            logger.info("FINISHED: creating pid file {} successfully",pidFilePath);

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new ErrorAccessingPidFile(pidFilePath);
        }
    }

    /**
     * checks current process pid
     * @return current process pid
     */
    @Override
    public long getCurrentPid()
    {
        return pid;
    }


    /**
     * deletes pid file
     */
    protected void deletePidFile()
    {
        try {
            Files.delete(Paths.get(pidFilePath));
            logger.info("Deleted pid file: {}",pidFilePath);
        } catch (IOException e) {
            logger.error(String.format("ERROR: failed to delete pidfile %s",pidFilePath),e);
        }
    }

}
