package fortscale.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

/**
 *
 * This is a helper legacy configuration class. It replaces springs component-scan of all fortscale class tree.
 *
 * It maintain the component-scan of all (used) forscale modules while scaning only the legacy utils packages.
 *
 * This class should be used only in web spring context files
 *
 * To use it, remove fortscale.utils from <context:component-scan > package list and add
 *   <bean id="fortscaleLegacyWebComponentScanConfig" class="fortscale.web.config.FortscaleLegacyWebComponentScanConfig" />
 *
 * Created by gaashh on 5/4/16.
 */


@Configuration
// DO NOT EXTEND THIS LIST
@ComponentScan( basePackages = { "fortscale.aggregation", "fortscale.common", "fortscale.domain",
                                 "fortscale.global.configuration", "fortscale.monitor", "fortscale.services" },
                excludeFilters = { @ComponentScan.Filter(org.springframework.stereotype.Controller.class),
                                   @ComponentScan.Filter(type = FilterType.REGEX,  pattern = "fortscale\\.services\\.ipresolving\\..*" )}
)

// fortscale.utils (legacy)
@Import(fortscale.utils.spring.config.UtilsLegacyComponentScanConfig.class)

public class FortscaleLegacyWebComponentScanConfig {
}




