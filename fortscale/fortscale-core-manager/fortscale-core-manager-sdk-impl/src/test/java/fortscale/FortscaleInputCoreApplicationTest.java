package fortscale;

import fortscale.services.impl.CoreManagerServiceImpl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest

public class FortscaleInputCoreApplicationTest {

	@Autowired
    CoreManagerServiceImpl service;

	@Test
	public void contextLoads() throws Exception {
		Assert.assertNotNull(service);

	}

}
