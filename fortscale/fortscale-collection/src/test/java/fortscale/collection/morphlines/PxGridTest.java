package fortscale.collection.morphlines;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class PxGridTest {

    private MorphlinesTester morphlineTester = new MorphlinesTester();
    private String confFile = "resources/conf-files/parsePxGrid.conf";
    private String[] iseOutputFields = new String[]{"event_time", "hostname", "ipaddress","adHostName"};

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
                        "Regular event",
                        "Mon Dec 14 14:30:12 IST 2015,192.168.0.81,zehavitv",
                        "1450096212,zehavitv,192.168.0.81,false"
                )
        );
    }

}
