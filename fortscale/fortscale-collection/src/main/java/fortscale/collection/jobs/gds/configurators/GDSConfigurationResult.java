package fortscale.collection.jobs.gds.configurators;

import java.util.Set;

/**
 * @author gils
 * 10/01/2016
 */
public interface GDSConfigurationResult<T> {
    boolean isSuccess();
    String errorMessage();
    Set<T> getAffectedConfigList();
}
