package presidio.output.domain.records.alerts;

import fortscale.common.general.Schema;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import presidio.output.domain.records.AbstractElasticDocument;

import java.util.Date;
import java.util.Map;

@Document(indexName = AbstractElasticDocument.INDEX_NAME + "-" + IndicatorEvent.EVENT_TYPE, type = IndicatorEvent.EVENT_TYPE)
public class IndicatorEvent extends AbstractElasticDocument {

    public static final String EVENT_TYPE = "event";

    @Field(type = FieldType.Keyword, store = true)
    String indicatorId;

    @Field(type = FieldType.Text, store = true)
    Schema schema;

    @Field(type = FieldType.Date, store = true)
    Date eventTime;

    @Field(type = FieldType.Object, store = true)
    Map<String, Object> features;

    @Field(type = FieldType.Object, store = true)
    Map<String, Double> scores;

    @Field(type = FieldType.Keyword, store = true)
    String entityType;

    public IndicatorEvent() {
        super();
    }

    public String getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(String indicatorId) {
        this.indicatorId = indicatorId;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    public Map<String, Double> getScores() {
        return scores;
    }

    public void setScores(Map<String, Double> scores) {
        this.scores = scores;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
