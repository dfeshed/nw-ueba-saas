package fortscale.utils.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * This is a helper legacy configuration class. It enables spring component scan of only some (not all) directories under
 * fortscale.utils. This prevents scanning of newer packages under util the uses class based configuration.
 *
 * To use it, remove fortscale.utils from <context:component-scan > package list and add
 *   <bean id="UtilsLegacyComponentScanConfig" class="fortscale.utils.spring.config.UtilsLegacyComponentScanConfig" />
 *
 * Created by gaashh on 5/4/16.
 */


@Configuration
// DO NOT EXTEND THIS LIST
@ComponentScan( {
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
            "fortscale.utils.time" })

public class UtilsLegacyComponentScanConfig {
}




