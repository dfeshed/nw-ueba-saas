package presidio.monitoring.elastic.records;



import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Metric {

    private String name;
    private Number value;
    private Instant time;
    private Set<String> tags;
    private String unit;
    private boolean reportOneTime;

    public Metric(String name, Number value, Set<String> tags, String unit, boolean reportOneTime) {
        this.name = name;
        this.value = value;
        this.tags = tags;
        this.unit = unit;
        this.reportOneTime = reportOneTime;
        this.time=Instant.now();
    }

    public Metric(String name, Number value, String unit, boolean reportOneTime) {
        this( name,  value,new HashSet<String>() ,  unit,  reportOneTime);
    }

    public Metric(String name, Number value, String unit) {
        this(name,value,unit,false);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setReportOneTime(boolean reportOneTime) {
        this.reportOneTime = reportOneTime;
    }

    public String getName() {

        return name;
    }

    public Number getValue() {
        return value;
    }

    public Instant getTime() {
        return time;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getUnit() {
        return unit;
    }

    public boolean isReportOneTime() {
        return reportOneTime;
    }
}
