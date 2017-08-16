package presidio.manager.api.records;


import java.util.ArrayList;
import java.util.List;

public class ValidationResults {

    private List<ConfigurationBadParamDetails> errorsList;

    private boolean isValid;

    public ValidationResults() {
        this.errorsList = new ArrayList<>();
        this.isValid = true;
    }

    public ValidationResults(List<ConfigurationBadParamDetails> errorsList) {
        this.errorsList = new ArrayList<>();
        addErrors(errorsList);
    }

    public ValidationResults(ConfigurationBadParamDetails error) {
        this.errorsList = new ArrayList<>();
        addError(error);
    }

    public boolean isValid() {
        return this.isValid;
    }

    public void addErrors(List<ConfigurationBadParamDetails> errorsList) {
        if (errorsList != null && !errorsList.isEmpty()) {
            this.errorsList.addAll(errorsList);
            this.isValid = false;
        }
    }

    public void addError(ConfigurationBadParamDetails errorsList) {
        if (errorsList!=null) {
            this.errorsList.add(errorsList);
            this.isValid = false;
        }
    }

    public List<ConfigurationBadParamDetails> getErrorsList() {
        return errorsList;
    }
}
