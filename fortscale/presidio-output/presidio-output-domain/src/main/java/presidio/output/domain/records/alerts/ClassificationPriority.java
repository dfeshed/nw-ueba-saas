package presidio.output.domain.records.alerts;


public class ClassificationPriority {

    private String name;
    private int priority;

    public ClassificationPriority(String name, int priority) {
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
