package fortscale.web.spring;

import fortscale.utils.spring.SpringPropertiesUtil;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;


/**
 * Created by rans on 25/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/override-properties-test.xml"})
public class SpringPropertyUtilTest {

    private static final String EVIDENCE_MESSAGE = "fortscale.message.evidence.";

    @Value("${fortscale.message.evidence.event_time}")
    private String eventTimeByValue;
    @Value("${fortscale.message.evidence.multiple_failed_authentications}")
    private String srcMachinesByValue;

    /**
     * Reads the same property from both @Value and from getProperty
     * See that the results are identical
     */
    @Test
    public void readPropertiesTest(){

        String eventTimeByPropertyPlaceHolder = SpringPropertiesUtil.getProperty("fortscale.message.evidence.event_time");
        String srcMachineByPropertyPlaceHolder = SpringPropertiesUtil.getProperty("fortscale.message.evidence.multiple_failed_authentications");

        assertEquals("messages from @value and from direct getProperties are not identical", eventTimeByValue, eventTimeByPropertyPlaceHolder);
        assertEquals("messages from @value and from direct getProperties are not identical", srcMachinesByValue, srcMachineByPropertyPlaceHolder);

    }
}
