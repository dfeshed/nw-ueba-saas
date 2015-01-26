package fortscale.streaming.scorer;

import static org.mockito.Mockito.mock;
import net.minidev.json.JSONObject;

import org.apache.samza.config.Config;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/scorers-context-test.xml" })
public class ScorerBaseTest {

	protected Config config;
	
	@Autowired
	protected ScorerFactoryService scorerFactoryService;
	
	@Before
    public void setUp() {
        config = mock(Config.class);
    }

	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}
	
	protected EventMessage buildEventMessage(boolean isAddInput, String fieldName, String fieldValue){
		JSONObject jsonObject = null;
		if(isAddInput){
			jsonObject = new JSONObject();
			jsonObject.put(fieldName, fieldValue);
		}
		return new EventMessage(jsonObject);
	}
	
	protected void addToEventMessage(EventMessage eventMessage, String fieldName, String fieldValue){
		JSONObject jsonObject = eventMessage.getJsonObject();
		jsonObject.put(fieldName, fieldValue);
	}
}
