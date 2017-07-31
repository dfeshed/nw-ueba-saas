package presidio.webapp.dto;

public abstract class ResponseBean {
    protected String errorMessage;

    public ResponseBean(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ResponseBean() {
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
