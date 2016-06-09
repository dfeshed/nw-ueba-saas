package fortscale.domain.core;

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
import static fortscale.domain.core.DeletedUser.administratorAccountField;
import static fortscale.domain.core.DeletedUser.executiveAccountField;
import static fortscale.domain.core.DeletedUser.userServiceAccountField;


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
		@CompoundIndex(name="totalScoreCurScore", def = "{'scores.total.score': -1}"),
		@CompoundIndex(name="totalScoreCurTrend", def = "{'scores.total.trendScore': -1}"),
		@CompoundIndex(name="authScoreCurScore", def = "{'scores.auth.score': -1}"),
		@CompoundIndex(name="authScoreCurTrend", def = "{'scores.auth.trendScore': -1}"),
		@CompoundIndex(name="sshScoreCurScore", def = "{'scores.ssh.score': -1}"),
		@CompoundIndex(name="sshScoreCurTrend", def = "{'scores.ssh.trendScore': -1}"),
		@CompoundIndex(name="vpnScoreCurScore", def = "{'scores.vpn.score': -1}"),
		@CompoundIndex(name="vpnScoreCurTrend", def = "{'scores.vpn.trendScore': -1}"),
})
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
	public static final String classifierScoreField = "scores";
	public static final String followedField = "followed";
	public static final String adInfoField = "adInfo";
	public static final String whenCreatedField = "whenCreated";
	public static final String tagsField = "tags";
    public static final String socreField = "score";
    public static final String scoreSeverityField = "scoreSeverity";

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
	
	@Field(appField)
	@JsonProperty
	Map<String, ApplicationUserDetails> appUserDetailsMap = new HashMap<>();
	
	@Field(logUsernameField)
	@JsonProperty
	Map<String, String> logUsernameMap = new HashMap<>();
	
	@Field(classifierScoreField)
	private HashMap<String, ClassifierScore> scores = new HashMap<String, ClassifierScore>();
	
	@Field(searchFieldName)
	@Indexed
	private String searchField;
	
	@Field(adInfoField)
	private UserAdInfo adInfo;
	
	@Field(whenCreatedField)
	private Date whenCreated;

	private String adDn;
	
	@Field(logLastActivityField)
	Map<String, DateTime> logLastActivityMap = new HashMap<>();
	
	@Field(lastActivityField)
	DateTime lastActivity;
	
	@Field(tagsField)
	private Set<String> tags = new HashSet<String>();

    @Field(scoreSeverityField)
    private Severity scoreSeverity = Severity.Medium; //Todo: remove '= Severity.Medium' when we add real calculation.

    @Field(socreField)
    private int score = 50; //Todo: remove '= 50' when we add real calculation.

	
	public String getAdDn() {
		return adDn;
	}
	
	public void setAdDn(String adDn) {
		this.adDn = adDn;
	}
	
	private String adObjectGUID;
	
	public String getAdObjectGUID() {
		return adObjectGUID;
	}

	public void setAdObjectGUID(String adObjectGUID) {
		this.adObjectGUID = adObjectGUID;
	}
		
		
	public UserAdInfo getAdInfo() {
		if(adInfo == null){
			adInfo = new UserAdInfo();
		}
		return adInfo;
	}

	public void setAdInfo(UserAdInfo adInfo) {
		this.adInfo = adInfo;
		// populate display name field with updated user ad info
		populateDisplayName();
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		// populate display name field with updated user ad info
		populateDisplayName();
	}
	
	private void populateDisplayName() {
		if(getAdInfo().getFirstname() != null && getAdInfo().getLastname() != null){
			this.displayName = getAdInfo().getFirstname() + " " + getAdInfo().getLastname();
		} else if(getAdInfo().getDisplayName() != null){
			this.displayName = getAdInfo().getDisplayName();
		} else{
			this.displayName = getUsername();
		}
	}
	
	public String getDisplayName() {
		// ensure display name was set
		if (StringUtils.isEmpty(displayName))
			populateDisplayName();
		return displayName;
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
	
	public Boolean getFollowed() {
		return followed;
	}


	public void setFollowed(Boolean followed) {
		this.followed = followed;
	}
	
	public boolean containsApplicationUserDetails(ApplicationUserDetails applicationUserDetails) {
		return appUserDetailsMap.containsKey(applicationUserDetails.getApplicationName());
	}
	
	public ApplicationUserDetails getApplicationUserDetails(String applicationName) {
		return appUserDetailsMap.get(applicationName);
	}
	
	public void addApplicationUserDetails(ApplicationUserDetails applicationUserDetails) {
		Assert.notNull(applicationUserDetails);
		appUserDetailsMap.put(applicationUserDetails.getApplicationName(), applicationUserDetails);
	}
	
	public Map<String, ApplicationUserDetails> getApplicationUserDetails(){
		return appUserDetailsMap;
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
	
	

	public HashMap<String, ClassifierScore> getScores() {
		return scores;
	}
	
	public ClassifierScore getScore(String classifierId) {
		return scores.get(classifierId);
	}

	public void putClassifierScore(ClassifierScore score) {
		this.scores.put(score.getClassifierId(), score);
	}	
	
	public void removeClassifierScore(String classifierId) {
		this.scores.remove(classifierId);
	}	
	
	public void removeAllScores(){
		this.scores.clear();
	}
	
	
	public void addTag(String tag) {
		checkNotNull(tag);
		tags.add(tag);
	}
	
	public boolean hasTag(String tag) {
		checkNotNull(tag);
		return tags.contains(tag);
	}

	public void removeTag(String tag) {
		checkNotNull(tag);
		tags.remove(tag);
	}

	public Set<String> getTags(){
		return tags;
	}

	public void setTags(Set<String> tags) { this.tags = tags; }
	
	public Date getWhenCreated() {
		return whenCreated;
	}

	public void setWhenCreated(Date whenCreated) {
		this.whenCreated = whenCreated;
	}
	
	public Boolean getUserServiceAccount() {
		return tags != null ? tags.contains(UserTagEnum.service.getId()) : false;
	}

	public Boolean getAdministratorAccount() {
		return tags != null ? tags.contains(UserTagEnum.admin.getId()) : false;
	}

	public Boolean getExecutiveAccount() {
		return tags != null ? tags.contains(UserTagEnum.executive.getId()) : false;
	}

	public static String getClassifierScoreField(String classifierId) {
		return String.format("%s.%s", User.classifierScoreField, classifierId);
	}
	
	public static String getClassifierScoreCurrentTimestampField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.timestampField);
	}
	
	public static String getClassifierScoreCurrentTimestampEpochField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.timestampEpocField);
	}
	
	public static String getClassifierScoreCurrentScoreField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.scoreField);
	}
	
	public static String getClassifierScoreCurrentAvgScoreField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.avgScoreField);
	}
	
	public static String getClassifierScoreCurrentTrendField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.trendField);
	}
	
	public static String getClassifierScoreCurrentTrendScoreField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.trendScoreField);
	}
	
	public static String getAppUserNameField(String applicationName) {
		return String.format("%s.%s.%s", User.appField,applicationName,ApplicationUserDetails.userNameField);
	}
	
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

    public static String getSocreField() {
        return socreField;
    }

    public static String getScoreSeverityField() {
        return scoreSeverityField;
    }

    public void setScoreSeverity(Severity scoreSeverity) {
        this.scoreSeverity = scoreSeverity;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public Severity getScoreSeverity() {
        return scoreSeverity;
    }
}
