package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import presidio.output.domain.records.AbstractElasticDocument;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

@Document(indexName = AbstractElasticDocument.INDEX_NAME + "-" + Alert.ALERT_TYPE, type = Alert.ALERT_TYPE)
public class Alert extends AbstractElasticDocument {

    public static final String ALERT_TYPE = "alert";

    // field names
    public static final String CLASSIFICATIONS = "classifications";
    public static final String USER_NAME = "userName";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String SCORE = "score";
    public static final String INDICATORS_NUM = "indicatorsNum";
    public static final String INDICATOR_NAMES = "indicatorsNames";
    public static final String TIMEFRAME = "timeframe";
    public static final String SEVERITY = "severity";
    public static final String USER_ID = "userId";
    public static final String SMART_ID = "smartId";
    public static final String USER_TAGS_FIELD_NAME = "userTags";
    public static final String CONTRIBUTION_TO_USER_SCORE_FIELD_NAME = "contributionToUserScore";
    public static final String AGGR_SEVERITY_PER_DAY = "severityPerDay";

    @Field(type = FieldType.String, store = true, index = FieldIndex.not_analyzed)
    @JsonProperty(CLASSIFICATIONS)
    private List<String> classifications;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(USER_NAME)
    private String userName;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(SMART_ID)
    private String smartId;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(USER_ID)
    private String userId;

    @Field(type = FieldType.Date, store = true)
    @JsonProperty(START_DATE)
    private Date startDate;

    @Field(type = FieldType.Date, store = true)
    @JsonProperty(END_DATE)
    private Date endDate;

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

    @Field(type = FieldType.String, store = true, index = FieldIndex.not_analyzed)
    @JsonProperty(INDICATOR_NAMES)
    private List<String> indicatorsNames;

    @JsonIgnore
    private transient List<Indicator> indicators;

    @JsonProperty(USER_TAGS_FIELD_NAME)
    private List<String> userTags;

    @JsonProperty(CONTRIBUTION_TO_USER_SCORE_FIELD_NAME)
    private Double contributionToUserScore;

    public Alert() {
        // empty const for JSON deserialization
    }

    public Alert(String userId, String smartId, List<String> classifications, String userName, Date startDate, Date endDate, double score, int indicatorsNum, AlertEnums.AlertTimeframe timeframe, AlertEnums.AlertSeverity severity, List<String> userTags, Double contributionToUserScore) {
        super();
        this.classifications = classifications;
        this.userId = userId;
        this.smartId = smartId;
        this.userName = userName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.score = score;
        this.indicatorsNum = indicatorsNum;
        this.timeframe = timeframe;
        this.severity = severity;
        this.userTags = userTags;
        this.contributionToUserScore = contributionToUserScore;
    }

    public Alert(String userId, String smartId, List<String> classifications, String userName, Date startDate, Date endDate, double score, int indicatorsNum, AlertEnums.AlertTimeframe timeframe, AlertEnums.AlertSeverity severity, List<String> userTags, Double contributionToUserScore, String id, Date createdDate, Date updatedDate, String updatedBy) {
        super(id, createdDate, updatedDate, updatedBy);
        this.classifications = classifications;
        this.userId = userId;
        this.smartId = smartId;
        this.userName = userName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.score = score;
        this.indicatorsNum = indicatorsNum;
        this.timeframe = timeframe;
        this.severity = severity;
        this.userTags = userTags;
        this.contributionToUserScore = contributionToUserScore;
    }

    public String getSmartId() {
        return smartId;
    }

    public void setSmartId(String smartId) {
        this.smartId = smartId;
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

    public List<Indicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<Indicator> indicators) {
        this.indicators = indicators;
    }

    public List<String> getIndicatorsNames() {
        return indicatorsNames;
    }

    public void setIndicatorsNames(List<String> indicatorsNames) {
        this.indicatorsNames = indicatorsNames;
    }

    public Double getContributionToUserScore() {
        return contributionToUserScore;
    }

    public void setContributionToUserScore(Double contributionToUserScore) {
        this.contributionToUserScore = contributionToUserScore;
    }
}
