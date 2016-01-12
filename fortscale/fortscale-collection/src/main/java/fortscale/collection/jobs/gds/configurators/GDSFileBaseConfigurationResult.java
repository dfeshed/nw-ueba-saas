package fortscale.collection.jobs.gds.configurators;

import java.util.Set;

/**
 * @author gils
 * 10/01/2016
 */
public class GDSFileBaseConfigurationResult implements GDSConfigurationResult<String> {

    private boolean success;
    private String errorMessage;
    private Set<String> affectedConfigFileNames;

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }

    public Set<String> getAffectedConfigDescriptors() {
        return affectedConfigFileNames;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setAffectedConfigDescriptors(Set<String> affectedFiles) {
        this.affectedConfigFileNames = affectedFiles;
    }
}
