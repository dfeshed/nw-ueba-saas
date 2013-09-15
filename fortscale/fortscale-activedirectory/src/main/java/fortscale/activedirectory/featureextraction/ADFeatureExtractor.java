package fortscale.activedirectory.featureextraction;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fortscale.domain.ad.AdUser;
import fortscale.domain.fe.IFeature;
import fortscale.utils.logging.Logger;


public class ADFeatureExtractor {

	public static final String FEATURE_MEMBER_OF_GROUP_UNIQUE_NAME = "Group_Member_%s" ;
	public static final String FEATURE_MEMBER_OF_GROUP_DISPLAY_NAME = "Member of the Group: %s" ;
	public static final String FEATURE_MEMBER_OF_OU_UNIQUE_NAME = "OU_Member_%s" ;
	public static final String FEATURE_MEMBER_OF_OU_DISPLAY_NAME = "Member of the Organizational Unit: %s" ;
	public static final String FEATURE_ACCOUNT_IS_DISABLED_UNIQUE_NAME = "account_is_disabled" ;
	public static final String FEATURE_ACCOUNT_IS_DISABLED_DISPLAY_NAME = "User Account is Disabled" ;
	public static final String FEATURE_NO_PASSWORD_REQUIRED_UNIQUE_NAME = "authentication_no_password_required";
	public static final String FEATURE_NO_PASSWORD_REQUIRED_DISPLAY_NAME = "No Password is Required";
	public static final String FEATURE_NOT_NORMAL_USER_ACCOUNT_UNIQUE_NAME = "account_not_normal_user_account" ;
	public static final String FEATURE_NOT_NORMAL_USER_ACCOUNT_DISPLAY_NAME = "Not a Normal User Account" ;
	public static final String FEATURE_INTERDOMAIN_TRUST_ACCOUNT_UNIQUE_NAME = "account_interdomain_trust_account";
	public static final String FEATURE_INTERDOMAIN_TRUST_ACCOUNT_DISPLAY_NAME = "InterDomain Trust Account";
	public static final String FEATURE_PASSWORD_NEVER_EXPIRES_UNIQUE_NAME = "authentication_password_never_expires";
	public static final String FEATURE_PASSWORD_NEVER_EXPIRES_DISPLAY_NAME = "Password Never Expires";
	public static final String FEATURE_DES_KEY_ONLY_UNIQUE_NAME = "authentication_des_key_only";
	public static final String FEATURE_DES_KEY_ONLY_DISPLAY_NAME = "DES Key Only";
	public static final String FEATURE_ACCOUNT_NEVER_EXPIRES_UNIQUE_NAME = "account_never_expires";
	public static final String FEATURE_ACCOUNT_NEVER_EXPIRES_DISPLAY_NAME = "Account Never Expires";
	public static final String FEATURE_IS_CRITICAL_SYSTEM_OBJECT_UNIQUE_NAME = "isCriticalSystemObject";
	public static final String FEATURE_IS_CRITICAL_SYSTEM_OBJECT_DISPLAY_NAME = "Critical System Object";
	public static final String FEATURE_ACCOUNT_HAS_NO_DESCRIPTION_UNIQUE_NAME = "account_has_no_description";
	public static final String FEATURE_ACCOUNT_HAS_NO_DESCRIPTION_DISPLAY_NAME = "Account Has No Description";
	public static final String FEATURE_ACCOUNT_HAS_NO_MAILBOX_UNIQUE_NAME = "mail_has_no_mailbox";
	public static final String FEATURE_ACCOUNT_HAS_NO_MAILBOX_DISPLAY_NAME = "Account Doesn't Own Mailbox";
	public static final String FEATURE_ACTIVITY_DAYS_SINCE_LAST_LOGON_UNIQUE_NAME = "activity_days_since_last_logon";
	public static final String FEATURE_ACTIVITY_DAYS_SINCE_LAST_LOGON_DISPLAY_NAME = "Activity Days Since Last Logon";
	public static final String FEATURE_ACTIVITY_DAYS_SINCE_PASSWORD_LAST_SET_UNIQUE_NAME = "activity_days_since_password_last_set";
	public static final String FEATURE_ACTIVITY_DAYS_SINCE_PASSWORD_LAST_SET_DISPLAY_NAME = "Activity Days Since Password Was Last Set";
	public static final String FEATURE_LOGON_ACTIVITIES_NORMALIZED_UNIQUE_NAME = "activity_logon_activities_counter_normalized_by_whenCreated";
	public static final String FEATURE_LOGON_ACTIVITIES_NORMALIZED_DISPLAY_NAME = "Logon Activities (Normalized)";
	public static final String FEATURE_LOGON_FAILURE_ACTIVITIES_NORMALIZED_UNIQUE_NAME = "activity_logon_failures_counter_normalized_by_whenCreated";
	public static final String FEATURE_LOGON_FAILURE_ACTIVITIES_NORMALIZED_DISPLAY_NAME = "Logon Failure Activities (Normalized)";
	public static final String FEATURE_NUMBER_OF_SUBORDINATES_UNIQUE_NAME = "organizational_number_of_subordinates";
	public static final String FEATURE_NUMBER_OF_SUBORDINATES_DISPLAY_NAME = "Number of Subordinates";
	
	public static final String LAST_LOGON_DEFAULT_VALUE = "0";
	public static final String PWDLASTSET_DEFAULT_VALUE = "0";

	private static final Logger logger = Logger.getLogger(ADFeatureExtractor.class);
	private HashMap<String, IFeature> userFeatures = null;
	private ADUserParser adUserParser = null;
	private double activityDays;
	

	public Map<String, IFeature> parseUserFeatures(AdUser adUser) {
		userFeatures = new HashMap<String, IFeature>();
		adUserParser = new ADUserParser();

		try {
			activityDays = adUserParser.getActivityDays(adUser.getWhenCreated());
		}
		catch (ParseException e) {
			logger.warn("Parse Exception on whenCreated value of :{}", adUser.getWhenCreated());
			activityDays = 0.0;
		}
		
		parseOUFeature(adUser);
		parseGroupFeatures(adUser);
		parseUserAccountControlFeatures(adUser);
		parseAccountNeverExpiresFeature(adUser);
		parseIsCriticalSystemObjectFeature(adUser);
		parseAccountHasDescriptionFeature(adUser);
		parseAccountHasMailboxFeature(adUser);
		parseActivityDaysSinceLastLogonFeature(adUser);
		parseActivityDaysSincePasswordLastSetFeature(adUser);
		parseLogonActivitiesNormalizedFeature(adUser);
		parseLogonFailureActivitiesNormalizedFeature(adUser);
		parseNumberOfSubordinatesFeature(adUser);
		
		return userFeatures;
		
	}
	
	
	private void parseOUFeature(AdUser adUser) {
		String dn = adUser.getDistinguishedName();
		String ou = adUserParser.parseOUFromDN(dn);
		if (null != ou) {
			String featureUniqueName = String.format(FEATURE_MEMBER_OF_OU_UNIQUE_NAME, ou);
			String featureDisplayName = String.format(FEATURE_MEMBER_OF_OU_DISPLAY_NAME, ou);
			int featureType = Feature.FEATURE_TYPE_BOOLEAN;
			Double featureDefaultValue = Feature.NEGATIVE_STATUS ;
			Double featureValue = Feature.POSITIVE_STATUS;
			Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
			userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
		}
	}
	
	
	private void parseGroupFeatures(AdUser adUser) {
		String memberOf = adUser.getMemberOf();
		String[] userGroups = adUserParser.getUserGroups(memberOf);
		for (String group : userGroups) {
			if (group.isEmpty()) {continue;}
			String featureUniqueName = String.format(FEATURE_MEMBER_OF_GROUP_UNIQUE_NAME, group);
			String featureDisplayName = String.format(FEATURE_MEMBER_OF_GROUP_DISPLAY_NAME, group);
			int featureType = Feature.FEATURE_TYPE_BOOLEAN;
			Double featureDefaultValue = Feature.NEGATIVE_STATUS ;
			Double featureValue = Feature.POSITIVE_STATUS;
			Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
			userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
		}
	}
	
	
	private void parseUserAccountControlFeatures(AdUser adUser) {
		String userAccountControl = adUser.getUserAccountControl();
		
		String featureUniqueName;
		String featureDisplayName;
		int featureType = Feature.FEATURE_TYPE_BOOLEAN;
		Double featureDefaultValue = Feature.NEGATIVE_STATUS;
		Double featureValue;
		Feature feature;
		
		// Account is Disabled
		featureUniqueName = FEATURE_ACCOUNT_IS_DISABLED_UNIQUE_NAME;
		featureDisplayName= FEATURE_ACCOUNT_IS_DISABLED_DISPLAY_NAME;
		featureValue = userAccountControl.isEmpty() ? null : adUserParser.getAccountIsDisabledValue(userAccountControl);
		feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
		
		// No Password Required
		featureUniqueName = FEATURE_NO_PASSWORD_REQUIRED_UNIQUE_NAME;
		featureDisplayName= FEATURE_NO_PASSWORD_REQUIRED_DISPLAY_NAME;
		featureValue = userAccountControl.isEmpty() ? null : adUserParser.getNoPasswordRequiresValue(userAccountControl);
		feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
		
		// Normal User Account
		featureUniqueName = FEATURE_NOT_NORMAL_USER_ACCOUNT_UNIQUE_NAME;
		featureDisplayName= FEATURE_NOT_NORMAL_USER_ACCOUNT_DISPLAY_NAME;
		featureValue = userAccountControl.isEmpty() ? null : adUserParser.getNormalUserAccountValue(userAccountControl);
		feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
		
		// Interdomain Trust Account
		featureUniqueName = FEATURE_INTERDOMAIN_TRUST_ACCOUNT_UNIQUE_NAME;
		featureDisplayName= FEATURE_INTERDOMAIN_TRUST_ACCOUNT_DISPLAY_NAME;
		featureValue = userAccountControl.isEmpty() ? null : adUserParser.getInterdomainTrustAccountValue(userAccountControl);
		feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);

		// Password Never Expires
		featureUniqueName = FEATURE_PASSWORD_NEVER_EXPIRES_UNIQUE_NAME;
		featureDisplayName= FEATURE_PASSWORD_NEVER_EXPIRES_DISPLAY_NAME;
		featureValue = userAccountControl.isEmpty() ? null : adUserParser.getPasswordNeverExpiresValue(userAccountControl);
		feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
		
		// Des Key Only
		featureUniqueName = FEATURE_DES_KEY_ONLY_UNIQUE_NAME;
		featureDisplayName= FEATURE_DES_KEY_ONLY_DISPLAY_NAME;
		featureValue = userAccountControl.isEmpty() ? null : adUserParser.getDesKeyOnlyValue(userAccountControl);
		feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);		
	}

	
	private void parseAccountNeverExpiresFeature(AdUser adUser) {
		String featureUniqueName = FEATURE_ACCOUNT_NEVER_EXPIRES_UNIQUE_NAME;
		String featureDisplayName = FEATURE_ACCOUNT_NEVER_EXPIRES_DISPLAY_NAME;
		int featureType = Feature.FEATURE_TYPE_BOOLEAN;
		Double featureDefaultValue = Feature.NEGATIVE_STATUS ;
		
		String accountExpires = adUser.getAccountExpires();
		Double featureValue = (accountExpires.equals("0") || accountExpires.equals("9223372036854775807")) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
		Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
	}
	
	
	private void parseIsCriticalSystemObjectFeature(AdUser adUser) {
		String featureUniqueName = FEATURE_IS_CRITICAL_SYSTEM_OBJECT_UNIQUE_NAME;
		String featureDisplayName = FEATURE_IS_CRITICAL_SYSTEM_OBJECT_DISPLAY_NAME;
		int featureType = Feature.FEATURE_TYPE_BOOLEAN;
		Double featureDefaultValue = Feature.NEGATIVE_STATUS ;
		
		String isCriticalSystemObject = adUser.getIsCriticalSystemObject();
		Double featureValue = isCriticalSystemObject.equalsIgnoreCase("TRUE") ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
		Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);		
	}
	
	
	private void parseAccountHasDescriptionFeature(AdUser adUser) {
		String featureUniqueName = FEATURE_ACCOUNT_HAS_NO_DESCRIPTION_UNIQUE_NAME;
		String featureDisplayName = FEATURE_ACCOUNT_HAS_NO_DESCRIPTION_DISPLAY_NAME;
		int featureType = Feature.FEATURE_TYPE_BOOLEAN;
		Double featureDefaultValue = Feature.POSITIVE_STATUS ;
		
		String accountDescription = adUser.getDescription();
		Double featureValue = accountDescription.isEmpty() ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
		Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
	}
	
	
	private void parseAccountHasMailboxFeature(AdUser adUser) {
		String featureUniqueName = FEATURE_ACCOUNT_HAS_NO_MAILBOX_UNIQUE_NAME;
		String featureDisplayName = FEATURE_ACCOUNT_HAS_NO_MAILBOX_DISPLAY_NAME;
		int featureType = Feature.FEATURE_TYPE_BOOLEAN;
		Double featureDefaultValue = Feature.POSITIVE_STATUS ;
		
		String emailAddress = adUser.getEmailAddress();
		Double featureValue = emailAddress.isEmpty() ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
		Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
	}
	
	
	private void parseActivityDaysSinceLastLogonFeature(AdUser adUser) {
		String featureUniqueName = FEATURE_ACTIVITY_DAYS_SINCE_LAST_LOGON_UNIQUE_NAME;
		String featureDisplayName = FEATURE_ACTIVITY_DAYS_SINCE_LAST_LOGON_DISPLAY_NAME;
		int featureType = Feature.FEATURE_TYPE_NUMERIC;
		Double featureDefaultValue = null ;
		
		String lastLogon = adUser.getLastLogon();
		Double featureValue = null;
		if (!lastLogon.equals(LAST_LOGON_DEFAULT_VALUE) && !lastLogon.isEmpty()) {
			try {
				Date starting_date = adUserParser.parseDate(lastLogon);
				Date now = new Date();
				long timeDifInMilliSec2 = now.getTime() - starting_date.getTime() ;
				Double counter_days = timeDifInMilliSec2 / (24 * 60 * 60 * 1000.0);
				counter_days = Math.round(counter_days * 100.0) / 100.0;
				featureValue = counter_days ;
			}
			catch (ParseException e) {
				logger.warn("Parse Exception on lastLogon value of :{}", lastLogon);
			}
		}

		Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);		
	}


	private void parseActivityDaysSincePasswordLastSetFeature(AdUser adUser) {
		String featureUniqueName = FEATURE_ACTIVITY_DAYS_SINCE_PASSWORD_LAST_SET_UNIQUE_NAME;
		String featureDisplayName = FEATURE_ACTIVITY_DAYS_SINCE_PASSWORD_LAST_SET_DISPLAY_NAME;
		int featureType = Feature.FEATURE_TYPE_NUMERIC;
		Double featureDefaultValue = null ;
		
		String pwdLastSet = adUser.getPwdLastSet();
		Double featureValue = null;
		if (!pwdLastSet.equals(PWDLASTSET_DEFAULT_VALUE) && !pwdLastSet.isEmpty()) {
			try {
				Date starting_date = adUserParser.parseDate(pwdLastSet);
				Date now = new Date();
				long timeDifInMilliSec2 = now.getTime() - starting_date.getTime() ;
				Double counter_days = timeDifInMilliSec2 / (24 * 60 * 60 * 1000.0);
				counter_days = Math.round(counter_days * 100.0) / 100.0;
				featureValue = counter_days ;
			}	
			catch (ParseException e) {
				logger.warn("Parse Exception on PwdLastSet value of :{}", pwdLastSet);
			}
		}
		Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);		
	}
	
	
	private void parseLogonActivitiesNormalizedFeature(AdUser adUser) {
		String featureUniqueName = FEATURE_LOGON_ACTIVITIES_NORMALIZED_UNIQUE_NAME;
		String featureDisplayName = FEATURE_LOGON_ACTIVITIES_NORMALIZED_DISPLAY_NAME;
		int featureType = Feature.FEATURE_TYPE_NUMERIC;
		Double featureDefaultValue = null ;
		
		String logonCount = adUser.getLogonCount();
		Double featureValue = (activityDays==0.0 || logonCount.isEmpty()) ? null : Math.round((Double.parseDouble(logonCount)/activityDays)*100.0) / 100.0;
		Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
	}
	
	
	private void parseLogonFailureActivitiesNormalizedFeature(AdUser adUser) {
		String featureUniqueName = FEATURE_LOGON_FAILURE_ACTIVITIES_NORMALIZED_UNIQUE_NAME;
		String featureDisplayName = FEATURE_LOGON_FAILURE_ACTIVITIES_NORMALIZED_DISPLAY_NAME;
		int featureType = Feature.FEATURE_TYPE_NUMERIC;
		Double featureDefaultValue = null ;
		
		String badPwdCount = adUser.getBadPwdCount();
		Double featureValue = (activityDays==0.0 || badPwdCount.isEmpty()) ? null : Double.parseDouble(badPwdCount)/activityDays;
		Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);
	}

	
	private void parseNumberOfSubordinatesFeature(AdUser adUser) {
		String featureUniqueName = FEATURE_NUMBER_OF_SUBORDINATES_UNIQUE_NAME;
		String featureDisplayName = FEATURE_NUMBER_OF_SUBORDINATES_DISPLAY_NAME;
		int featureType = Feature.FEATURE_TYPE_NUMERIC;
		Double featureDefaultValue = null ;
		
		String directReports = adUser.getDirectReports();
		Double featureValue = adUserParser.getNumberOfSubordinates(directReports);
		Feature feature = new Feature(featureUniqueName, featureDisplayName, featureType, featureDefaultValue, featureValue);
		userFeatures.put(feature.getFeatureUniqueName(), (IFeature)feature);		
	}


}

