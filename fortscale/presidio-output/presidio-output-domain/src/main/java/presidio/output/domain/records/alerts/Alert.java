package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Document(indexName = Alert.INDEX_NAME, type = Alert.ALERT_TYPE)
public class Alert {

    public static final String INDEX_NAME = "presidio-output";
    public static final String ALERT_TYPE = "alert";

    // field names
    public static final String CLASSIFICATION = "classification";
    public static final String ID = "id";
    public static final String USER_NAME = "userName";
    public static final String TYPE = "alertType";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String SCORE = "score";
    public static final String INDICATORS_NUM = "indicatorsNum";
    public static final String TIMRFRAME = "timeframe";
    public static final String SEVERITY = "severity";

    @Field(type = FieldType.String, store = true)
    @JsonProperty(CLASSIFICATION)
    private List<String> classification;

    @Id
    @Field(type = FieldType.String, store = true)
    private String id;

    @Field(type = FieldType.String, store = true)
    private String userName;

    @Field(type = FieldType.String, store = true)
    private AlertEnums.AlertType alertType;

    @Field(type = FieldType.Long, store = true)
    private long startDate;

    @Field(type = FieldType.Long, store = true)
    private long endDate;

    @Field(type = FieldType.Double, store = true)
    private double score;

    @Field(type = FieldType.Integer, store = true)
    private int indicatorsNum;

    @Field(type = FieldType.String, store = true)
    @Enumerated(EnumType.STRING)
    private AlertEnums.AlertTimeframe timeframe;

    @Field(type = FieldType.String, store = true)
    @Enumerated(EnumType.STRING)
    private AlertEnums.AlertSeverity severity;

    public Alert() {
        // empty const for JSON deserialization
    }

    public Alert(List<String> classification, String id, String userName, AlertEnums.AlertType type, long startDate, long endDate, double score, int indicatorsNum, AlertEnums.AlertTimeframe timeframe, AlertEnums.AlertSeverity severity) {
        this.classification = classification;
        this.id = id;
        this.userName = userName;
        this.alertType = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.score = score;
        this.indicatorsNum = indicatorsNum;
        this.timeframe = timeframe;
        this.severity = severity;
    }

    public List<String> getClassification() {
        return classification;
    }

    public void setClassification(List<String> classification) {
        this.classification = classification;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public AlertEnums.AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertEnums.AlertType alertType) {
        this.alertType = alertType;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getIndicatorsNum() {
        return indicatorsNum;
    }

    public void setIndicatorsNum(int indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
    }

    public AlertEnums.AlertTimeframe getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(AlertEnums.AlertTimeframe timeframe) {
        this.timeframe = timeframe;
    }

    public AlertEnums.AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertEnums.AlertSeverity severity) {
        this.severity = severity;
    }
}
