package fortscale.streaming;


import fortscale.utils.test.category.HadoopTestCategory;
import fortscale.utils.test.category.IntegrationTestCategory;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/streaming-AlertGeneratorTask-context.xml"})
@Category(HadoopTestCategory.class)
public class StreamingAlertGeneratorTaskContextTestInt {

	
	@Test
	@Category(IntegrationTestCategory.class)
	public void testContext(){
	}
}
