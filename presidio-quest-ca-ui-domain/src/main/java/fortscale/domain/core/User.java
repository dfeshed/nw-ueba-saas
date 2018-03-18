package fortscale.domain.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;



@Document(collection=User.collectionName)
@CompoundIndexes({
		@CompoundIndex(name="ad_objectGUID_1", def = "{'adInfo.objectGUID': 1}", unique=true, sparse=true),
		@CompoundIndex(name="ad_dn_1", def = "{'adInfo.dn': 1}"),
		@CompoundIndex(name="ad_emailAddress_1", def = "{'adInfo.emailAddress': 1}"),
		@CompoundIndex(name="appActive_directoryUserName_1", def = "{'app.active_directory.userName': 1}"),
		@CompoundIndex(name="appVpnUsername_1", def = "{'app.vpn.userName': 1}"),
		@CompoundIndex(name="logUsername_authenticationscores_1", def = "{'logUsername.authenticationscores': 1}"),
		@CompoundIndex(name="logUsername_vpndatares_1", def = "{'logUsername.vpndatares': 1}"),
		@CompoundIndex(name="logUsername_sshscores_1", def = "{'logUsername.sshscores': 1}"),
})
@ApiModel
public class User extends AbstractDocument {
	private static final long serialVersionUID = -2544779887545246880L;
	
	public static final String collectionName = "user";
	public static final String appField = "app";
	public static final String logUsernameField = "logUsername";
	public static final String logLastActivityField = "logLastActivity";
	public static final String lastActivityField = "lastActivity";
	public static final String usernameField = "username";
	public static final String noDomainUsernameField = "noDomainUsername";
	public static final String displayNameField = "displayName";
	public static final String searchFieldName = "sf";
	public static final String followedField = "followed";
	public static final String adInfoField = "adInfo";
	public static final String whenCreatedField = "whenCreated";
	public static final String userServiceAccountField = "userServiceAccount";
	public static final String administratorAccountField = "administratorAccount";
	public static final String executiveAccountField = "executiveAccount";
	public static final String tagsField = "tags";
    public static final String scoreField = "score";
    public static final String scoreSeverityField = "scoreSeverity";
	public static final String terminationDateField = "terminationDate";
	public static final String alertsCountField = "alertsCount";
	public static final String sourceMachineCountField = "sourceMachineCount";

	public static final String SERVICE = "service";
	public static final String ADMIN = "admin";
	public static final String EXECUTIVE = "executive";

	@Indexed
	@Field(administratorAccountField)
	private Boolean administratorAccount;
	
	@Field(executiveAccountField)
	private Boolean executiveAccount;
	
	@Indexed
	@Field(userServiceAccountField)
	private Boolean userServiceAccount;
	
	@Indexed
	@Field(displayNameField)
	private String displayName;
	
	@Indexed
	@Field(usernameField)
	private String username;
	
	@Indexed
	@Field(noDomainUsernameField)
	private String noDomainUsername;
	
	@Indexed
	@Field(followedField)
	private Boolean followed = false;

	private List<Alert> alerts;
	

	@Field(logUsernameField)
	@JsonProperty
	Map<String, String> logUsernameMap = new HashMap<>();

	@Field(searchFieldName)
	@Indexed
	private String searchField;
	
//	@Field(adInfoField)
//	private UserAdInfo adInfo;
	
	@Field(whenCreatedField)
	private Date whenCreated;

	private String adDn;
	
	@Field(logLastActivityField)
	Map<String, DateTime> logLastActivityMap = new HashMap<>();
	
	@Field(lastActivityField)
	@Indexed
	DateTime lastActivity;
	
	@Field(tagsField)
	private Set<String> tags = new HashSet<String>();

    @Field(scoreSeverityField)
    private Severity scoreSeverity;

    @Field(scoreField)
	@Indexed
    private double score=0;

	@Field(terminationDateField)
	private DateTime terminationDate;

	@Indexed
	@Field(alertsCountField)
	private int alertsCount;

	@Field(sourceMachineCountField)
	private int sourceMachineCount;

	public String getAdDn() {
		return adDn;
	}
	
	public void setAdDn(String adDn) {
		this.adDn = adDn;
	}
	
	private String adObjectGUID;

	public int getAlertsCount() {
		return alertsCount;
	}

	public void setAlertsCount(int alertsCount) {
		this.alertsCount = alertsCount;
	}

	public String getAdObjectGUID() {
		return adObjectGUID;
	}

	public void setAdObjectGUID(String adObjectGUID) {
		this.adObjectGUID = adObjectGUID;
	}


	@ApiModelProperty(position = 1, required = true, value = "The normalized user name")
	public String getUsername() {
		return username;
	}

	public void setMockId(String id){
		super.setId(id);
	}
	public void setUsername(String username) {
		this.username = username;
		this.displayName = getUsername();

	}
	
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getNoDomainUsername() {
		return noDomainUsername;
	}

	public void setNoDomainUsername(String noDomainUsername) {
		this.noDomainUsername = noDomainUsername;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	@ApiModelProperty(position = 1, required = false, value = "The normalized user name",dataType = "Boolean")
	public Boolean getFollowed() {
		return followed;
	}

	public void setFollowed(Boolean followed) {
		this.followed = followed;
	}
	

	public boolean containsLogUsername(String logname) {
		return logUsernameMap.containsKey(logname);
	}
	
	public void addLogUsername(String logname, String username) {
		Assert.hasText(logname);
		Assert.hasText(username);
		logUsernameMap.put(logname, username);
	}
	
	public String getLogUserName(String logname){
		return logUsernameMap.get(logname);
	}
	
	public Map<String, String> getLogUsernameMap(){
		return logUsernameMap;
	}
		
	public DateTime getLogLastActivity(String logEventName){
		return logLastActivityMap.get(logEventName);
	}
	
	public Map<String, DateTime> getLogLastActivityMap(){
		return logLastActivityMap;
	}

	public void addTag(String tag) {
		checkNotNull(tag);
		tags.add(tag);
	}
	


	@ApiModelProperty(position = 1, required = true, value = "Set of users tags",example = "tag1,tag2,tag3" )
	public Set<String> getTags(){
		return tags;
	}

	public void setTags(Set<String> tags) { this.tags = tags; }
	


	public static String getAppField(String applicationName) {
		return String.format("%s.%s", User.appField,applicationName);
	}
	
	public static String getLogUserNameField(String logname) {
		return String.format("%s.%s", User.logUsernameField,logname);
	}
	
	public static String getLogLastActivityField(String logEventsName) {
		return String.format("%s.%s", User.logLastActivityField, logEventsName);
	}
	
	public DateTime getLastActivity() {
		return lastActivity;
	}

	public static String getAdInfoField(String adInfoFieldName) {
		return String.format("%s.%s", User.adInfoField,adInfoFieldName);
	}

	public Severity getScoreSeverity() {
		return scoreSeverity;
	}

	public void setScoreSeverity(Severity scoreSeverity) {
        this.scoreSeverity = scoreSeverity;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

	public DateTime getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(DateTime terminationDate) {
		this.terminationDate = terminationDate;
	}

	public int getSourceMachineCount() {
		return sourceMachineCount;
	}

	public void setSourceMachineCount(int sourceMachineCount) {
		this.sourceMachineCount = sourceMachineCount;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}
}
