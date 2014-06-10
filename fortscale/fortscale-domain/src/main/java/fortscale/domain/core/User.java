package fortscale.domain.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import fortscale.domain.events.LogEventsEnum;







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
		@CompoundIndex(name="active_directory_group_membershipScoreCurScore", def = "{'scores.active_directory_group_membership.score': -1}"),
		@CompoundIndex(name="active_directory_group_membershipScoreCurTrend", def = "{'scores.active_directory_group_membership.trendScore': -1}"),
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
	public static final String usernameField = "username";
	public static final String noDomainUsernameField = "noDomainUsername";
	public static final String displayNameField = "displayName";
	public static final String searchFieldName = "sf";
	public static final String classifierScoreField = "scores";
	public static final String followedField = "followed";
	public static final String adInfoField = "adInfo";
	public static final String userServiceAccountField = "userServiceAccount";
	public static final String administratorAccountField = "administratorAccount";

	@Indexed
	@Field(administratorAccountField)
	private Boolean administratorAccount;
	
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
	
	private String adDn;
	
	@Field(logLastActivityField)
	Map<String, DateTime> logLastActivityMap = new HashMap<>();
	
	
	
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
	
	
	
	
	

	
	
	
	
	public static String getClassifierScoreField(String classifierId) {
		return String.format("%s.%s", User.classifierScoreField, classifierId);
	}
	
	public static String getClassifierScoreCurrentTimestampField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.timestampField);
	}
	
	public static String getClassifierScoreCurrentScoreField(String classifierId) {
		return String.format("%s.%s.%s", User.classifierScoreField, classifierId, ScoreInfo.scoreField);
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
	
	public static String getLogLastActivityField(LogEventsEnum eventId) {
		return String.format("%s.%s", User.logLastActivityField,eventId.getId());
	}
	
	public static String getAdInfoField(String adInfoFieldName) {
		return String.format("%s.%s", User.adInfoField,adInfoFieldName);
	}

	public Boolean getUserServiceAccount() {
		return userServiceAccount;
	}

	public void setUserServiceAccount(Boolean userServiceAccount) {
		this.userServiceAccount = userServiceAccount;
	}

	public Boolean getAdministratorAccount() {
		return administratorAccount;
	}

	public void setAdministratorAccount(Boolean administratorAccount) {
		this.administratorAccount = administratorAccount;
	}
}
