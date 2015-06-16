package fortscale.streaming.aggregation.feature;

/**
 * Created by amira on 15/06/2015.
 */
public class Feature {
    private String name;
    private Object value;

    public Feature(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setName(String name) { this.name = name; }
}
