package fortscale.web.beans;

/**
 * Created by shays on 11/12/2016.
 */
public class ResponseEntityMessage {
    private String message;

    public ResponseEntityMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
