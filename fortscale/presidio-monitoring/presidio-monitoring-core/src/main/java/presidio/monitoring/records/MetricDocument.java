package presidio.monitoring.records;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static presidio.monitoring.records.MetricDocument.METRIC_INDEX_NAME;
import static presidio.monitoring.records.MetricDocument.TYPE;

@Document(indexName = METRIC_INDEX_NAME, type = TYPE)
@Mapping(mappingPath = "elasticsearch/mappings/presidio-monitoring.json")
public final class MetricDocument {


    public static final String METRIC_INDEX_NAME = "<presidio-monitoring-{now/d}>";
    public static final String TYPE = "metric";

    @Id
    private String id;

    private String name;

    private Map<MetricEnums.MetricValues, Number> value;

    private Date timestamp;

    private Map<MetricEnums.MetricTagKeysEnum, String> tags;

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
        this.tags = tags;
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
