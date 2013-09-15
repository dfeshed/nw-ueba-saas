package fortscale.activedirectory.featureextraction;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import fortscale.domain.ad.AdUser;
import fortscale.domain.fe.IFeature;

public class ADFeatureExtractorTest {
	
	static ADFeatureExtractor adFeatureExtractor = new ADFeatureExtractor();
	static HashMap<String, Double> expectedFeatureValues = new HashMap<String, Double>();
	static AdUser adUser;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		adUser = new AdUser("CN=John Doe,OU=Company-Users,DC=Company,DC=dom");
		adUser.setAccountExpires("0");
		adUser.setBadPasswordTime("2013/08/25T08:35:47");
		adUser.setBadPwdCount("0");
		adUser.setCn("John Doe");
		adUser.setDisplayName("John Doe");
		adUser.setFirstname("John");
		adUser.setLastLogoff("0");
		adUser.setLastLogon("2013/08/28T17:40:57");
		adUser.setLastname("Doe");
		adUser.setLockoutTime("0");
		adUser.setLogonCount("606");
		adUser.setLogonHours("////////////////////////////");
		adUser.setManager("CN=Manager,OU=Company-Users,DC=Company,DC=dom");
		adUser.setMemberOf("CN=VPN-Users,OU=Company-Users,DC=Company,DC=dom");
		adUser.setPrimaryGroupID("513");
		adUser.setPwdLastSet("2013/08/18T09:36:23");
		adUser.setsAMAccountType("805306368");
		adUser.setUserAccountControl("512");
		adUser.setUserParameters("bTogICAgICAgICAgICAgICAgICAgIGQJICAgICAgICAgICAgICAgICAgICAgICAg");
		adUser.setUserPrincipalName("john_d@company.dom");
		adUser.setWhenChanged("2013/08/19T14:19:16");
		adUser.setWhenCreated("2012/12/27T16:31:03");
		
		expectedFeatureValues.put(String.format(ADFeatureExtractor.FEATURE_MEMBER_OF_GROUP_UNIQUE_NAME,"CN=VPN-Users,OU=Company-Users,DC=Company,DC=dom"), Feature.POSITIVE_STATUS);
		expectedFeatureValues.put(String.format(ADFeatureExtractor.FEATURE_MEMBER_OF_OU_UNIQUE_NAME,"OU=Company-Users,DC=Company,DC=dom"), Feature.POSITIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_ACCOUNT_IS_DISABLED_UNIQUE_NAME, Feature.NEGATIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_NO_PASSWORD_REQUIRED_UNIQUE_NAME, Feature.NEGATIVE_STATUS); 
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_NOT_NORMAL_USER_ACCOUNT_UNIQUE_NAME, Feature.NEGATIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_INTERDOMAIN_TRUST_ACCOUNT_UNIQUE_NAME, Feature.NEGATIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_PASSWORD_NEVER_EXPIRES_UNIQUE_NAME, Feature.NEGATIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_DES_KEY_ONLY_UNIQUE_NAME, Feature.NEGATIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_ACCOUNT_NEVER_EXPIRES_UNIQUE_NAME, Feature.POSITIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_IS_CRITICAL_SYSTEM_OBJECT_UNIQUE_NAME, Feature.NEGATIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_ACCOUNT_HAS_NO_DESCRIPTION_UNIQUE_NAME, Feature.POSITIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_ACCOUNT_HAS_NO_MAILBOX_UNIQUE_NAME, Feature.POSITIVE_STATUS);
		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_NUMBER_OF_SUBORDINATES_UNIQUE_NAME, 0.0);
//		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_ACTIVITY_DAYS_SINCE_LAST_LOGON_UNIQUE_NAME, );
//		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_ACTIVITY_DAYS_SINCE_PASSWORD_LAST_SET_UNIQUE_NAME, );
//		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_LOGON_ACTIVITIES_NORMALIZED_UNIQUE_NAME, );
//		expectedFeatureValues.put(ADFeatureExtractor.FEATURE_LOGON_FAILURE_ACTIVITIES_NORMALIZED_UNIQUE_NAME, );

		
	}

	@Test
	public void testParseUserFeatures() {
		Map<String, IFeature> userFeatures = adFeatureExtractor.parseUserFeatures(adUser);
		
		for (IFeature feature : userFeatures.values()) {
			if (expectedFeatureValues.containsKey(feature.getFeatureUniqueName())) {
				assertTrue("Feature Extraction works properly on feature: " + feature.getFeatureUniqueName(), feature.getFeatureValue().equals(expectedFeatureValues.get(feature.getFeatureUniqueName())));
			}
		}
		
	}

}
