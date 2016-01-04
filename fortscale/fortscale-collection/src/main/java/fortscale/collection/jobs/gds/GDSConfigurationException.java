package fortscale.collection.jobs.gds;

/**
 * Generic data source configuration exception representation
 *
 * @author gils
 * 03/01/2016
 */
public class GDSConfigurationException extends Exception{
    public GDSConfigurationException(String message) {
        super(message);
    }

    public GDSConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
