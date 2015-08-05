package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class ISETest {

    private MorphlinesTester morphlineTester = new MorphlinesTester();
    private String confFile = "resources/conf-files/parseISE.conf";
    private String[] iseOutputFields = new String[]{"eventCode", "timestampepoch", "hostname", "ipaddress", "macAddress","adHostName"};



    // Add this notes only for debug usage
	/*
	@BeforeClass
	public static void setUpClass(){
		testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test.xml");

	}

	@AfterClass
	public static void finalizeTestClass(){
		testContextManager.close();
		testContextManager = null;
	}
	*/

    @SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }

    @Before
    public void setUp() throws Exception {
        morphlineTester.init(new String[]{confFile}, Arrays.asList(iseOutputFields));
    }

    @After
    public void tearDown() throws Exception {
        morphlineTester.close();
    }

    @Test
    @Parameters
    public void testIseSingleLines(String testCase, String inputLine, String expectedOutput) {
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }


    @SuppressWarnings("unused")
    private Object[] parametersForTestIseSingleLines() {
        return $(
                $(
                        "Regular 3000 event",
                        "2015-06-22T00:00:00.000+00:00,2015-06-22T00:00:00+02:00,3000,TEST-IDANP,10.141.40.81,88-43-e1-62-59-40",
                        "3000,1434924000,TEST-IDANP,10.141.40.81,88-43-e1-62-59-40,false"
                ),
                $(
                        "3000 event without IP",
                        "2015-06-22T00:00:00.000+00:00,2015-06-22T00:00:00+00:00,3000,TEST-IDANP,,88-43-e1-62-59-40",
                        null
                ),
                $(
                        "Regular 3001 event",
                        "2015-06-22T00:00:00.000+00:00,2015-06-22T00:00:00+02:00,3001,TEST-IDANP,10.141.40.81,88-43-e1-62-59-40",
                        "3001,1434924000,TEST-IDANP,10.141.40.81,88-43-e1-62-59-40,false"
                ),
                $(
                        "3001 event without IP",
                        "2015-06-22T00:00:00.000+00:00,2015-06-22T00:00:00+00:00,3001,TEST-IDANP,,88-43-e1-62-59-40",
                        null
                ),
                $(
                        "Event number 5200 - need to be dropped",
                        "2015-06-22T00:00:00.000+00:00,2015-06-22T00:00:00+00:00,5200,TEST-IDANP,10.141.40.81,88-43-e1-62-59-40",
                        null
                ),
                $(
                        "No event number - need to be dropped",
                        "2015-06-22T00:00:00.000+00:00,2015-06-22T00:00:00+00:00,,TEST-IDANP,10.141.40.81,88-43-e1-62-59-40",
                        null
                )
        );
    }

}
