package fortscale.monitoring.external.stats.collector.impl.linux.service;
/**
 * Created by gaashh on 6/6/16.
 */

import fortscale.monitoring.external.stats.collector.impl.linux.config.LinuxCollectorsServicesImplConfig;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.config.StandardStatsServiceConfig;
import fortscale.utils.process.hostnameService.config.HostnameServiceConfig;
import fortscale.utils.spring.TestPropertiesConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
// See https://spring.io/blog/2011/06/21/spring-3-1-m2-testing-with-configuration-classes-and-profiles
public class LinuxCollectorsServicesImplServicesTest {

    private static final Logger logger = Logger.getLogger(LinuxCollectorsServicesImplServicesTest.class);

    @Configuration
    @PropertySource("classpath:META-INF/fortscale-config.properties")
    @Import({LinuxCollectorsServicesImplConfig.class,
             StandardStatsServiceConfig.class,
             HostnameServiceConfig.class }) // required by stats

    static public class CollectorsSpringConfig {

        @Bean
        public static TestPropertiesConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();


            TestPropertiesConfigurer configurer = new TestPropertiesConfigurer(properties);

            return configurer;
        }
    }

    // @Test
    public void testLinuxCollectorsServicesImplServices() throws InterruptedException {
        // Sleep to allow collectors to work at background
        Thread.sleep(5 * 60 * 1000);

    }

    @Test // To make sure we have at least one test
    public void testDummy(){
        // NOP
    }

}