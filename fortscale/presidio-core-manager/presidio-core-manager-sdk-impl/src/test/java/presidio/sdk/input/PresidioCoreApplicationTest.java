package presidio.sdk.input;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.services.PresidioInputSdk;
import presidio.sdk.impl.services.CoreManagerSdk;
import presidio.sdk.impl.spring.CoreManagerSdkConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreManagerSdkConfig.class)

public class PresidioCoreApplicationTest {

    @Autowired
    private CoreManagerSdk service;


    @Autowired
    private PresidioInputSdk presidioInput;
    ;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull(service);
        Assert.assertNotNull(presidioInput);

    }

}
