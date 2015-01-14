package fortscale.streaming;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.utils.test.category.HadoopTestCategory;
import fortscale.utils.test.category.IntegrationTestCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/streaming-context.xml"})
@Category(HadoopTestCategory.class)
public class StreamingContextTestInt {

	
	@Test
	@Category(IntegrationTestCategory.class)
	public void testContext(){
	}
}
