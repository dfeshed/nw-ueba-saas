package presidio.manager.api.records;


import java.util.ArrayList;
import java.util.List;

public class ValidationResponse {

    private List<ConfigurationBadParamDetails> errorsList;

    private boolean isValid;

    public ValidationResponse() {
        this.errorsList = new ArrayList<>();
        this.isValid = true;
    }

    public ValidationResponse(List<ConfigurationBadParamDetails> errorsList) {
        this.errorsList = new ArrayList<>();
        addToErrorList(errorsList);
    }

    public ValidationResponse(ConfigurationBadParamDetails error) {
        this.errorsList = new ArrayList<>();
        addToErrorList(error);
    }

    public boolean isValid() {
        return this.isValid;
    }

    public void addToErrorList(List<ConfigurationBadParamDetails> errorsList) {
        if (!errorsList.isEmpty()) {
            this.errorsList.addAll(errorsList);
            this.isValid = false;
        }
    }

    public void addToErrorList(ConfigurationBadParamDetails errorsList) {
        this.errorsList.add(errorsList);
        this.isValid = false;
    }

    public List<ConfigurationBadParamDetails> getErrorsList() {
        return errorsList;
    }
}
