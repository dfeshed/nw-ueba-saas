package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.general.Schema;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import presidio.output.domain.records.AbstractElasticDocument;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(indexName = AbstractElasticDocument.INDEX_NAME + "-" + Indicator.INDICATOR_TYPE, type = Indicator.INDICATOR_TYPE)
@Mapping(mappingPath = "elasticsearch/mappings/indicators-mappings.json")
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

    @JsonProperty(NAME)
    private String name;

    @JsonProperty(VALUE)
    private String anomalyValue;

    @JsonProperty(ALERT_ID)
    private String alertId;

    @JsonProperty(HISTORICAL_DATA)
    private HistoricalData historicalData;

    @JsonProperty(START_DATE)
    private long startDate;

    @JsonProperty(END_DATE)
    private long endDate;

    @JsonProperty(SCHEMA)
    Schema schema;

    @JsonProperty(SCORE)
    private double score;

    @JsonProperty(TYPE)
    @Enumerated(EnumType.STRING)
    private AlertEnums.IndicatorTypes type;

    @JsonProperty(EVENTS_NUM)
    private int eventsNum;

    @JsonIgnore
    private transient List<IndicatorEvent> events;

    public Indicator() {
        super();
        events = new ArrayList<IndicatorEvent>();
        // empty const for JSON deserialization
    }

    public Indicator(String alertId) {
        super();
        events = new ArrayList<IndicatorEvent>();
        this.alertId = alertId;
    }

    public Indicator(String alertId, String id, Date createdDate) {
        super(id, createdDate);
        events = new ArrayList<IndicatorEvent>();
        this.alertId = alertId;
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

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
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
}
