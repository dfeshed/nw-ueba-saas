package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

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
public class Security4771EventsTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_splunk.conf";
	private String conf4771File = "resources/conf-files/processSecEvt4771.conf";

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.security.events.login.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[] { confFile, conf4771File }, splunkSecEventsOutputFields);
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
	        "4771 Event with all neccessary fields",
	        "2014-03-19T10:38:53.000+02:00|03/19/2014 10:38:53 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4771	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248270818	Keywords=Audit Failure	Message=Kerberos pre-authentication failed.		Account Information:		Security ID:		FORTSCALEaxk		Account Name:		maxk		Service Information:		Service Name:		krbtgt/FORTSCALE.DOM		Network Information:		Client Address:		::ffff:192.168.0.107		Client Port:		55612		Additional Information:		Ticket Options:		0x40800000		Failure Code:		0x18		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number: 			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options and failure codes are defined in RFC 4120.		If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.",
	    	"2014-03-19T10:38:53.000+02:00,2014-03-19 10:38:53,1395218333,maxk,,FORTSCALEaxk,4771,192.168.0.107,maxbox,FAILURE,0x18,2,0x40800000,True,False,False,False,False,False,false"
    		),
    		$ (
	        "4771 Event with all neccessary fields over NAT",
	        "2014-03-19T10:38:53.000+02:00|03/19/2014 10:38:53 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4771	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248270818	Keywords=Audit Failure	Message=Kerberos pre-authentication failed.		Account Information:		Security ID:		FORTSCALEaxk		Account Name:		maxk		Service Information:		Service Name:		krbtgt/FORTSCALE.DOM		Network Information:		Client Address:		::ffff:192.168.0.22		Client Port:		55612		Additional Information:		Ticket Options:		0x40800000		Failure Code:		0x18		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number: 			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options and failure codes are defined in RFC 4120.		If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.",
	    	"2014-03-19T10:38:53.000+02:00,2014-03-19 10:38:53,1395218333,maxk,,FORTSCALEaxk,4771,192.168.0.22,openvpnas,FAILURE,0x18,2,0x40800000,True,False,False,False,False,False,true"
    		),
    		$ (
	        "4771 Event with computer as account name (Should be dropped)",
	        "2014-03-19T10:38:53.000+02:00|03/19/2014 10:38:53 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4771	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248270818	Keywords=Audit Failure	Message=Kerberos pre-authentication failed.		Account Information:		Security ID:		FORTSCALEaxk		Account Name:		maxk$		Service Information:		Service Name:		krbtgt/FORTSCALE.DOM		Network Information:		Client Address:		::ffff:192.168.0.22		Client Port:		55612		Additional Information:		Ticket Options:		0x40800000		Failure Code:		0x18		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number: 			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options and failure codes are defined in RFC 4120.		If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.",
	    	null
    		),
    		$ (
	        "Event 4771 with no user name (Should be dropped)",
	        "2014-03-19T10:38:53.000+02:00|03/19/2014 10:38:53 AM	LogName=Security	SourceName=Microsoft Windows security auditing.	EventCode=4771	EventType=0	Type=Information	ComputerName=Fs-DC-01.Fortscale.dom	TaskCategory=Kerberos Authentication Service	OpCode=Info	RecordNumber=248270818	Keywords=Audit Failure	Message=Kerberos pre-authentication failed.		Account Information:		Security ID:		FORTSCALEaxk		Service Information:		Service Name:		krbtgt/FORTSCALE.DOM		Network Information:		Client Address:		::ffff:192.168.0.107		Client Port:		55612		Additional Information:		Ticket Options:		0x40800000		Failure Code:		0x18		Pre-Authentication Type:	2		Certificate Information:		Certificate Issuer Name:				Certificate Serial Number: 			Certificate Thumbprint:				Certificate information is only provided if a certificate was used for pre-authentication.		Pre-authentication types, ticket options and failure codes are defined in RFC 4120.		If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.	",
	    	null
    		)
        );
 	}

}
