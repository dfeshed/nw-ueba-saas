package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.Arrays;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSplunk4624RouterTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvtRouter_splunk.conf";

	
	@Before
	public void setUp() throws Exception {
		List<String> fieldsToCheck = Arrays.asList("eventCode","isComputer");
		morphlineTester.init(new String[] { confFile}, fieldsToCheck);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}
	
	@Test
	@Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}


	@SuppressWarnings("unused")
 	private Object[] parametersForTest() {
        return	$(
        		$ (
		        "Successfull 4624 computer Event", 
		    	"2014-08-06T07:19:59.000+03:00|08/06/2014 07:19:59 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4624	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Logon	OpCode=Info	RecordNumber=269497583	Keywords=Audit Success	Message=An account was successfully logged on.		Subject:		Security ID:		NULL SID		Account Name:		-		Account Domain:		-		Logon ID:		0x3e7		Logon Type:			3		New Logon:		Security ID:		FORTSCALE\\ROND-PC$		Account Name:		ROND-PC$		Account Domain:		FORTSCALE		Logon ID:		0x309d1ac6		Logon GUID:		{00000000-0000-0000-0000-000000000000}		Process Information:		Process ID:		0x1f4		Process Name:		C:\\Windows\\System32\\lsass.exe		Network Information:		Workstation Name:	FS-DC-01		Source Network Address:	192.168.0.174		Source Port:		36464		Detailed Authentication Information:		Logon Process:		Kerberos  		Authentication Package:	Kerberos		Transited Services:	-		Package Name (NTLM only):	-		Key Length:		0		This event is generated when a logon session is created. It is generated on the computer that was accessed.		The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.		The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).		The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.		The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.		The authentication information fields provide detailed information about this specific logon request.		- Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.		- Transited services indicate which intermediate services have participated in this logon request.		- Package name indicates which sub-protocol was used among the NTLM protocols.		- Key length indicates the length of the generated session key. This will be 0 if no session key was requested.	",
		    	"4624,true"
        		),
				$ (
						"Successfull 4624 Event - with WAN enrichment",
						"2014-08-06T07:19:59.000+03:00|08/06/2014 07:19:59 AM\tLogName=Security\tSourceName=Microsoft Windows security auditing.\tEventCode=4624\tEventType=0\tType=Information\tComputerName=Fs-DC-01.Fortscale.dom\tTaskCategory=Logon\tOpCode=Info\tRecordNumber=269497583\tKeywords=Audit Success\tMessage=An account was successfully logged on.\t\tSubject:\t\tSecurity ID:\t\tNULL SID\t\tAccount Name:\t\t-\t\tAccount Domain:\t\t-\t\tLogon ID:\t\t0x3e7\t\tLogon Type:\t\t\t3\t\tNew Logon:\t\tSecurity ID:\t\tFORTSCALE\\ROND-PC$\t\tAccount Name:\t\tROND-PC$\t\tAccount Domain:\t\tFORTSCALE\t\tLogon ID:\t\t0x309d1ac6\t\tLogon GUID:\t\t{00000000-0000-0000-0000-000000000000}\t\tProcess Information:\t\tProcess ID:\t\t0x1f4\t\tProcess Name:\t\tC:\\Windows\\System32\\lsass.exe\t\tNetwork Information:\t\tWorkstation Name:\tFS-DC-01\t\tSource Network Address:\t192.168.0.174\t\tSource Port:\t\t36464\t\tDetailed Authentication Information:\t\tLogon Process:\t\tKerberos  \t\tAuthentication Package:\tKerberos\t\tTransited Services:\t-\t\tPackage Name (NTLM only):\t-\t\tKey Length:\t\t0\t\tThis event is generated when a logon session is created. It is generated on the computer that was accessed.\t\tThe subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.\t\tThe logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).\t\tThe New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.\t\tThe network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.\t\tThe authentication information fields provide detailed information about this specific logon request.\t\t- Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.\t\t- Transited services indicate which intermediate services have participated in this logon request.\t\t- Package name indicates which sub-protocol was used among the NTLM protocols.\t\t- Key length indicates the length of the generated session key. This will be 0 if no session key was requested. timezone Asia/Jerusalem datacenter Israel",
						"4624,true"
				),
        		$ (
		        "Successfull 4624 user Event", 
		    	"2014-08-06T07:19:59.000+03:00|08/06/2014 07:19:59 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4624	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Logon	OpCode=Info	RecordNumber=269497583	Keywords=Audit Success	Message=An account was successfully logged on.		Subject:		Security ID:		NULL SID		Account Name:		-		Account Domain:		-		Logon ID:		0x3e7		Logon Type:			3		New Logon:		Security ID:		FORTSCALE\\ROND-PC		Account Name:		ROND-PC		Account Domain:		FORTSCALE		Logon ID:		0x309d1ac6		Logon GUID:		{00000000-0000-0000-0000-000000000000}		Process Information:		Process ID:		0x1f4		Process Name:		C:\\Windows\\System32\\lsass.exe		Network Information:		Workstation Name:	FS-DC-01		Source Network Address:	192.168.0.174		Source Port:		36464		Detailed Authentication Information:		Logon Process:		Kerberos  		Authentication Package:	Kerberos		Transited Services:	-		Package Name (NTLM only):	-		Key Length:		0		This event is generated when a logon session is created. It is generated on the computer that was accessed.		The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.		The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).		The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.		The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.		The authentication information fields provide detailed information about this specific logon request.		- Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.		- Transited services indicate which intermediate services have participated in this logon request.		- Package name indicates which sub-protocol was used among the NTLM protocols.		- Key length indicates the length of the generated session key. This will be 0 if no session key was requested.	",
		    	"4624,false"
        		),
        		$ (
		        "Account name value is \"-\", should be dropped", 
		    	"2014-08-06T07:19:59.000+03:00|08/06/2014 07:19:59 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4624	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Logon	OpCode=Info	RecordNumber=269497583	Keywords=Audit Success	Message=An account was successfully logged on.		Subject:		Security ID:		NULL SID		Account Name:		-		Account Domain:		-		Logon ID:		0x3e7		Logon Type:			3		New Logon:		Security ID:		FORTSCALE\\ROND-PC		Account Name:		-		Account Domain:		FORTSCALE		Logon ID:		0x309d1ac6		Logon GUID:		{00000000-0000-0000-0000-000000000000}		Process Information:		Process ID:		0x1f4		Process Name:		C:\\Windows\\System32\\lsass.exe		Network Information:		Workstation Name:	FS-DC-01		Source Network Address:	192.168.0.174		Source Port:		36464		Detailed Authentication Information:		Logon Process:		Kerberos  		Authentication Package:	Kerberos		Transited Services:	-		Package Name (NTLM only):	-		Key Length:		0		This event is generated when a logon session is created. It is generated on the computer that was accessed.		The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.		The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).		The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.		The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.		The authentication information fields provide detailed information about this specific logon request.		- Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.		- Transited services indicate which intermediate services have participated in this logon request.		- Package name indicates which sub-protocol was used among the NTLM protocols.		- Key length indicates the length of the generated session key. This will be 0 if no session key was requested.	",
		    	null
        		)
        		   		
        );
 	}


}
