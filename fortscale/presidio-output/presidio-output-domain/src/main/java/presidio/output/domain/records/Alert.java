package presidio.output.domain.records;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static presidio.output.domain.records.AlertEnums.*;

@Document(indexName = Alert.INDEX_NAME, type = Alert.ALERT_TYPE)
public class Alert {

    public static final String INDEX_NAME = "presidio-output";
    public static final String ALERT_TYPE = "alert";

    // field names
    public static final String ID = "id";
    public static final String USER_NAME = "userName";
    public static final String TYPE = "alertType";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String SCORE = "score";
    public static final String INDICATORS_NUM = "indicatorsNum";
    public static final String TIMRFRAME = "timeframe";
    public static final String SEVERITY = "severity";

    @Id
    @Field(type = FieldType.String, store = true)
    private String id;

    @Field(type = FieldType.String, store = true)
    private String userName;

    @Field(type = FieldType.String, store = true)
    private AlertType alertType;

    @Field (type = FieldType.String, store = true)
    private String startDate;

    @Field (type = FieldType.String, store = true)
    private String endDate;

    @Field(type = FieldType.Double, store = true)
    private double score;

    @Field(type = FieldType.Integer, store = true)
    private int indicatorsNum;

    @Field(type = FieldType.String, store = true)
    @Enumerated(EnumType.STRING)
    private AlertTimeframe timeframe;

    @Field (type = FieldType.String, store = true)
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    public Alert(){
        // empty const for JSON deserialization
    }

    public Alert(String id, String userName, AlertType type, String startDate, String endDate, double score, int indicatorsNum, AlertTimeframe timeframe, AlertSeverity severity) {
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

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
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

    public AlertTimeframe getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(AlertTimeframe timeframe) {
        this.timeframe = timeframe;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }
}
