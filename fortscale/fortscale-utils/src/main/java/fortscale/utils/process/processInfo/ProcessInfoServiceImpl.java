package fortscale.utils.process.processInfo;

import fortscale.utils.logging.Logger;
import fortscale.utils.process.processInfo.exceptions.ErrorAccessingPidFile;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * this service should update pid file with current process pid
 */
public class ProcessInfoServiceImpl implements ProcessInfoService {
    private static final Logger logger = Logger.getLogger(ProcessInfoServiceImpl.class);



    private String pidFilePath="";
    /**
     * Ctor
     * @param pidFilePath pid full file path
     */
    public ProcessInfoServiceImpl(String pidFilePath) {
        this.pidFilePath= pidFilePath;
    }

    /**
     * creates pid file
     */
    @Override
    public void createPidFile() {
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
        long pid = Long.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        logger.info("current pid: {}",pid);
        return pid;
    }


    /**
     * deletes pid file
     */
    @Override
    public void deletePidFile()
    {
        try {
            Files.delete(Paths.get(pidFilePath));
            logger.info("Deleted pid file: {}",pidFilePath);
        } catch (IOException e) {
            logger.error(String.format("ERROR: failed to delete pidfile %s",pidFilePath),e);
        }
    }


}
