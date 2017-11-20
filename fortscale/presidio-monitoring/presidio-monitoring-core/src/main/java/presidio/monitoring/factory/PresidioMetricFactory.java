package presidio.monitoring.factory;


public class PresidioMetricFactory {

    private static String applicationName;

    public PresidioMetricFactory(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
