package fortscale.web.rest.errorhandler;

/**
 * Created by shays on 08/05/2016.
 */
public class ErrorMessage {
    private String attribute;
    private String message;

    public ErrorMessage() {
    }

    public ErrorMessage(String attribute, String message) {
        this.attribute = attribute;
        this.message = message;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
