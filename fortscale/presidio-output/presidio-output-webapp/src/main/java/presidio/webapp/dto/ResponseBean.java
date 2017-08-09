package presidio.webapp.dto;

public abstract class ResponseBean {
    protected String errorMessage;
    protected int status;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
