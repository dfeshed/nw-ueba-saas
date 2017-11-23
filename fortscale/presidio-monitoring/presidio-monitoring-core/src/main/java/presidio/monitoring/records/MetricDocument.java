package presidio.monitoring.records;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static presidio.monitoring.records.MetricDocument.METRIC_INDEX_NAME;
import static presidio.monitoring.records.MetricDocument.TYPE;

//@Document(indexName = METRIC_INDEX_NAME + "T(java.time.Instant).now().truncatedTo(T(java.time.temporal.ChronoUnit).DAYS)", type = TYPE)
@Document(indexName = METRIC_INDEX_NAME, type = TYPE)
public class MetricDocument {


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

    public MetricDocument(String name, long value, Set<String> tags, String unit) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.value = value;
        this.timestamp = new Date();
        this.tags = tags;
        this.unit = unit;
    }

    public MetricDocument(String name, long value, Date timestamp, Set<String> tags, String unit) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.tags = tags;
        this.unit = unit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MetricDocument() {
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

    public boolean equals(MetricDocument metricDocument) {
        return this.value == metricDocument.getValue();
    }

}
