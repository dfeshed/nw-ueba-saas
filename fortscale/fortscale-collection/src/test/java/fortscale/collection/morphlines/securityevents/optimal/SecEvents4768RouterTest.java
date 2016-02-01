package fortscale.collection.morphlines.securityevents.optimal;

import fortscale.collection.morphlines.MorphlinesTester;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEvents4768RouterTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/default/readSecEvtRouter.conf";

	
	
	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }
	
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
	        "Successful 4768 Event Computer",
	        "4768,2016-01-30T10:00:00,ga23418$,failure,10.33.48.78,password has expired,deltads.ent",
	    	"4768,true"
    		),
			$ (
			"Successful 4768 Event User",
			"4768,2016-01-30T10:00:00,ga23418,failure,10.33.48.78,password has expired,deltads.ent,ga23418",
			"4768,false"
			)
	        );
 	}


}
