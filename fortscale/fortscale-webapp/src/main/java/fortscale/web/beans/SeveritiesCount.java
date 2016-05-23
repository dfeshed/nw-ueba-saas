package fortscale.web.beans;

import fortscale.domain.core.Severity;

/**
 * Created by shays on 23/05/2016.
 */
public class SeveritiesCount {

    private Severity severity;
    private int count;

    public SeveritiesCount(){

    }

    public SeveritiesCount(Severity severity, int count) {
        this.severity = severity;
        this.count = count;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
