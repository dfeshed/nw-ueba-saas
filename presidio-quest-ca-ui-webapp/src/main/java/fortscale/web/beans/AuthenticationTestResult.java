package fortscale.web.beans;

/**
 * Created by shays on 18/12/2016.
 */
public class AuthenticationTestResult {

    private boolean result;
    private String reason;

    public AuthenticationTestResult(boolean result, String reason) {
        this.result = result;
        this.reason = reason;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
