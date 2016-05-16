package fortscale.collection.ad;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fortscale.domain.ad.AdConnection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fortscale.collection.jobs.ad.AdFetchJob;

/**
 * Created by Amir Keren on 17/05/2015.
 */
@Ignore("Just for debugging purposes for now")
public class ADMockFetchTest {

	private AdFetchJob adFetchJob;
	private List<AdConnection> adConnections;

	@Before
	public void setUp() throws Exception {
		adFetchJob = mock(AdFetchJob.class);
		//AdConnection connection = mock(AdConnection.class); // if you want a list of mocked connections just create some of these and add it to the list below
		adConnections = Arrays.asList();
	}

	@Test
	public void groupFetchTest() throws Exception {
		String filter = "(&(objectclass=group))";
		String adFields = "distinguishedName,name,isCriticalSystemObject,groupType,sAMAccountType,member,whenChanged," +
				"whenCreated,cn,description,sAMAccountName,objectSid,objectGUID";
		String expected = "isCriticalSystemObject: ????\n" +
				"name: ????\n" +
				"groupType: ????\n" +
				"objectSid: ????\n" +
				"sAMAccountType: ????\n" +
				"member: ????\n" +
				"member: ????\n" +
				"dn: ????\n" +
				"distinguishedName: ????\n" +
				"sAMAccountName: ????\n" +
				"objectGUID: ????\n" +
				"cn: ????\n" +
				"whenCreated: ????\n" +
				"description: ????\n" +
				"whenChanged: ????\n" +
				"\n";
		runDebugUtility(filter, adFields, expected);
	}

	@Test
	public void userFetchTest() throws Exception {
		String filter = "(&(objectclass=user)(!(objectclass=computer)))";
		String adFields = "distinguishedName,badPwdCount,logonCount,primaryGroupID,sAMAccountType,userAccountControl," +
				"accountExpires,badPasswordTime,lastLogoff,memberOf,whenChanged,cn,displayName,givenName,l," +
				"userPrincipalName,lastLogon,pwdLastSet,whenCreated,sn,sAMAccountName,objectSid,objectGUID";
		String expected = "displayName: ???\n" +
				"givenName: ???\n" +
				"sAMAccountType: ???\n" +
				"primaryGroupID: ???\n" +
				"badPasswordTime: ???\n" +
				"cn: ???\n" +
				"userAccountControl: ???\n" +
				"userPrincipalName: ???\n" +
				"dn: ???\n" +
				"distinguishedName: ???\n" +
				"whenChanged: ???\n" +
				"whenCreated: ???\n" +
				"pwdLastSet: ???\n" +
				"logonCount: ???\n" +
				"accountExpires: ???\n" +
				"lastLogoff: ???\n" +
				"objectGUID: ???\n" +
				"sn: ???\n" +
				"lastLogon: ???\n" +
				"objectSid: ???\n" +
				"sAMAccountName: ???\n" +
				"memberOf: ???\n" +
				"badPwdCount: ???\n" +
				"\n";
		runDebugUtility(filter, adFields, expected);
	}

	@Test
	public void computerFetchTest() throws Exception {
		String filter = "(&(objectclass=computer))";
		String adFields = "distinguishedName,operatingSystem,lastLogoff,lastLogon,logonCount,whenChanged,whenCreated," +
				"cn,pwdLastSet,objectSid,objectGUID";
		String expected = "objectSid: ???\n" +
				"logonCount: ???\n" +
				"pwdLastSet: ???\n" +
				"lastLogoff: ???\n" +
				"dn: ???\n" +
				"distinguishedName: ???\n" +
				"objectGUID: ???\n" +
				"cn: ???\n" +
				"whenCreated: ???\n" +
				"lastLogon: ???\n" +
				"operatingSystem: ???\n" +
				"whenChanged: ???\n" +
				"\n";
		runDebugUtility(filter, adFields, expected);
	}

	@Test
	public void ouFetchTest() throws Exception {
		String filter = "(&(objectclass=organizationalUnit))";
		String adFields = "distinguishedName,whenChanged,whenCreated,ou,objectGUID";
		String expected = "objectGUID: ???\n" +
				"whenChanged: ???\n" +
				"ou: ???\n" +
				"whenCreated: ???\n" +
				"dn: ???\n" +
				"distinguishedName: ???\n" +
				"\n";
		runDebugUtility(filter, adFields, expected);
	}

	private void runDebugUtility(String filter, String adFields, String expected) throws Exception {
		adFetchJob.fetchFromActiveDirectory(new BufferedWriter(new StringWriter()), filter, adFields, 1);
		adFields = "dn," + adFields;
		for (String adField: adFields.split(",")) {
			String patternString = ".*" + adField + ": .*\n";
			Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(expected);
			assertTrue(matcher.matches());
		}
	}

}