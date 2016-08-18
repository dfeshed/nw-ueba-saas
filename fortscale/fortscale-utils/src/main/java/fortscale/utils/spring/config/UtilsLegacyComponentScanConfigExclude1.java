package fortscale.utils.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 *
 * Similar to UtilsLegacyComponentScanConfig - see its documentation. In addition to it, it excludes some class
 * per collection-context-test-light-local-timezone.xml and collection-context-test-mocks.xml needs
 *
 * To use it, remove fortscale.utils from <context:component-scan > package list and add
 *   <bean id="UtilsLegacyComponentScanConfigExclude1" class="fortscale.utils.spring.config.UtilsLegacyComponentScanConfigExclude1" />
 *
 * Created by gaashh on 5/4/16.
 */


@Configuration
// DO NOT EXTEND THIS LIST
@ComponentScan( basePackages = {
        "fortscale.utils.actdir",
        "fortscale.utils.cleanup",
        "fortscale.utils.factory",
        "fortscale.utils.hdfs",
        "fortscale.utils.image",
        "fortscale.utils.impala",
        "fortscale.utils.jade",
        "fortscale.utils.jonfig",
        "fortscale.utils.json",
        "fortscale.utils.junit",
        "fortscale.utils.kafka",
        "fortscale.utils.logging",
        "fortscale.utils.mongodb",
        "fortscale.utils.pool",
        "fortscale.utils.prettifiers",
        "fortscale.utils.properties",
        "fortscale.utils.pxGrid",
        "fortscale.utils.qradar",
        "fortscale.utils.servlet",
        "fortscale.utils.splunk",
        "fortscale.utils.syslog",
        "fortscale.utils.test",
        "fortscale.utils.time" },

        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
                                               pattern = "fortscale.utils.mongodb.config.SpringMongoConfiguration")


)

public class UtilsLegacyComponentScanConfigExclude1 {
}


