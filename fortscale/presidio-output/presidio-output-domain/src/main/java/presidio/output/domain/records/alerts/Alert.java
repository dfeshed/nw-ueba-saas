package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import presidio.output.domain.records.AbstractElasticDocument;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Document(indexName = AbstractElasticDocument.INDEX_NAME, type = Alert.ALERT_TYPE)
public class Alert extends AbstractElasticDocument {

    public static final String ALERT_TYPE = "alert";

    // field names
    public static final String CLASSIFICATIONS = "classifications";
    public static final String USER_NAME = "userName";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String SCORE = "score";
    public static final String INDICATORS_NUM = "indicatorsNum";
    public static final String TIMEFRAME = "timeframe";
    public static final String SEVERITY = "severity";
    public static final String USER_ID = "userId";
    public static final String USER_TAGS_FIELD_NAME = "userTags";

    @Field(type = FieldType.String, store = true, index = FieldIndex.not_analyzed)
    @JsonProperty(CLASSIFICATIONS)
    private List<String> classifications;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(USER_NAME)
    private String userName;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(USER_ID)
    private String userId;

    @Field(type = FieldType.Long, store = true)
    @JsonProperty(START_DATE)
    private long startDate;

    @Field(type = FieldType.Long, store = true)
    @JsonProperty(END_DATE)
    private long endDate;

    @Field(type = FieldType.Double, store = true)
    @JsonProperty(SCORE)
    private double score;

    @Field(type = FieldType.Integer, store = true)
    @JsonProperty(INDICATORS_NUM)
    private int indicatorsNum;

    @Field(type = FieldType.String, store = true)
    @Enumerated(EnumType.STRING)
    @JsonProperty(TIMEFRAME)
    private AlertEnums.AlertTimeframe timeframe;

    @Field(type = FieldType.String, store = true, index = FieldIndex.not_analyzed)
    @Enumerated(EnumType.STRING)
    @JsonProperty(SEVERITY)
    private AlertEnums.AlertSeverity severity;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(USER_TAGS_FIELD_NAME)
    private List<String> userTags;

    public Alert() {
        // empty const for JSON deserialization
    }

    public Alert(String userId, List<String> classifications, String userName, long startDate, long endDate, double score, int indicatorsNum, AlertEnums.AlertTimeframe timeframe, AlertEnums.AlertSeverity severity, List<String> userTags) {
        super();
        this.classifications = classifications;
        this.userId = userId;
        this.userName = userName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.score = score;
        this.indicatorsNum = indicatorsNum;
        this.timeframe = timeframe;
        this.severity = severity;
        this.userTags = userTags;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<String> classifications) {
        this.classifications = classifications;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public List<String> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<String> userTags) {
        this.userTags = userTags;
    }
}
