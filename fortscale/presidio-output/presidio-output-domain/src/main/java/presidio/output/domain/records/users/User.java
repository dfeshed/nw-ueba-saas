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

/**
 * Created by efratn on 20/08/2017.
 */
@Document(indexName = AbstractElasticDocument.INDEX_NAME, type = User.USER_DOC_TYPE)
public class User extends AbstractElasticDocument {

    public static final String USER_DOC_TYPE = "user";

    public static final String ALERT_CLASSIFICATOINS_FIELD_NAME = "alertClassifications";
    public static final String INDICATORS_FIELD_NAME = "indicators";
    public static final String USER_SEVERITY_FIELD_NAME = "userSeverity";
    public static final String SCORE_FIELD_NAME = "userScore";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String IS_ADMIN_FIELD_NAME = "isAdmin";


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
    private double userScore;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(ALERT_CLASSIFICATOINS_FIELD_NAME)
    private List<String> alertClassifications;

    @Field(type = FieldType.String, store = true)
    @JsonProperty(INDICATORS_FIELD_NAME)
    private List<String> indicators;

    @Field(type = FieldType.String, store = true)
    @Enumerated(EnumType.STRING)
    @JsonProperty(USER_SEVERITY_FIELD_NAME)
    private UserSeverity userSeverity;

    public UserSeverity getUserSeverity() {
        return userSeverity;
    }

    public void setUserSeverity(UserSeverity userSeverity) {
        this.userSeverity = userSeverity;
    }

    public User(){
        // empty const for JSON deserialization
        this.indicators = new ArrayList<String>();
        this.alertClassifications = new ArrayList<String>();
    }

    public User(String userId, String userName, String userDisplayName, double userScore, List<String> alertClassifications, List<String> indicators, Boolean isAdmin) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.userScore = userScore;
        this.alertClassifications = alertClassifications;
        this.indicators = indicators;
        this.isAdmin = isAdmin;
    }

    public User(String userId, String userName, String userDisplayName, Boolean isAdmin) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.isAdmin = isAdmin;
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

    public void setUserScore(double userScore) {
        this.userScore = userScore;
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

    public double getUserScore() {
        return userScore;
    }

    @Field(type = FieldType.Boolean, store = true)
    @JsonProperty(IS_ADMIN_FIELD_NAME)
    private Boolean isAdmin;


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
        Set<String> newAlertClassifications = new HashSet<String>(this.alertClassifications);
        newAlertClassifications.addAll(alertClassifications);
        this.alertClassifications=new ArrayList<>();
        this.alertClassifications.addAll(newAlertClassifications);
    }

    public void setIndicators(List<String> indicators) {
        this.indicators = indicators;
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

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
