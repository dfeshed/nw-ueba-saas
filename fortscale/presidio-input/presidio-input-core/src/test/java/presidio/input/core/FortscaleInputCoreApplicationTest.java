package presidio.input.core;


import fortscale.common.shell.PresidioExecutionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.core.spring.InputCoreConfiguration;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = InputCoreConfiguration.class)
public class FortscaleInputCoreApplicationTest {

    @Autowired
    private PresidioExecutionService processService;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertTrue(processService instanceof InputExecutionServiceImpl);
    }
}
