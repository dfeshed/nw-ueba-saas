package fortscale.utils.standardProcess.pidService;

import fortscale.utils.logging.Logger;
import fortscale.utils.standardProcess.pidService.exceptions.ErrorAccessingPidFile;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * this service should update pid file with current process pid
 */
public class PidService {
    private static final Logger logger = Logger.getLogger(PidService.class);

    private final String PID_FILE_EXTENSION="pid";

    private String pidFilePath;
    /**
     * Ctor
     * @param pidDir a directory containing all pid files
     */
    public PidService(String pidDir,String groupPidFolderName, String pidFileName) {
        this.pidFilePath= String.format("%s/%s/%s.%s",pidDir,groupPidFolderName,pidFileName,PID_FILE_EXTENSION);
        this.createPidFile();
    }

    private void createPidFile()
    {
        long pid = getCurrentPid();
        logger.info("EXECUTING: create pid file {}",pidFilePath);
        PrintWriter writer;
        try {
            writer = new PrintWriter(pidFilePath, "UTF-8");
            writer.println(pid);
            writer.close();
            logger.info("FINISHED: creating pid file {} successfully",pidFilePath);

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new ErrorAccessingPidFile(pidFilePath);
        }
    }

    private long getCurrentPid()
    {
        return Long.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    private void shutdown()
    {
        try {
            Files.delete(Paths.get(pidFilePath));
        } catch (IOException e) {
            logger.error(String.format("ERROR: failed to delete pidfile %s",pidFilePath),e);
        }
    }


}
