package presidio.webapp.dto;

import java.io.Serializable;

public abstract class ResponseBean implements Serializable {
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
