package presidio.collector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.collector.services.api.ReaderService;
import presidio.collector.spring.CollectorConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectorConfig.class)

public class FortscaleInputCoreApplicationTest {

	@Autowired
	ReaderService processService;

	@Test
	public void contextLoads() throws Exception {
		processService.run("SHAY");

	}

}
