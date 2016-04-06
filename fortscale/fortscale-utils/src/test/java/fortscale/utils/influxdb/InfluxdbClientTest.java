package fortscale.utils.influxdb;

import fortscale.utils.test.category.InfluxDBTestCategory;
import org.eclipse.jdt.internal.core.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/fortscale-common-context-test.xml"})
@Category(InfluxDBTestCategory.class) public class InfluxdbClientTest {

	@Autowired
	InfluxdbClient influxdbClient;

	@Test
	@Ignore
	public void shouldConnectToInfluxDB() {
		Assert.isTrue(influxdbClient.isInfluxDBStarted());
	}
}