package fortscale.utils.process.processInfo;

/**
 * Created by cloudera on 5/30/16.
 */
public interface ProcessInfoService {

    /**
     * creates pid file
     */
    void createPidFile();

    /**
     *
     * @return current process id
     */
    long getCurrentPid();

    /**
     * a shut down method that deletes pid file
     */
    void deletePidFile();


}
