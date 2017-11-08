package presidio.monitoring.records;


import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Set;

import static presidio.monitoring.records.MetricDocument.METRIC_INDEX_NAME;
import static presidio.monitoring.records.MetricDocument.TYPE;

@Document(indexName = METRIC_INDEX_NAME, type = TYPE)
public class MetricWithLogicTimeDocument extends MetricDocument {

    @Field(type = FieldType.Date, store = true)
    private Date logicTime;


    public MetricWithLogicTimeDocument(String name, long value, Set<String> tags, String unit, Date logicTime) {
        super(name, value, tags, unit);
        this.logicTime = logicTime;
    }

    public MetricWithLogicTimeDocument(String name, long value, Date timestamp, Set<String> tags, String unit, Date logicTime) {
        super(name, value, timestamp, tags, unit);
        this.logicTime = logicTime;
    }

    public Date getLogicTime() {
        return logicTime;
    }

    public void setLogicTime(Date logicTime) {
        this.logicTime = logicTime;
    }

    @Override
    public String toString() {
        return "PresidioMetric{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", value=" + getValue() +
                ", timestamp=" + getTimestamp() +
                ", logicTime=" + logicTime +
                ", tags=" + getTags() +
                ", unit='" + getUnit() + '\'' +
                '}';
    }
}
