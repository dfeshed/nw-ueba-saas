package presidio.output.domain.records.alerts;


public class AlertPriority {

    private String name;
    private int priority;

    public AlertPriority(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }
}
