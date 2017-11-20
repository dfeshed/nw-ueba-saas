package presidio.monitoring.records;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import presidio.monitoring.enums.MetricEnums;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static presidio.monitoring.records.MetricDocument.METRIC_INDEX_NAME;
import static presidio.monitoring.records.MetricDocument.TYPE;


@Document(indexName = METRIC_INDEX_NAME, type = TYPE)
public class MetricDocument {


    public static final String METRIC_INDEX_NAME = "presidio-monitoring";
    public static final String TYPE = "metric";

    @Id
    @Field(type = FieldType.String, store = true)
    private String id;

    @Field(type = FieldType.String, store = true)
    private String name;

    @Field(type = FieldType.Object, store = true, index = FieldIndex.analyzed)
    private Map<MetricEnums.MetricValues, Number> value;

    @Field(type = FieldType.Date, store = true)
    private Date timestamp;

    @Field(type = FieldType.Object, store = true, index = FieldIndex.analyzed)
    Map<MetricEnums.MetricTagKeysEnum, String> tags;

    @Field(type = FieldType.Date, store = true)
    private Date logicTime;

    public MetricDocument(String name, Map<MetricEnums.MetricValues, Number> value, Date timestamp, Map<MetricEnums.MetricTagKeysEnum, String> tags, Date logicTime) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.tags = tags;
        this.logicTime = logicTime;
    }

    public MetricDocument() {
    }


    public Date getLogicTime() {
        return logicTime;
    }

    public void setLogicTime(Date logicTime) {
        this.logicTime = logicTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Map<MetricEnums.MetricValues, Number> value) {
        this.value = value;
    }

    public void addValue(MetricEnums.MetricValues name, Number value) {
        this.value.put(name, value);
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setTags(Map<MetricEnums.MetricTagKeysEnum, String> tags) {
        this.tags.putAll(tags);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Map<MetricEnums.MetricValues, Number> getValue() {
        return value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Map<MetricEnums.MetricTagKeysEnum, String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "PresidioMetric{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value.toString() +
                ", timestamp=" + timestamp +
                ", tags=" + tags +
                '\'' +
                '}';
    }

    public boolean equals(MetricDocument metricDocument) {
        return this.value == metricDocument.getValue();
    }

}
