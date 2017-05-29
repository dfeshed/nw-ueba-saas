package presidio.sdk.input;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.sdk.api.services.CoreManagerService;
import presidio.sdk.api.services.PresidioInputPersistencyService;
import presidio.sdk.impl.spring.CoreManagerServiceConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreManagerServiceConfig.class)

public class PresidioCoreApplicationTest {

    @Autowired
    private CoreManagerService service;
    
    @Autowired
    private PresidioInputPersistencyService presidioInput;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull(service);
        Assert.assertNotNull(presidioInput);

    }

}
