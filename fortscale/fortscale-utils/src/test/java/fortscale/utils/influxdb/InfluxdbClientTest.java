package fortscale.utils.influxdb;

import fortscale.utils.influxdb.Exception.InfluxDBRuntimeException;
import fortscale.utils.influxdb.config.InfluxdbClientConfig;
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
@ContextConfiguration(classes=InfluxdbClientConfig.class)
@Category(InfluxDBTestCategory.class) public class InfluxdbClientTest {

	@Autowired
	InfluxdbClient influxdbClient;

	@Ignore
	@Test
	public void shouldConnectToInfluxDB() {
		try {
			Assert.isTrue(influxdbClient.isInfluxDBStarted());
		} catch (InfluxDBRuntimeException e) {
			e.printStackTrace();
		}
	}
}