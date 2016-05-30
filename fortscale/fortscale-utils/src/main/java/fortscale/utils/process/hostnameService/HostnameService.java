package fortscale.utils.process.hostnameService;

/**
 * service holds current process hostname. hostname may change during process run.
 */
public interface HostnameService {
    /**
     * @return current machine hostname
     */
    String getHostname();
}
