package fortscale.domain.core;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;







@Document
public class User extends AbstractDocument {	
	public static final String appField = "app";
	public static final String logUsernameField = "logUsername";
	public static final String usernameField = "username";
	public static final String searchFieldName = "sf";
	public static final String classifierScoreField = "scores";
	public static final String followedField = "followed";
	public static final String adInfoField = "adInfo";
	
	
	@Indexed
	@Field(usernameField)
	private String username;
	
	@Field(followedField)
	private Boolean followed = false;
	
	@Field(appField)
	@JsonProperty
	Map<String, ApplicationUserDetails> appUserDetailsMap = new HashMap<>();
	
	@Field(logUsernameField)
	@JsonProperty
	Map<String, String> logUsernameMap = new HashMap<>();
	
	@Indexed
	@Field(classifierScoreField)
	private HashMap<String, ClassifierScore> scores = new HashMap<String, ClassifierScore>();
	
	@Field(searchFieldName)
	@Indexed
	private String searchField;
	
	@Field(adInfoField)
	private UserAdInfo adInfo = new UserAdInfo();
	
	private String adDn;
	
	public String getAdDn() {
		return adDn;
	}
	
	public void setAdDn(String adDn) {
		this.adDn = adDn;
	}
		
	

	/**
	 * Creates a new {@link User} from the given adDn.
	 * 
	 * @param adDn must not be {@literal null} or empty.
	 */
//	@PersistenceConstructor
//	@JsonCreator
//	public User(@JsonProperty("adDn") String adDn) {
//
//		Assert.hasText(adDn);
//
//		this.adDn = adDn;
//	}
	
	
	public UserAdInfo getAdInfo() {
		return adInfo;
	}

	public void setAdInfo(UserAdInfo adInfo) {
		this.adInfo = adInfo;
	}
	
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
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
	
	public Map<String, String> getLogUsernameMap(){
		return logUsernameMap;
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
	
	public static String getLogUserNameField(String logname) {
		return String.format("%s.%s", User.logUsernameField,logname);
	}
	
	public static String getAdInfoField(String adInfoFieldName) {
		return String.format("%s.%s", User.adInfoField,adInfoFieldName);
	}
}
