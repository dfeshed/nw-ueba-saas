package presidio.ui.presidiouiapp;

import fortscale.presidio.remote.conf.spring.PresidioUiRemoteConfigurationClientMockConfiguration;
import fortscale.spring.PresidioUiRemoteConfigurationClientConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(PresidioUiRemoteConfigurationClientMockConfiguration.class) //Mock config server with local data
public class PresidioUiAppApplicationTests {

    @Test
    public void contextLoads() {
    }

}
