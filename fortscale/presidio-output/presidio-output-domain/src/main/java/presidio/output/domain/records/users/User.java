package presidio.output.domain.records.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.general.ThreadLocalWithBatchInformation;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import presidio.output.domain.records.AbstractElasticDocument;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(indexName = AbstractElasticDocument.INDEX_NAME + "-" + User.USER_DOC_TYPE, type = User.USER_DOC_TYPE)
@Mapping(mappingPath = "elasticsearch/indexes/presidio-output-user/mappings.json")
@Setting(settingPath = "elasticsearch/indexes/presidio-output-user/settings.json")
public class User extends AbstractElasticDocument {

    public static final String USER_DOC_TYPE = "user";

    public static final String ALERT_CLASSIFICATIONS_FIELD_NAME = "alertClassifications";
    public static final String INDICATORS_FIELD_NAME = "indicators";
    public static final String SEVERITY_FIELD_NAME = "severity";
    public static final String SCORE_FIELD_NAME = "score";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String INDEXED_USER_NAME_FIELD_NAME = "indexedUserName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String TAGS_FIELD_NAME = "tags";
    public static final String ALERTS_COUNT_FIELD_NAME = "alertsCount";
    public static final String UPDATED_BY_LOGICAL_START_DATE_FIELD_NAME = "updatedByLogicalStartDate";
    public static final String UPDATED_BY_LOGICAL_END_DATE_FIELD_NAME = "updatedByLogicalEndDate";
    public static final String USER_DISPLAY_NAME_SORT_LOWERCASE_FIELD_NAME = "userDisplayNameSortLowercase";


    @JsonProperty(USER_ID_FIELD_NAME)
    private String userId;

    @JsonProperty(USER_NAME_FIELD_NAME)
    private String userName;

    @JsonProperty(INDEXED_USER_NAME_FIELD_NAME)
    private String indexedUserName;

    @JsonProperty(USER_DISPLAY_NAME_FIELD_NAME)
    private String userDisplayName;

    @JsonProperty(USER_DISPLAY_NAME_SORT_LOWERCASE_FIELD_NAME)
    private String userDisplayNameSortLowercase;

    @JsonProperty(SCORE_FIELD_NAME)
    private double score;

    @JsonProperty(ALERT_CLASSIFICATIONS_FIELD_NAME)
    private List<String> alertClassifications = new ArrayList<>();

    @JsonProperty(INDICATORS_FIELD_NAME)
    private List<String> indicators = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @JsonProperty(SEVERITY_FIELD_NAME)
    private UserSeverity severity;

    @JsonProperty(TAGS_FIELD_NAME)
    private List<String> tags = new ArrayList<>();

    @JsonProperty(ALERTS_COUNT_FIELD_NAME)
    private int alertsCount;

    @JsonProperty(UPDATED_BY_LOGICAL_START_DATE_FIELD_NAME)
    private Date updatedByLogicalStartDate;

    @JsonProperty(UPDATED_BY_LOGICAL_END_DATE_FIELD_NAME)
    private Date updatedByLogicalEndDate;


    public User() {
        // empty const for JSON deserialization
    }

    public User(String userId, String userName, String userDisplayName, double score, List<String> alertClassifications, List<String> indicators, List<String> tags, UserSeverity severity,
                int alertsCount) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.indexedUserName = userName;
        this.userDisplayName = userDisplayName;
        this.userDisplayNameSortLowercase = userDisplayName;
        this.score = score;
        this.alertClassifications = alertClassifications;
        this.indicators = indicators;
        this.tags = tags;
        this.severity = severity;
        this.alertsCount = alertsCount;
    }

    public User(String userId, String userName, String userDisplayName, List<String> tags) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.indexedUserName = userName;
        this.userDisplayName = userDisplayName;
        this.userDisplayNameSortLowercase = userDisplayName;
        this.severity = UserSeverity.LOW;
        this.tags = tags;
    }

    public UserSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(UserSeverity severity) {
        this.severity = severity;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIndexedUserName() {
        return indexedUserName;
    }

    public void setIndexedUserName(String indexedUserName) {
        this.indexedUserName = indexedUserName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public double getScore() {
        return score;
    }

    public List<String> getAlertClassifications() {
        return alertClassifications;
    }

    public List<String> getIndicators() {
        return indicators;
    }

    public int getAlertsCount() {
        return alertsCount;
    }

    public void setAlertsCount(int alertsCount) {
        this.alertsCount = alertsCount;
    }

    public void incrementAlertsCountByOne() {
        this.alertsCount++;
    }

    public void incrementAlertsCountByNumber(int number) {
        this.alertsCount = alertsCount + number;
    }

    public void incrementUserScoreByNumber(double number) {
        this.score += number;
    }

    public void setAlertClassifications(List<String> alertClassifications) {
        this.alertClassifications = alertClassifications;
    }

    public Date getUpdatedByLogicalStartDate() {
        return updatedByLogicalStartDate;
    }

    public void setUpdatedByLogicalStartDate(Date updatedByLogicalStartDate) {
        this.updatedByLogicalStartDate = updatedByLogicalStartDate;
    }

    public Date getUpdatedByLogicalEndDate() {
        return updatedByLogicalEndDate;
    }

    public void setUpdatedByLogicalEndDate(Date updatedByLogicalEndDate) {
        this.updatedByLogicalEndDate = updatedByLogicalEndDate;
    }

    public void addAlertClassifications(List<String> alertClassifications) {
        Set<String> newAlertClassifications = new HashSet<>(this.alertClassifications);
        newAlertClassifications.addAll(alertClassifications);
        this.alertClassifications = new ArrayList<>();
        this.alertClassifications.addAll(newAlertClassifications);
    }

    public void setIndicators(List<String> indicators) {
        this.indicators = indicators;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getUserDisplayNameSortLowercase() {
        return userDisplayNameSortLowercase;
    }

    public void setUserDisplayNameSortLowercase(String userDisplayNameSortLowercase) {
        this.userDisplayNameSortLowercase = userDisplayNameSortLowercase;
    }

    @Override
    public void updateFieldsBeforeSave() {
        super.updateFieldsBeforeSave();
        if (ThreadLocalWithBatchInformation.getCurrentProcessedTime() != null) {
            setUpdatedByLogicalStartDate(ThreadLocalWithBatchInformation.getCurrentProcessedTime().getStartAsDate());
            setUpdatedByLogicalEndDate(ThreadLocalWithBatchInformation.getCurrentProcessedTime().getEndAsDate());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
