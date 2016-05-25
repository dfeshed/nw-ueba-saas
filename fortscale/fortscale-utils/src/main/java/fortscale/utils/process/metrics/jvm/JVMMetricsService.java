package fortscale.utils.process.metrics.jvm;


/**
 * Service is used for jvm metrics statistics updates
 */
public interface JVMMetricsService {
    /**
     * update memory statistics such as free memory etc.
     */
    void collectMemoryStats();

    /**
     * update garbage collectors statistics
     */
    void collectGarbageCollectorsStats();
}
