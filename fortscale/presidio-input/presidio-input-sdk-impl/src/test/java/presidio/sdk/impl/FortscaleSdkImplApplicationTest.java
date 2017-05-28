package presidio.sdk.impl;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.sdk.impl.spring.InputSdkConfig;
import presidio.sdk.api.services.PresidioInputSdk;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InputSdkConfig.class)
public class FortscaleSdkImplApplicationTest {

	@Autowired
	PresidioInputSdk presidioInputSdk;

	@Test
	public void contextLoads() throws Exception {

		Assert.assertNotNull(presidioInputSdk);

	}

}
