package presidio.input.core;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.api.InputExecutionService;
import presidio.input.core.spring.InputCoreConfiguration;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = InputCoreConfiguration.class)
@Ignore

public class FortscaleInputCoreApplicationTest {

    @Autowired
    InputExecutionService processService;
    @Test
    public void contextLoads() throws Exception {
        processService.run();

    }

}
