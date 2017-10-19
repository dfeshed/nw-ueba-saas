package presidio.monitoring.elastic.records;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Set;

import static presidio.monitoring.elastic.records.PresidioMetric.METRIC_INDEX_NAME;
import static presidio.monitoring.elastic.records.PresidioMetric.TYPE;


@Document(indexName = METRIC_INDEX_NAME, type = TYPE)
public class PresidioMetric {


    public static final String METRIC_INDEX_NAME = "presidio-monitoring";
    public static final String TYPE = "metric";

    @Id
    @Field(type = FieldType.String, store = true)
    private String id;

    @Field(type = FieldType.String, store = true)
    private String name;

    @Field(type = FieldType.Long, store = true)
    private long value;

    @Field(type = FieldType.Date, store = true)
    private Date timestamp;

    @Field(type = FieldType.String, store = true)
    private Set<String> tags;

    @Field(type = FieldType.String, store = true)
    private String unit;

    @Field(type = FieldType.Boolean, store = true)
    private boolean exportOnlyOnFlush;

    public PresidioMetric(String name, long value, Set<String> tags, String unit,boolean exportOnlyOnFlush) {
        this.id = System.nanoTime() + "";
        this.name = name;
        this.value = value;
        this.timestamp = new Date();
        this.tags = tags;
        this.unit = unit;
        this.exportOnlyOnFlush = exportOnlyOnFlush;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PresidioMetric() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setTags(Set<String> tags) {
        this.tags.addAll(tags);
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public long getValue() {
        return value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getUnit() {
        return unit;
    }

    public void setExportOnlyOnFlush(boolean exportOnlyOnFlush) {
        this.exportOnlyOnFlush = exportOnlyOnFlush;
    }

    public boolean getExportOnlyOnFlush() {
        return exportOnlyOnFlush;
    }

    @Override
    public String toString() {
        return "PresidioMetric{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                ", tags=" + tags +
                ", unit='" + unit + '\'' +
                '}';
    }

    public boolean equals(PresidioMetric presidioMetric) {
        return this.value == presidioMetric.getValue();
    }

}
