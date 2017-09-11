package presidio.output.domain.records.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import presidio.output.domain.records.AbstractElasticDocument;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(indexName = AbstractElasticDocument.INDEX_NAME, type = User.USER_DOC_TYPE)
public class User extends AbstractElasticDocument {

    public static final String USER_DOC_TYPE = "user";

    public static final String ALERT_CLASSIFICATIONS = "alertClassifications";
    public static final String INDICATORS_FIELD_NAME = "indicators";
    public static final String USER_SEVERITY_FIELD_NAME = "userSeverity";
    public static final String SCORE_FIELD_NAME = "score";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String TAGS_FIELD_NAME = "tags";


    @Field(type = FieldType.String, store = true)
    @JsonProperty(USER_ID_FIELD_NAME)
    private String userId;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(USER_NAME_FIELD_NAME)
    private String userName;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(USER_DISPLAY_NAME_FIELD_NAME)
    private String userDisplayName;

    @Field(type = FieldType.Double, store = true)
    @JsonProperty(SCORE_FIELD_NAME)
    private double score;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(ALERT_CLASSIFICATIONS)
    private List<String> alertClassifications;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(INDICATORS_FIELD_NAME)
    private List<String> indicators;

    @Field(type = FieldType.String, store = true)
    @Enumerated(EnumType.STRING)
    @JsonProperty(USER_SEVERITY_FIELD_NAME)
    private UserSeverity userSeverity;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(TAGS_FIELD_NAME)
    private List<String> tags;

    public UserSeverity getUserSeverity() {
        return userSeverity;
    }

    public void setUserSeverity(UserSeverity userSeverity) {
        this.userSeverity = userSeverity;
    }

    public User() {
        // empty const for JSON deserialization
        this.indicators = new ArrayList<>();
        this.alertClassifications = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public User(String userId, String userName, String userDisplayName, double score, List<String> alertClassifications, List<String> indicators, List<String> tags, UserSeverity userSeverity) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.score = score;
        this.alertClassifications = alertClassifications;
        this.indicators = indicators;
        this.tags = tags;
        this.userSeverity = userSeverity;
    }

    public User(String userId, String userName, String userDisplayName, List<String> tags) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.tags = tags;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public void setAlertClassifications(List<String> alertClassifications) {
        this.alertClassifications = alertClassifications;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
