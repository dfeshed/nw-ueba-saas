package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSA4768Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_securityAnalytics.conf";
	private String conf4768File = "resources/conf-files/processSecEvtSA4768.conf";

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.security.events.login.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		List<String> splunkSecEventsOutputFieldsExcludingEnrichment = new ArrayList<>();
		for(String field: splunkSecEventsOutputFields){
			if(!field.equals("machine_name")){
				splunkSecEventsOutputFieldsExcludingEnrichment.add(field);
			}
		}
		morphlineTester.init(new String[] { confFile, conf4768File }, splunkSecEventsOutputFieldsExcludingEnrichment);
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
        	"Successful 4768 Event",
        	"May  1 10:14:31 roee-hd5 %NICWIN-4-Security_4768_Microsoft-Windows-Security-Auditing: Security,rn=462908116 cid=6992 eid=560,Mon Mar 17 13:48:37 2014,4768,Microsoft-Windows-Security-Auditing,,Audit Success,BG-DC2.bg.playtech.corp,Kerberos Authentication Service,,A Kerberos authentication ticket (TGT) was requested.  Account Information:  Account Name:  deannek  Supplied Realm Name: bg  User ID:   S-1-5-21-3421828858-1269048617-336047487-1435  Service Information:  Service Name:  krbtgt  Service ID:  S-1-5-21-3421828858-1269048617-336047487-502  Network Information:  Client Address:  ::ffff:192.168.203.147  Client Port:  50670  Additional Information:  Ticket Options:  0x40810010  Result Code:  0x0  Ticket Encryption Type: 0x12  Pre-Authentication Type: 2  Certificate Information:  Certificate Issuer Name:    Certificate Serial Number:   Certificate Thumbprint:    Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
        	"2014-03-17T13:48:37.000+02:00,2014-03-17 13:48:37,1395056917,deannek,bg,S-1-5-21-3421828858-1269048617-336047487-1435,4768,192.168.203.147,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,"
        	)
        	/*
    		$ (
	        "Successfull 4768 Event Type B",
	        "Mar 17 15:49:28 IL-DC2 microsoft-windows-security-auditing[failure] 4768 A Kerberos authentication ticket (TGT) was requested.#177#177Account Information:#177Account Name:alon_b#177Supplied Realm Name:IL#177User ID:S-1-0-0#177#177Service Information:#177Service Name:krbtgt/IL#177Service ID:S-1-0-0#177#177Network Information:#177Client Address:#177::ffff:192.168.158.38#177Client Port:51496#177#177Additional Information:#177Ticket Options:0x40810010#177Result Code:0x6#177Ticket Encryption Type:0xffffffff#177Pre-Authentication Type:-#177#177Certificate Information:#177Certificate Issuer Name:#177Certificate Serial Number:#177Certificate Thumbprint:#177#177Certificate information is only provided if a certificate#177was used for pre-authentication.#177#177Pre-authentication types, ticket options, encryption types#177and result codes are defined in RFC 4120. ",
	    	"2014-03-17T15:49:28.000+02:00,2014-03-17 15:49:28,1395064168,alon_b,IL,S-1-0-0,4768,192.168.158.38,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,"
    		)

    		$ (
	        "Successfull 4768 Event over NAT",
	        "2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 	Account Name:		tomerl 	Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.0.22 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
	    	"2014-03-21T23:24:58.000+02:00,2014-03-21 23:24:58,1395437098,tomerl,FORTSCALE.DOM,FORTSCALE\\tomerl,4768,192.168.0.22,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,true,,"
    		),
    		$ (
	        "Failure 4768 Event",
	        "2014-03-19T11:03:34.000+02:00|03/19/2014 11:03:34 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4768	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248286113	Keywords=Audit Failure	Message=A Kerberos authentication ticket (TGT) was requested.		Account Information:		Account Name:		roees		Supplied Realm Name:	FORTSCALE		User ID:			NULL SID		Service Information:		Service Name:		krbtgt/FORTSCALE		Service ID:		NULL SID		Network Information:		Client Address:		::ffff:192.168.0.31		Client Port:		52813		Additional Information:		Ticket Options:		0x40810010		Result Code:		0x17		Ticket Encryption Type:	0xffffffff		Pre-Authentication Type:	-		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number:			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
	    	"2014-03-19T11:03:34.000+02:00,2014-03-19 11:03:34,1395219814,roees,FORTSCALE,NULL SID,4768,192.168.0.31,FAILURE,0x17,-,0x40810010,True,False,False,False,False,False,false,,"
    		),
    		$ (
	        "Event 4768 with no user name (Should be dropped)",
	        "2014-03-21T23:24:58.000+02:00|03/21/2014 03:01:29 AM LogName=Security SourceName=Microsoft Windows security auditing. EventCode=4768 EventType=0 Type=Information ComputerName=Fs-DC-01.Fortscale.dom TaskCategory=Kerberos Authentication Service OpCode=Info RecordNumber=249225174 Keywords=Audit Success Message=A Kerberos authentication ticket (TGT) was requested.  Account Information: 		Supplied Realm Name:	FORTSCALE.DOM 	User ID:			FORTSCALE\tomerl  Service Information: 	Service Name:		krbtgt 	Service ID:		FORTSCALE\\krbtgt  Network Information: 	Client Address:		::ffff:192.168.100.157 	Client Port:		50129  Additional Information: 	Ticket Options:		0x40810010 	Result Code:		0x0 	Ticket Encryption Type:	0x12 	Pre-Authentication Type:	2  Certificate Information: 	Certificate Issuer Name:		 	Certificate Serial Number:	 	Certificate Thumbprint:		  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
	    	null
    		),
    		$ (
    		"Regular 4768 Event",
    		"2014-02-24T13:38:40.000+02:00|02/24/2014 01:38:40 PM    LogName=Security        SourceName=Microsoft Windows security auditing. EventCode=4768  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Authentication Service    OpCode=Info     RecordNumber=229771360  Keywords=Audit Success  Message=A Kerberos authentication ticket (TGT) was requested.           Account Information:            Account Name:           maxk            Supplied Realm Name:    FORTSCALE.DOM           User ID:                        FORTSCALEaxk            Service Information:            Service Name:          krbtgt          Service ID:             FORTSCALErbtgt          Network Information:            Client Address:         ::ffff:192.168.0.107            Client Port:            45665          Additional Information:         Ticket Options:         0x10            Result Code:            0x0             Ticket Encryption Type: 0x12            Pre-Authentication Type:       2               Certificate Information:                Certificate Issuer Name:                                Certificate Serial Number:                      Certificate Thumbprint:                        Certificate information is only provided if a certificate was used for pre-authentication.              Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
    		"2014-02-24T13:38:40.000+02:00,2014-02-24 13:38:40,1393241920,maxk,FORTSCALE.DOM,FORTSCALEaxk,4768,192.168.0.107,SUCCESS,0x0,2,0x10,False,False,False,False,False,False,false,,"
    		),
    		$ (
    		"4768 Event with computer as account name (Should be dropped)",
    		"2014-02-24T13:38:40.000+02:00|02/24/2014 01:38:40 PM    LogName=Security        SourceName=Microsoft Windows security auditing. EventCode=4768  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Authentication Service    OpCode=Info     RecordNumber=229771360  Keywords=Audit Success  Message=A Kerberos authentication ticket (TGT) was requested.           Account Information:            Account Name:           maxk$            Supplied Realm Name:    FORTSCALE.DOM           User ID:                        FORTSCALEaxk            Service Information:            Service Name:          krbtgt          Service ID:             FORTSCALErbtgt          Network Information:            Client Address:         ::ffff:192.168.0.107            Client Port:            45665          Additional Information:         Ticket Options:         0x10            Result Code:            0x0             Ticket Encryption Type: 0x12            Pre-Authentication Type:       2               Certificate Information:                Certificate Issuer Name:                                Certificate Serial Number:                      Certificate Thumbprint:                        Certificate information is only provided if a certificate was used for pre-authentication.              Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
    		null
    		)
    		*/
        );
 	}

}
