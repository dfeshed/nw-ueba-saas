package presidio.ui.presidiouiapp;

import fortscale.spring.PresidioUiRemoteConfigurationClientConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(PresidioUiRemoteConfigurationClientConfiguration.class) //Mock config server with local data
public class PresidioUiAppApplicationTests {

    @Test
    public void contextLoads() {
    }

}
