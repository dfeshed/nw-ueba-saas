package fortscale.utils.process.processInfo;

import org.springframework.context.support.AbstractApplicationContext;


public interface ProcessInfoService {

    /**
     * Init the process info service.
     *
     * It should be called as early as possible at process init phase
     *
     */
    void init();

    /**
     * Shutdown the process info service.
     *
     * It should be called as late as possible at process shutdown phase
     *
     * It does the following:
     *   - Delete the pidfile
     *
     */
    void shutdown();

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
    void registerToSpringContext(AbstractApplicationContext context);


    /**
     * Adds the following properties to the spring context.
     * <p>
     * fortscale.process.name         - process name
     * fortscale.process.group.name   - process group name
     * fortscale.process.pid          - process PID
     * fortscale.shell.commandline.commands - shell commands
     * <p>
     * NOTE: this function must be called before context is refreshed
     *
     * @param context - Spring context
     */
    void registerToSpringContext(AbstractApplicationContext context, String commands);


    /**
      *
      * @return current process id
      */
    long getCurrentPid();

    /**
     * if multiProcesses are enabled, meaning process can have more then one instance at the same time,
     * it will get a sequence number.
     * if multiProcesses is disabled (daemon) then the sequence is 0
     * @return process sequence number
     */
    String getCurrentProcessInstanceNumber();


}
