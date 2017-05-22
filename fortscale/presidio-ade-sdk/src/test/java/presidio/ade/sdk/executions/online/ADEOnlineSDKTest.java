package presidio.ade.sdk.executions.online;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;

/**
 * Created by barak_schuster on 5/21/17.
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ADEOnlineSDKTest {
    @Configuration
    @Import({
            MongodbTestConfig.class,
            ADEOnlineSDKConfig.class
    })
    public static class springConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer ADEOnlineSDKTestPropertiesConfigurer() {
            Properties properties = new Properties();
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }

    @Autowired
    private ADEOnlineSDK adeOnlineSDK;
    @Test
    public void test()
    {
        adeOnlineSDK.getRunId();
        System.out.println();
    }
}