package fortscale.utils.process.pidService;

import fortscale.utils.logging.Logger;
import fortscale.utils.process.pidService.exceptions.ErrorAccessingPidFile;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * this service should update pid file with current process pid
 */
public class PidService {
    private static final Logger logger = Logger.getLogger(PidService.class);

    private final String PID_FILE_EXTENSION="pid";

    private String pidFilePath="";
    /**
     * Ctor
     * @param pidDir a directory containing all pid files
     */
    public PidService(String pidDir,String groupPidFolderName, String pidFileName) {
        if (!pidDir.isEmpty()) {
            this.pidFilePath=this.pidFilePath.concat(String.format("%s/", pidDir));
        }
        if (!groupPidFolderName.isEmpty() ) {
            this.pidFilePath=this.pidFilePath.concat(String.format("%s/", groupPidFolderName));
        }
        this.pidFilePath=this.pidFilePath.concat(String.format("%s.%s",pidFileName,PID_FILE_EXTENSION));
        this.createPidFile();
    }

    /**
     * creates pid file
     */
    private void createPidFile()
    {
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
    public static long getCurrentPid()
    {
        long pid = Long.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        logger.info("current pid: {}",pid);
        return pid;
    }

    /**
     * deletes pid file
     */
    private void shutdown()
    {
        try {
            Files.delete(Paths.get(pidFilePath));
            logger.info("Deleted pid file: {}",pidFilePath);
        } catch (IOException e) {
            logger.error(String.format("ERROR: failed to delete pidfile %s",pidFilePath),e);
        }
    }


}
