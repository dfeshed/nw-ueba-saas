 package fortscale.activedirectory.qos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import fortscale.domain.ad.AdUser;
import fortscale.utils.logging.Logger;

public class QoS {

	ArrayList<AdUser> 		validUsers;
	HashMap<String, AdUser> testUsers;
	ArrayList<AdUser> 		allUsers;

	ArrayList<String> adminGroups;
	
	int numTestUsers;
	int topUsersThreshold = 5;

	static final String SANITY_TEST_SUPER_POWER_USER_NAME = "Sanity Test Super Power User";
	static final String SANITY_TEST_RESULT_FORMAT = "Test Results - Total Number of Users: %s. Number of Test Users: %s. Test Goal: Reach the first %s places. Result: %s" ;	
	
	static final String TEST_USER_NAME = "Test User %s";
	static final String TEST_RESULT_FORMAT = "Test Results - Total Number of Users: %s. Number of Test Users: %s. Test Goal: Reach the first %s places. Success Rate: %s / %s (%s%%)" ;

	String testResults;
	int testSuccessRate;
	
	private static final Logger logger = Logger.getLogger(QoS.class);



	public QoS() {
		validUsers = new ArrayList<AdUser>();
		testUsers  = new HashMap<String, AdUser>();
		allUsers   = new ArrayList<AdUser>();
	}


	public Iterable<AdUser> generateQoSTestUsers(Iterable<AdUser> users) {
		for (AdUser adUser : users) {
			this.validUsers.add(adUser);
		}

		numTestUsers = setNumTestUsers();
		for (int i=0; i<numTestUsers; i++) {
			AdUser adminUser = pickAdminUser(validUsers);
			AdUser nonAdminUser = pickNonAdminUser(validUsers);
			
			AdUser testUser = createTestUser(String.format(TEST_USER_NAME, i+1), adminUser, nonAdminUser);
			testUsers.put(testUser.getDistinguishedName(), testUser);
		}

		
		allUsers.addAll(validUsers);
		allUsers.addAll(testUsers.values());
		return allUsers;
	}
	
	
	public Iterable<AdUser> generateSanityTestUsers(Iterable<AdUser> users) {
		
		ArrayList<AdUser> tempUsersList = new ArrayList<AdUser>();
		for (AdUser adUser : users) {
			tempUsersList.add(adUser);
		}

		Random rand = new Random();
		for (int i=0; i<tempUsersList.size(); i++) {
			int randomUserIndex = rand.nextInt(tempUsersList.size());
			AdUser adUser = tempUsersList.get(randomUserIndex);
			this.validUsers.add(adUser);
		}


		AdUser sanityPowerUser = new AdUser(SANITY_TEST_SUPER_POWER_USER_NAME);
		String memberOf = "";
		for (AdUser adUser : validUsers) {
			memberOf += adUser.getMemberOf();
		}
		sanityPowerUser.setMemberOf(memberOf);
		testUsers.put(SANITY_TEST_SUPER_POWER_USER_NAME, sanityPowerUser);
		
		
		allUsers.addAll(validUsers);
		allUsers.addAll(testUsers.values());
		return allUsers;
	}

	
	public Iterable<AdUser> generateAutoTestUsers(Iterable<AdUser> users) {
		ArrayList<AdUser> tempUsersList = new ArrayList<AdUser>();
		for (AdUser adUser : users) {
			tempUsersList.add(adUser);
		}

		Random rand = new Random();
		for (int i=0; i<tempUsersList.size(); i++) {
			int randomUserIndex = rand.nextInt(tempUsersList.size());
			AdUser adUser = tempUsersList.get(randomUserIndex);
			this.validUsers.add(adUser);
		}
		
		numTestUsers = setNumTestUsers();
		for (int i=0; i<numTestUsers; i++) {
			AdUser adminUser = pickAdminUser(validUsers);
			AdUser nonAdminUser = pickNonAdminUser(validUsers);
			
			AdUser testUser = createTestUser(String.format(TEST_USER_NAME, i+1), adminUser, nonAdminUser);
			testUsers.put(testUser.getDistinguishedName(), testUser);
		}

		
		allUsers.addAll(validUsers);
		allUsers.addAll(testUsers.values());
		return allUsers;
	}

	
	public Iterable<AdUser> generateManualTestUsers(Iterable<AdUser> users) {
		ArrayList<AdUser> tempUsersList = new ArrayList<AdUser>();
		for (AdUser adUser : users) {
			tempUsersList.add(adUser);
		}

		Random rand = new Random();
		for (int i=0; i<tempUsersList.size(); i++) {
			int randomUserIndex = rand.nextInt(tempUsersList.size());
			AdUser adUser = tempUsersList.get(randomUserIndex);
			this.validUsers.add(adUser);
		}

		
		findAdminGroups();
		
		numTestUsers = setNumTestUsers();
		for (int i=0; i<numTestUsers; i++) {
			AdUser normalUser = pickNormalUser(validUsers);
			
			AdUser testUser = createManualTestUser(String.format(TEST_USER_NAME, i+1), normalUser);
			testUsers.put(testUser.getDistinguishedName(), testUser);
		}

		
		allUsers.addAll(validUsers);
		allUsers.addAll(testUsers.values());
		return allUsers;
	}
	
	
	public void computeSanityTestResults(Map<String, Double> userScores) {
		ArrayList<Entry<String,Double>> sortedScoreList = sortScoreMap(userScores) ;
		
		topUsersThreshold = 1 ;
		
		testResults = "";
		boolean result = true; 
		for (int i=0; i<topUsersThreshold; i++) {
			testResults += (i+1) + ". " + sortedScoreList.get(i).getKey() + ", Score: " + sortedScoreList.get(i).getValue() + "<BR>";
			if (!testUsers.containsKey(sortedScoreList.get(i).getKey())) {
				result = false;
			}
		}
		
		testSuccessRate = result ? 100 : 0;
		testResults += String.format(SANITY_TEST_RESULT_FORMAT, allUsers.size(), testUsers.size(), topUsersThreshold, result) ;
		logger.info(testResults);		
	}
	
	
	public void computeTestResults(Map<String, Double> userScores) {
		ArrayList<Entry<String,Double>> sortedScoreList = sortScoreMap(userScores) ;
		
		topUsersThreshold = Math.max(5 , testUsers.size() * 2) ;
		
		testResults = "";
		int successes = 0;
		for (int i=0; i<topUsersThreshold; i++) {
			testResults += (i+1) + ". " + sortedScoreList.get(i).getKey() + ", Score: " + sortedScoreList.get(i).getValue() + "<BR>";
			if (testUsers.containsKey(sortedScoreList.get(i).getKey())) {
				successes++;
			}
		}
		
		testSuccessRate = (int)Math.round(100.0 * successes / testUsers.size());
		testResults += String.format(TEST_RESULT_FORMAT, allUsers.size(), testUsers.size(), topUsersThreshold, successes, testUsers.size(), testSuccessRate) ;
		logger.info(testResults);
	}
	
	
	public String getTestResults() {
		return this.testResults;
	}
	
	
	public int getTestSuccessRate() {
		return this.testSuccessRate;
	}
	
	
	private int setNumTestUsers() {
		Random rand = new Random();
		return (rand.nextInt(10) + 1);
	}

	
	private void findAdminGroups() {
		adminGroups = new ArrayList<String>();
		for (AdUser adUser : this.validUsers) {
			if (adminGroups.size()<3) {
				for (String group : adUser.getMemberOf().split(";")) {
					if (group.startsWith("CN=Enterprise Admins,") || group.startsWith("CN=Domain Admins,") || group.startsWith("CN=Administrators,")) {
						if (!adminGroups.contains(group)) {
							adminGroups.add(group);
						}
					}
				}
			}
		}
	}

	
	private AdUser pickAdminUser(ArrayList<AdUser> validUsers) {
		AdUser adminUser;
		while (true) {
			adminUser = randomSelectUser(validUsers);
			if (isAdminUser(adminUser)) {
				return adminUser;
			}
		}
	}

	
	private AdUser pickNonAdminUser(ArrayList<AdUser> validUsers) {
		AdUser nonAdminUser;
		while (true) {
			nonAdminUser = randomSelectUser(validUsers);
			if (!isAdminUser(nonAdminUser)) {
				return nonAdminUser;
			}
		}
	}
	
	
	private AdUser pickNormalUser(ArrayList<AdUser> validUsers) {
		AdUser normalUser;
		while (true) {
			normalUser = randomSelectUser(validUsers);
			
			if (isNormalUser(normalUser)) {
				return normalUser;
			}
		}
	}
	
	
	private AdUser randomSelectUser(ArrayList<AdUser> validUsers) {
		Random rand = new Random();
		int randomIndex = rand.nextInt(validUsers.size());
		return validUsers.get(randomIndex);
	}
	
	
	private boolean isAdminUser(AdUser adUser) {
		String memberOf = adUser.getMemberOf();
		return (memberOf.contains("Admin") || adUser.getDistinguishedName().contains("Admin")) ? true : false; 
	}
	

	private boolean isNormalUser(AdUser normalUser) {
		String memberOf = normalUser.getMemberOf();
		for (String group : memberOf.split(";")) {
			if (adminGroups.contains(group)) {return false;}
		}
		
		int uac = Integer.parseInt(normalUser.getUserAccountControl());
		if (((uac & 32) == 32) || ((uac & 65536) == 65536)) {return false;}

		return true;

	}
	
	
	private AdUser createTestUser(String testUserName, AdUser adminUser, AdUser nonAdminUser) {
		
		AdUser testUser = new AdUser(testUserName);
		
		Map<String, String> adminAttrVals =    adminUser.getAttrVals();
		Map<String, String> nonAdminAttrVals = nonAdminUser.getAttrVals();
		Map<String, String> testAttrVals =     testUser.getAttrVals();
		
		String testValue;
		for (String attr : testAttrVals.keySet()) {
			if (attr.equals("distinguishedName")) {
				testValue = createTestUserDistinguishedName(testUser.getDistinguishedName(), adminUser.getDistinguishedName(), nonAdminUser.getDistinguishedName());
				testUser.setDistinguishedName(testValue);
				continue;
			}
			if (attr.equals("memberOf")) {
				testValue = createTestUserMemberOf(adminAttrVals.get("memberOf"), nonAdminAttrVals.get("memberOf"));
				testAttrVals.put(attr, testValue);
				continue;
			}
			
			if (attr.equals("userAccountControl")) {
				testValue = createTestUserAccountControl(Integer.parseInt(adminAttrVals.get(attr)), Integer.parseInt(nonAdminAttrVals.get(attr)));
				testAttrVals.put(attr, testValue);
				continue;
			}
			
			Random rand = new Random();
			testValue = rand.nextBoolean() ? adminAttrVals.get(attr) : nonAdminAttrVals.get(attr);
			testAttrVals.put(attr, testValue);
		}
		
		testUser.updateAttrVals(testAttrVals);
		
//		logger.debug("Admin attributes: {}", adminAttrVals.toString());
//		logger.debug("Non-Admin attributes: {}", nonAdminAttrVals.toString());
//		logger.debug("Test user attributes: {}", testAttrVals.toString());
		
		return testUser;
	}

	
	private String createTestUserDistinguishedName(String testDn, String adminDn, String nonAdminDn) {
		Random rand = new Random();
		return rand.nextBoolean() ? testDn + adminDn : testDn + nonAdminDn;
	}
	
	
	private String createTestUserMemberOf(String adminMemberOf, String nonAdminMemberOf) {
		Random rand = new Random();
		String value = "";
		for (String group : adminMemberOf.split(";")) {
			value += rand.nextBoolean() ? group + ";" : "" ;
		}
		for (String group : nonAdminMemberOf.split(";")) {
			value += rand.nextBoolean() ? group + ";" : "" ;
		}
		value = value.endsWith(";") ? value.substring(0, value.length()-1) : value;
		return value;

	}
	
	
	private String createTestUserAccountControl(int adminUac, int nonAdminUac) {
		Random rand = new Random();
		int testUac = 0;
		for (int flag : new Integer[]{1,2,8,16,32,64,128,256,512,2048,4096,8192,65536,131072,262144,524288,1048576,2097152,4194304,8388608,16777216,67108864}) {
			int adminFlag =       ((adminUac & flag) == flag) ? 0 : flag;
			int nonAdminFlag = ((nonAdminUac & flag) == flag) ? 0 : flag;
			testUac += rand.nextBoolean() ? adminFlag : nonAdminFlag;
		}
		
		return testUac+"";
		
	}

	
	private AdUser createManualTestUser(String testUserName, AdUser normalUser) {
		AdUser testUser = new AdUser(testUserName);
		
		Map<String, String> normalUserAttrVals = normalUser.getAttrVals();
		Map<String, String> testAttrVals = testUser.getAttrVals();
		
		Random rand = new Random();
		for (Entry<String, String> entry : normalUserAttrVals.entrySet()) {
			if (entry.getKey().equals("distinguishedName")) {
				testAttrVals.put("distinguishedName", testUserName + ": " + entry.getValue());
				continue;
			}
			if (entry.getKey().equals("memberOf")) {
				String memberOf = entry.getValue();
				memberOf += memberOf.isEmpty() ? adminGroups.get(rand.nextInt(adminGroups.size())) : ";" + adminGroups.get(rand.nextInt(adminGroups.size()));
				testAttrVals.put("memberOf", memberOf);
				continue;
			}
			
			if (entry.getKey().equals("userAccountControl")) {
				int uac = Integer.parseInt(entry.getValue());
				uac += rand.nextBoolean() ?  32 : 65536 ;
				testAttrVals.put("userAccountControl", uac+"");
				continue;
				
			}

			testAttrVals.put(entry.getKey(), entry.getValue());
		}
		
		testUser.updateAttrVals(testAttrVals);

		return testUser;
		
	}
	
	
	private ArrayList<Entry<String,Double>> sortScoreMap(Map<String, Double> userScoresMap) {
		ArrayList<Entry<String,Double>> sortedScoreList = new ArrayList<Entry<String,Double>>(); 
	
		sortedScoreList.addAll(userScoresMap.entrySet());
	
		Comparator<Entry<String,Double>> scoreComparator = new Comparator<Entry<String,Double>>() {
			public int compare(Entry<String,Double> e1, Entry<String,Double> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		};
		
		Collections.sort(sortedScoreList, scoreComparator);
		
		return sortedScoreList;
	}

}
