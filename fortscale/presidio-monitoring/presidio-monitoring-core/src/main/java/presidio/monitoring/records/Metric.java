package presidio.monitoring.records;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Metric {

    private String name;
    private long value;
    private Date time;
    private Date logicTime;
    private Set<String> tags;
    private String unit;
    private boolean reportOneTime;

    public Metric(String name, long value, Date logicTime, Set<String> tags, String unit, boolean reportOneTime) {
        this.name = name;
        this.value = value;
        this.time = new Date();
        this.logicTime = logicTime;
        this.tags = tags;
        this.unit = unit;
        this.reportOneTime = reportOneTime;
    }

    public Metric(String name, long value, Set<String> tags, String unit, boolean reportOneTime) {
        this(name, value, null, tags, unit, reportOneTime);
    }

    public Metric(String name, long value, String unit, boolean reportOneTime) {
        this(name, value, new HashSet<>(), unit, reportOneTime);
    }

    public Metric(String name, long value, String unit) {
        this(name, value, unit, false);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setTime(Date time) {
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

    public long getValue() {
        return value;
    }

    public Date getTime() {
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

    public Date getLogicTime() {
        return logicTime;
    }

    public void setLogicTime(Date logicTime) {
        this.logicTime = logicTime;
    }
}
