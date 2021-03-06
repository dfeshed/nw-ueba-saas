package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.general.Schema;
import fortscale.utils.elasticsearch.annotations.JoinColumn;
import fortscale.utils.elasticsearch.annotations.OneToMany;
import fortscale.utils.elasticsearch.annotations.OneToOne;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import presidio.output.domain.records.AbstractElasticDocument;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(indexName = AbstractElasticDocument.INDEX_NAME + "-" + Indicator.INDICATOR_TYPE, type = Indicator.INDICATOR_TYPE)
@Mapping(mappingPath = "elasticsearch/indexes/presidio-output-indicator/mappings.json")
public class Indicator extends AbstractElasticDocument {

    public static final String INDICATOR_TYPE = "indicator";

    // field names
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String VALUE = "anomalyValue";
    public static final String ALERT_ID = "alertId";
    public static final String HISTORICAL_DATA = "historicalData";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String SCHEMA = "schema";
    public static final String SCORE = "score";
    public static final String EVENTS_NUM = "eventsNum";
    public static final String SCORE_CONTRIBUTION = "scoreContribution";
    public static final String CONTEXTS = "contexts";
    public static final String ENTITY_TYPE = "entityType";

    @JsonProperty(NAME)
    private String name;

    @JsonProperty(VALUE)
    private String anomalyValue;

    @JsonProperty(ALERT_ID)
    private String alertId;

    @JsonProperty(HISTORICAL_DATA)
    private HistoricalData historicalData;

    @JsonProperty(START_DATE)
    private Date startDate;

    @JsonProperty(END_DATE)
    private Date endDate;

    @JsonProperty(SCHEMA)
    private Schema schema;

    @JsonProperty(SCORE)
    private double score;

    @JsonProperty(SCORE_CONTRIBUTION)
    private double scoreContribution;

    @JsonProperty(TYPE)
    @Enumerated(EnumType.STRING)
    private AlertEnums.IndicatorTypes type;

    @JsonProperty(EVENTS_NUM)
    private int eventsNum;

    @JsonProperty(CONTEXTS)
    private Map<String,String> contexts;

    @JsonProperty(ENTITY_TYPE)
    private String entityType;

    @JsonIgnore
    @ToStringExclude
    @OneToMany
    @JoinColumn(name = "id", referencedColumnName = "indicatorId")
    private transient List<IndicatorEvent> events;

    @JsonIgnore
    @ToStringExclude
    @OneToOne
    @JoinColumn(name = "alertId", referencedColumnName = "id")
    private transient Alert alert;

    public Indicator() {
        super();
        events = new ArrayList<IndicatorEvent>();
        // empty const for JSON deserialization
    }

    public Indicator(String alertId, String entityType) {
        super();
        events = new ArrayList<IndicatorEvent>();
        this.alertId = alertId;
        this.entityType = entityType;
    }

    public void setScoreContribution(double scoreContribution) {
        this.scoreContribution = scoreContribution;
    }

    public double getScoreContribution() {
        return scoreContribution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnomalyValue() {
        return anomalyValue;
    }

    public void setAnomalyValue(String anomalyValue) {
        this.anomalyValue = anomalyValue;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public HistoricalData getHistoricalData() {
        return historicalData;
    }

    public List<IndicatorEvent> getEvents() {
        return events;
    }

    public void setHistoricalData(HistoricalData historicalData) {
        this.historicalData = historicalData;
    }

    public void setEvents(List<IndicatorEvent> events) {
        this.events = events;
    }

    public int getEventsNum() {
        return eventsNum;
    }

    public void setEventsNum(int eventsNum) {
        this.eventsNum = eventsNum;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public AlertEnums.IndicatorTypes getType() {
        return type;
    }

    public void setType(AlertEnums.IndicatorTypes type) {
        this.type = type;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Map<String, String> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, String> contexts) {
        this.contexts = contexts;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
