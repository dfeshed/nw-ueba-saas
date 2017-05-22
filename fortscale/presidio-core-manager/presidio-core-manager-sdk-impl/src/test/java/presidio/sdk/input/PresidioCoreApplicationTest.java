package presidio.sdk.input;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.impl.services.CoreManagerServiceImpl;
import presidio.sdk.impl.spring.CoreManagerSdkImplConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreManagerSdkImplConfig.class)

public class PresidioCoreApplicationTest {

//	@Autowired
//	CoreManagerServiceImpl service;

	@Test
	public void contextLoads() throws Exception {
		//Assert.assertNotNull(service);

	}

}
