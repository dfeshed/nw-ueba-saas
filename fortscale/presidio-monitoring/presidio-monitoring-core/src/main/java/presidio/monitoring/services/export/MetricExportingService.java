package presidio.monitoring.services.export;


public interface MetricExportingService {

    void exportApplicationMetrics();

    void exportAllMetrics();

    void exportSystemMetrics();
}
