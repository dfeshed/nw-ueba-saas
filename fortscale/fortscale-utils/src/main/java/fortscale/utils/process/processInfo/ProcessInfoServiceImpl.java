package fortscale.utils.process.processInfo;

import fortscale.utils.logging.Logger;
import fortscale.utils.process.processInfo.exceptions.ErrorAccessingPidFile;
import fortscale.utils.process.processType.ProcessType;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this service should update pid file with current process pid
 */

public class ProcessInfoServiceImpl implements ProcessInfoService {
    private static final Logger logger = Logger.getLogger(ProcessInfoServiceImpl.class);
    public static final int DEFAULT_MAX_PROCESS_INSTANCES = 15;
    public static final String JAVA_PROCESS_COMMAND = "java";

    // pidfiles base path (NOTE must match paths at LinuxCollectorsServicesImplProperties)
    private final String PID_BASE_FILE_PATH = "/var/run/fortscale";
    private final String CONFIG_PATH = "/home/cloudera/fortscale/config";
    private final String SEQUENCED_PID_FILE_NAME_FORMAT = "%s_%s.pid";

    // Process name
    private String processName;

    // Process group name
    private String processGroupName;

    // Process type
    private ProcessType processType;

    // Process PID
    private long pid;

    // Pid file path
    private String pidFilePath;

    // Pid file directory
    private String pidDir;

    // multiple process can run together. in this case we will create a sequenced instance pid files
    private boolean isMultiProcess;

    // the sequence number in case of multiProcess, default is 0;
    private String processInstanceNumber;

    // maximum process instances running at the same moment;
    private long maxProcessInstances;

    // std spring properties
    Properties springProperties = new Properties();

    // process exit code. 0 = exit normally
    private static int exitCode = 0;

    /**
     * c'tor
     *
     * @param processName
     * @param processGroupName
     * @param processType
     * @param isMultiProcess
     */
    public ProcessInfoServiceImpl(String processName, String processGroupName, ProcessType processType, boolean isMultiProcess) {
        // Keep the args
        this.processName = processName;
        this.processGroupName = processGroupName;
        this.processType = processType;
        this.isMultiProcess = isMultiProcess;


        // Get the process PID
        pid = Long.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);

        // Calc pidfile path
        pidDir = Paths.get(PID_BASE_FILE_PATH, processGroupName).toString();
        pidFilePath = Paths.get(pidDir, String.format("%s.pid", processName)).toString();
        processInstanceNumber = "";

        setMaxProcessInstances();

        this.springProperties = new Properties();

        logger.info("Creating ProcessInfoService: processName={} processGroupName={} pid={} pidDir={} processType={} isMultiProcess={}",
                processName, processGroupName, pid, pidDir, processType,isMultiProcess);

    }

    /**
     * syntactic sugar c'tor
     * @param processName
     * @param processGroupName
     * @param processType
     */
    public ProcessInfoServiceImpl(String processName, String processGroupName, ProcessType processType) {
        this(processName, processGroupName, processType, false);
    }

    /**
     * shut down hook deleting pid file at exit
     */
    private void registerShutdownHook() {
        ProcessInfoServiceImpl currentInstance = this;

        logger.debug("Registering shut down hook");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                // When process threads is done, its time for shutdown
                logger.info("Shutdown hook is launched. processName={} processGroupName={} pid={} pidFilePath={} processType={}",
                        processName, processGroupName, pid, pidFilePath, processType);
                // delete pid file at shutdown
                currentInstance.shutdown();
                logger.info("Process finished with exitCode={}",ProcessInfoServiceImpl.exitCode);
            }
        });
    }

    /**
     * Init the process info service.
     * <p>
     * It should be called as early as possible at process init phase
     * <p>
     * It does the following:
     * - Create the pidfile
     */
    public void init() {

        logger.debug("Init-ing ProcessInfoService: processName={} processGroupName={} pid={}",
                processName, processGroupName, pid);

        // Create the pidfile
        createPidFile();

        // register shutdown hook for pid cleanup
        registerShutdownHook();
    }

    /**
     * Shutdown the process info service.
     * <p>
     * It should be called as late as possible at process shutdown phase
     * <p>
     * It does the following:
     * - Delete the pidfile
     */
    public void shutdown() {

        logger.info("shutting down ProcessInfoService: processName={} processGroupName={} pid={}",
                processName, processGroupName, pid);

        // Delete the pidfile
        deletePidFile(pidFilePath);
    }

    /**
     * checks if process with pid is running
     *
     * @param pid
     * @return false if process is not running, true if cannot determine process status or if process is running java
     */
    public boolean isPidRunningAndIsJavaProcess(int pid) {
        try {
            Path processCommandFilePath = Paths.get(String.format("/proc/%d/comm",pid));

            if(!Files.exists(processCommandFilePath)) {
                logger.debug("process for pid={} is not running",pid);
                return false;
            }

            String command = Files.lines(processCommandFilePath).findFirst().get();
            if(!command.contains(JAVA_PROCESS_COMMAND))
            {
                logger.debug("process for pid={} is not a java process",pid);
                return false;
            }

            logger.debug("process with pid={} is running java",pid);
            return true;
        } catch (Exception e) {
            logger.warn("could not determine if pid is running {}", pid, e);
            return true;
        }
    }

    /**
     * reads pid from path
     *
     * @param file pid file
     * @return pid written in path
     */
    public int readPidFile(File file) {
        int pid = -1;
        try {
            pid = Integer.parseInt(Files.lines(file.toPath()).findFirst().get());
        } catch (IOException e) {
            logger.warn("could not read pid from file {}",file.getAbsolutePath(), e);
            return pid;
        }
        return pid;
    }

    /**
     * Adds the following properties to the spring context.
     * <p>
     * fortscale.process.name         - process name
     * fortscale.process.group.name   - process group name
     * fortscale.process.pid          - process PID
     * <p>
     * NOTE: this function must be called before context is refreshed
     *
     * @param context - Spring context
     */
    public void registerToSpringContext(AbstractApplicationContext context) {

        // fill properties object

        springProperties.put("fortscale.process.name", processName);
        springProperties.put("fortscale.process.pid", pid);
        springProperties.put("fortscale.process.group.name", processGroupName);
        springProperties.put("fortscale.process.type", processType);
        springProperties.put("fortscale.process.instance.number", processInstanceNumber);
        springProperties.put("fortscale.path.config", CONFIG_PATH);


        // Add the properties object to the spring context
        logger.info("Adding basic process properties to spring context: {}", springProperties.toString());

        PropertySource propertiesSource = new PropertiesPropertySource("basicProcess", springProperties);
        context.getEnvironment().getPropertySources().addLast(propertiesSource);
    }

    public void registerToSpringContext(AbstractApplicationContext context, String commands) {
        springProperties.put("fortscale.shell.commandline.commands",commands);
        registerToSpringContext(context);
    }

    /**
     *
     * @return list of files answering to the pattern ${processName}_${processInstanceNumber}.pid
     */
    protected File[] getSequencedPidFiles()
    {
        File dir = new File(pidDir);
        File[] foundFiles = dir.listFiles((dir1, name) -> {
            String regex = String.format("^%s_([\\d]{3})\\.pid", processName);
            return name.matches(regex);
        });

        return foundFiles;
    }

    /**
     * get all pid files matching the process name and directory
     *
     * @return pid files
     */
    protected File[] getAllProcessPidFiles() {
        File dir = new File(pidDir);
        File[] foundFiles = dir.listFiles((dir1, name) -> {
            String regex = String.format("^(%s).*pid", processName);
            return name.matches(regex);
        });

        return foundFiles;
    }

    /**
     * deletes non running processes pid files
     */
    protected void deleteNonRunningPidFiles()
    {
        File[] pidFiles = getAllProcessPidFiles();
        if (pidFiles != null) {
            // delete non-running process pid files
            Arrays.stream(pidFiles).forEach(file -> {
                int pidFileValue = readPidFile(file);
                if (pidFileValue != -1 && !isPidRunningAndIsJavaProcess(pidFileValue)) {
                    Path filePath = file.toPath();
                    try {
                        logger.warn("deleting pidFile={} of pid={} since process is not running", filePath.toString(), Files.lines(filePath).findFirst());
                    } catch (IOException e) {
                        logger.warn("could not read pid from pidFile={}", filePath.toString());
                    }
                    deletePidFile(file.getAbsolutePath());
                }
            });
        }
    }

    /**
     * old pid files of dead processes will be deleted, and a new one will be created with process instance number. i.e. process_1.pid
     */
    protected void handleMultiPidFile() {

        deleteNonRunningPidFiles();

        // non sequenced pid file is available
        if(!Files.exists(Paths.get(pidFilePath)))
        {
            return;
        }

        File[] sequencedPidFiles = getSequencedPidFiles();

        // sort running process pid file names by process instance number
        Arrays.sort(sequencedPidFiles, (o1, o2) -> {
            int n1 = extractProcessInstanceNumber(o1.getName());
            int n2 = extractProcessInstanceNumber(o2.getName());
            return Integer.compare(n1,n2);

        });

        // search for first available process instance number
        int fileProcessInstanceNumber = 1;

        for (File pidFile : sequencedPidFiles) {
            if (pidFile.getAbsolutePath().equals(pidFilePath)) {
                continue;
            }
            if (extractProcessInstanceNumber(pidFile.getName()) != fileProcessInstanceNumber) {
                break;
            }
            fileProcessInstanceNumber++;
        }

        if(fileProcessInstanceNumber<=maxProcessInstances)
        {
            processInstanceNumber = String.format("%03d",fileProcessInstanceNumber);
        }
        else
        {
            logger.warn("Process has high number of {} running instances, not recommended to run more than {} at the same time"
                    , sequencedPidFiles.length,maxProcessInstances);
            processInstanceNumber = String.format("%03d",maxProcessInstances);
        }

        this.pidFilePath = Paths.get(pidDir, String.format(SEQUENCED_PID_FILE_NAME_FORMAT, processName, String.format("%03d",fileProcessInstanceNumber))).toString();
    }

    /**
     *
     * @param name - pid file name, should be from patter processName_InstanceNumber.pid
     * @return instance number of pid file
     */
    public static int extractProcessInstanceNumber(String name) {
        int instanceNumber;
        try {
            String regex = ".*_([\\d]{3})\\.pid";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(name);
            if (!matcher.find()) {
                return 0;
            }
            instanceNumber = Integer.parseInt(matcher.group(1));
        } catch (Exception e) {
            instanceNumber = 0; // if filename does not match the format
            logger.info("unable to parse process instance from pid file name={}", name);
            // then default to 0
        }
        return instanceNumber;
    }


    /**
     * creates pid file
     */
    protected void createPidFile() {
        if (isMultiProcess) {
            handleMultiPidFile();
        }
        long pid = getCurrentPid();
        logger.info("executing: create pid file {}", pidFilePath);
        PrintWriter writer;
        try {
            File pidFile = new File(pidFilePath);
            File pidDirectory = pidFile.getParentFile();
            if (pidDirectory != null) {
                if(!pidDirectory.exists())
                {
                    boolean dirCreated = pidFile.getParentFile().mkdirs();
                    if(!dirCreated)
                    {
                        String msg = String.format("error creating pid directory %s", pidDirectory.getPath());
                        throw new RuntimeException(msg);
                    }
                }
            }
            if (pidFile.exists()) {
                logger.warn("Pid file: {} already exist... overriding file", pidFilePath);
            }
            writer = new PrintWriter(pidFile);
            writer.println(pid);
            writer.close();
            logger.info("pid file {} created successfully", pidFilePath);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ErrorAccessingPidFile(pidFilePath);
        }
    }

    /**
     * checks current process pid
     *
     * @return current process pid
     */
    @Override
    public long getCurrentPid() {
        return pid;
    }

    @Override
    public String getCurrentProcessInstanceNumber() {
        return processInstanceNumber;
    }


    public static void exit(int exitCode) {
        ProcessInfoServiceImpl.exitCode =exitCode;
        Thread.currentThread().interrupt();

        System.exit(ProcessInfoServiceImpl.exitCode );
    }

    /**
     * updates the maximum amount of process instances from system properties, default value is inserted if not exists
     */
    public void setMaxProcessInstances() {
        String maxProcessInstancesStr= System.getProperty("fortscale.process.maxProcessInstances");
        if(maxProcessInstancesStr==null) {
            maxProcessInstances = DEFAULT_MAX_PROCESS_INSTANCES;
        }
        else
        {
            maxProcessInstances=Long.parseLong(maxProcessInstancesStr);
        }
        logger.warn("maxProcessInstances={}",maxProcessInstances);
    }

    /**
     * deletes pid file
     * @param path file path
     */
    protected void deletePidFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
            logger.info("Deleted pid file: {}", path);
        } catch (IOException e) {
            logger.error(String.format("ERROR: failed to delete pidfile %s", path), e);
        }
    }

}
