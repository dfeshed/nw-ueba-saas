package fortscale.collection.ad;

import fortscale.domain.ad.AdConnection;
import fortscale.collection.jobs.ad.AdFetchJob;
import fortscale.utils.logging.Logger;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir Keren on 17/05/2015.
 *
 * This test CAN NOT and SHOULD NOT fail. It is only designed to help debug and understand the structure
 * of the Active Directory filters and queries and how we currently handle them.
 */
@Ignore("Just for debugging purposes for now")
public class ADFetchTest {

	private AdFetchJob adFetchJob;
	private List<AdConnection> adConnections;
	private static Logger logger = Logger.getLogger(AdFetchJob.class);
	private boolean initialized;

	@Before
	public void setUp() {
		try {
			adFetchJob = new AdFetchJob();
			PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
			adConnections = new ArrayList<>(); //when needed, change so it will receive a meaningful list of ad connections
			initialized = true;
		} catch (Exception ex) {
			logger.debug("Bad json file");
			initialized = false;
		}
	}

	@Test
	public void groupFetchTest() {
		String filter = "(&(objectclass=group))";
		String adFields = "distinguishedName,name,isCriticalSystemObject,isDeleted,groupType,sAMAccountType,memberOf," +
				"managedBy,managedObjects,masteredBy,member,nonSecurityMember,nonSecurityMemberBL,directReports," +
				"secretary,whenChanged,whenCreated,accountNameHistory,cn,description,displayName,mail,sAMAccountName," +
				"objectSid,objectGUID";
		String expected = "isCriticalSystemObject: TRUE\n" +
				"name: Administrators\n" +
				"groupType: -2147483643\n" +
				"objectSid: AQIAAAAAAAUgAAAAIAIAAA==\n" +
				"sAMAccountType: 536870912\n" +
				"member: CN=maria10 maria10,OU=MariaOU,DC=somebigcompany,DC=com\n" +
				"member: CN=eran zehavi,OU=IdanOU,DC=somebigcompany,DC=com\n" +
				"member: CN=gretchen gw. winder,OU=employees,DC=somebigcompany,DC=com\n" +
				"member: CN=Domain Admins,CN=Users,DC=somebigcompany,DC=com\n" +
				"member: CN=Enterprise Admins,CN=Users,DC=somebigcompany,DC=com\n" +
				"member: CN=Administrator,CN=Users,DC=somebigcompany,DC=com\n" +
				"dn: CN=Administrators,CN=Builtin,DC=somebigcompany,DC=com\n" +
				"distinguishedName: CN=Administrators,CN=Builtin,DC=somebigcompany,DC=com\n" +
				"sAMAccountName: Administrators\n" +
				"objectGUID: bKXurJQMLUqVPQtZg7E3UQ==\n" +
				"cn: Administrators\n" +
				"whenCreated: 20140609063653.0Z\n" +
				"description: Administrators have complete and unrestricted access to the computer/domain\n" +
				"whenChanged: 20140908160005.0Z\n" +
				"\n";
		if (initialized) {
			runTest(filter, adFields, expected);
		}
	}

	@Test
	public void userFetchTest() {
		String filter = "(&(objectclass=user)(!(objectclass=computer)))";
		String adFields = "distinguishedName,isCriticalSystemObject,isDeleted,badPwdCount,logonCount,primaryGroupID," +
				"sAMAccountType,userAccountControl,accountExpires,badPasswordTime,lastLogoff,lockoutTime,assistant," +
				"memberOf,managedObjects,manager,masteredBy,directReports,secretary,logonHours,whenChanged," +
				"streetAddress,cn,company,c,department,description,displayName,division,mail,employeeID," +
				"employeeNumber,employeeType,givenName,l,o,personalTitle,otherFacsimileTelephoneNumber," +
				"otherHomePhone,homePhone,otherMobile,mobile,otherTelephone,roomNumber,userPrincipalName," +
				"telephoneNumber,title,userParameters,userWorkstations,lastLogon,pwdLastSet,whenCreated,sn," +
				"sAMAccountName,objectSid,objectGUID";
		String expected = "sAMAccountType: 805306368\n" +
				"primaryGroupID: 513\n" +
				"badPasswordTime: 130765158717991216\n" +
				"cn: Administrator\n" +
				"userAccountControl: 66048\n" +
				"dn: CN=Administrator,CN=Users,DC=somebigcompany,DC=com\n" +
				"distinguishedName: CN=Administrator,CN=Users,DC=somebigcompany,DC=com\n" +
				"whenChanged: 20150518141426.0Z\n" +
				"whenCreated: 20140609063653.0Z\n" +
				"pwdLastSet: 130649217057274468\n" +
				"logonCount: 451\n" +
				"isCriticalSystemObject: TRUE\n" +
				"description: Built-in account for administering the computer/domain\n" +
				"lastLogoff: 0\n" +
				"accountExpires: 0\n" +
				"lockoutTime: 0\n" +
				"objectGUID: YP29xF+MnUiYdn8QT5VGUg==\n" +
				"lastLogon: 130765164629933600\n" +
				"objectSid: AQUAAAAAAAUVAAAAn0XL6XGRLPLs52Mv9AEAAA==\n" +
				"sAMAccountName: Administrator\n" +
				"memberOf: CN=TestGroup666,OU=sampleOU,DC=somebigcompany,DC=com\n" +
				"badPwdCount: 0\n" +
				"\n";
		if (initialized) {
			runTest(filter, adFields, expected);
		}
	}

	@Test
	public void computerFetchTest() {
		String filter = "(&(objectclass=computer))";
		String adFields = "distinguishedName,operatingSystem,operatingSystemHotfix,operatingSystemServicePack," +
				"operatingSystemVersion,lastLogoff,lastLogon,lastLogonTimestamp,logonCount,whenChanged,whenCreated," +
				"cn,description,pwdLastSet,memberOf,objectSid,objectGUID";
		String expected = "operatingSystemVersion: 6.1 (7601)\n" +
				"objectSid: AQUAAAAAAAUVAAAAn0XL6XGRLPLs52Mv6AMAAA==\n" +
				"logonCount: 2239\n" +
				"pwdLastSet: 130758276120568879\n" +
				"memberOf: CN=Cert Publishers,CN=Users,DC=somebigcompany,DC=com\n" +
				"lastLogoff: 0\n" +
				"lastLogonTimestamp: 130758017470036494\n" +
				"dn: CN=DEMO-DC,OU=Domain Controllers,DC=somebigcompany,DC=com\n" +
				"distinguishedName: CN=DEMO-DC,OU=Domain Controllers,DC=somebigcompany,DC=com\n" +
				"objectGUID: XZsKz6WUsEu0K7ppOtqFvw==\n" +
				"cn: DEMO-DC\n" +
				"whenCreated: 20140609063825.0Z\n" +
				"lastLogon: 130764929719261001\n" +
				"operatingSystem: Windows Server 2008 R2 Standard\n" +
				"whenChanged: 20150511142012.0Z\n" +
				"operatingSystemServicePack: Service Pack 1\n" +
				"\n";
		if (initialized) {
			runTest(filter, adFields, expected);
		}
	}

	@Test
	public void ouFetchTest() {
		String filter = "(&(objectclass=organizationalUnit))";
		String adFields = "distinguishedName,isCriticalSystemObject,isDeleted,defaultGroup,memberOf,managedBy," +
				"managedObjects,masteredBy,nonSecurityMemberBL,directReports,whenChanged,whenCreated,cn,c," +
				"description,displayName,l,ou,objectSid,objectGUID";
		String expected = "objectGUID: HyJ4az76QE6+IBqseVw7WA==\n" +
				"whenChanged: 20140609063652.0Z\n" +
				"ou: Domain Controllers\n" +
				"whenCreated: 20140609063652.0Z\n" +
				"description: Default container for domain controllers\n" +
				"isCriticalSystemObject: TRUE\n" +
				"dn: OU=Domain Controllers,DC=somebigcompany,DC=com\n" +
				"distinguishedName: OU=Domain Controllers,DC=somebigcompany,DC=com\n" +
				"\n";
		if (initialized) {
			runTest(filter, adFields, expected);
		}
	}

	private void runTest(String filter, String adFields, String expected) {
		try {
			StringWriter writer = new StringWriter();
			adFetchJob.fetchFromActiveDirectory(new BufferedWriter(writer), filter, adFields, 1);
			String actual = writer.getBuffer().toString();
			if (!expected.equals(actual)) {
				logger.debug("Diff found - \nexpected:\n{}\nactual: {}", expected, actual);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
