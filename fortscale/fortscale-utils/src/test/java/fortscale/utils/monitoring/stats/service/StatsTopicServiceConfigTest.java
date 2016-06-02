package fortscale.utils.monitoring.stats.service;

import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.config.StandardStatsServiceConfig;
import fortscale.utils.process.hostnameService.config.HostnameServiceConfig;
import fortscale.utils.spring.MainProcessPropertiesConfigurer;
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
 * This test just loads the topic stats service context to make sure is resolves properly
 *
 * Created by gaashh on 5/31/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
// See https://spring.io/blog/2011/06/21/spring-3-1-m2-testing-with-configuration-classes-and-profiles
public class StatsTopicServiceConfigTest {

    @Configuration
    @PropertySource("classpath:META-INF/fortscale-config.properties")
    @Import( {StandardStatsServiceConfig.class, HostnameServiceConfig.class} )
    static public class StatSpringConfig {

        @Bean
        public static MainProcessPropertiesConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            MainProcessPropertiesConfigurer configurer = new MainProcessPropertiesConfigurer(properties);

            return configurer;
        }
    }

    @Autowired
    StatsService statsService;

    @Test
    public void testStatsTopicServiceConfig() {

        Assert.assertNotNull(statsService);

    }

}

