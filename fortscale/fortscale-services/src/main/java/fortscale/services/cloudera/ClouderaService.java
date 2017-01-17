package fortscale.services.cloudera;

/**
 * Created by barak_schuster on 1/17/17.
 */
public interface ClouderaService {
    boolean start(String serviceName);
    boolean isInstalled(String serviceName);
    boolean isStarted(String serviceName);
    boolean isStopped(String serviceName);
    boolean stop(String serviceName);
}
