
package fortscale.domain.core;

import fortscale.domain.events.LogEventsEnum;
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

/**
 * Created by Idanp
 * This class will represent users that was duplicated at the User collection
 * In case that from some reason user was recreated at the Active directory domain with different distinguish name and diffrent GUID and SID but with the same princaple name
 * For avoiding duplication in the User collection at mongo we will move the old record of that user from User collection into DuplicatedUser collection
 */
@Document(collection= DeletedUser.collectionName)
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
public class DeletedUser extends AbstractDocument {
	private static final long serialVersionUID = -2544779887545246880L;

	public static final String collectionName = "DeletedUsers";
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
	public static final String userServiceAccountField = "userServiceAccount";
	public static final String administratorAccountField = "administratorAccount";
	public static final String executiveAccountField = "executiveAccount";
	public static final String tagsField = "tags";

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

	public DateTime getLogLastActivity(LogEventsEnum eventId){
		return logLastActivityMap.get(eventId.getId());
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

	public Date getWhenCreated() {
		return whenCreated;
	}

	public void setWhenCreated(Date whenCreated) {
		this.whenCreated = whenCreated;
	}

	public Boolean getUserServiceAccount() {
		return userServiceAccount != null ? userServiceAccount : false;
	}

	public void setUserServiceAccount(Boolean userServiceAccount) {
		this.userServiceAccount = userServiceAccount;
	}

	public Boolean getAdministratorAccount() {
		return administratorAccount != null ? administratorAccount : false;
	}

	public void setAdministratorAccount(Boolean administratorAccount) {
		this.administratorAccount = administratorAccount;
	}

	public Boolean getExecutiveAccount() {
		return executiveAccount != null ? executiveAccount : false;
	}

	public void setExecutiveAccount(Boolean executiveAccount) {
		this.executiveAccount = executiveAccount;
	}

	public static String getClassifierScoreField(String classifierId) {
		return String.format("%s.%s", DeletedUser.classifierScoreField, classifierId);
	}

	public static String getClassifierScoreCurrentTimestampField(String classifierId) {
		return String.format("%s.%s.%s", DeletedUser.classifierScoreField, classifierId, ScoreInfo.timestampField);
	}

	public static String getClassifierScoreCurrentTimestampEpochField(String classifierId) {
		return String.format("%s.%s.%s", DeletedUser.classifierScoreField, classifierId, ScoreInfo.timestampEpocField);
	}

	public static String getClassifierScoreCurrentScoreField(String classifierId) {
		return String.format("%s.%s.%s", DeletedUser.classifierScoreField, classifierId, ScoreInfo.scoreField);
	}

	public static String getClassifierScoreCurrentAvgScoreField(String classifierId) {
		return String.format("%s.%s.%s", DeletedUser.classifierScoreField, classifierId, ScoreInfo.avgScoreField);
	}

	public static String getClassifierScoreCurrentTrendField(String classifierId) {
		return String.format("%s.%s.%s", DeletedUser.classifierScoreField, classifierId, ScoreInfo.trendField);
	}

	public static String getClassifierScoreCurrentTrendScoreField(String classifierId) {
		return String.format("%s.%s.%s", DeletedUser.classifierScoreField, classifierId, ScoreInfo.trendScoreField);
	}

	public static String getAppUserNameField(String applicationName) {
		return String.format("%s.%s.%s", DeletedUser.appField,applicationName,ApplicationUserDetails.userNameField);
	}

	public static String getAppField(String applicationName) {
		return String.format("%s.%s", DeletedUser.appField,applicationName);
	}

	public static String getLogUserNameField(String logname) {
		return String.format("%s.%s", DeletedUser.logUsernameField,logname);
	}

	public static String getLogLastActivityField(LogEventsEnum eventId) {
		return String.format("%s.%s", DeletedUser.logLastActivityField,eventId.getId());
	}

	public DateTime getLastActivity() {
		return lastActivity;
	}

	public static String getAdInfoField(String adInfoFieldName) {
		return String.format("%s.%s", DeletedUser.adInfoField,adInfoFieldName);
	}

	public void cloneFromUser(User user)
	{

		this.displayName = user.getDisplayName();
		this.username = user.getUsername();
		this.noDomainUsername = user.getNoDomainUsername();
		this.followed = user.getFollowed();
		this.appUserDetailsMap = user.getApplicationUserDetails();
		this.logUsernameMap = user.getLogUsernameMap();
		this.scores = user.getScores();
		this.searchField = user.getSearchField();
		this.adInfo = user.getAdInfo();
		this.whenCreated = user.getWhenCreated();
		this.logLastActivityMap = user.getLogLastActivityMap();
		this.tags = user.getTags();


	}

}

