package fortscale.utils.monitoring.stats.service;

import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.config.StandardStatsServiceConfig;
import fortscale.utils.process.hostnameService.config.HostnameServiceConfig;
import fortscale.utils.spring.TestPropertiesConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Properties;

/**
 *
 * This test just loads the Samza stats service context to make sure is resolves properly
 *
 * Created by gaashh on 5/31/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
// See https://spring.io/blog/2011/06/21/spring-3-1-m2-testing-with-configuration-classes-and-profiles
public class StatsSamzaServiceConfigTest {

    @Configuration
    @PropertySource("classpath:META-INF/fortscale-config.properties")
    @Import( {StandardStatsServiceConfig.class, HostnameServiceConfig.class } )
    static public class StatSpringConfig {

        @Bean
        public static TestPropertiesConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            TestPropertiesConfigurer configurer = new TestPropertiesConfigurer(properties);

            return configurer;
        }
    }

    @Autowired
    StatsService statsService;

    @Test
    public void testStatsSamzaServiceConfig() {

        Assert.assertNotNull(statsService);

    }

}

