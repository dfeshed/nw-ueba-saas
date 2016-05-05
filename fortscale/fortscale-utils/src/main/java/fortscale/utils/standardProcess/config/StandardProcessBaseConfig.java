package fortscale.utils.standardProcess.config;

import fortscale.global.configuration.GlobalConfiguration;
import fortscale.utils.standardProcess.pidService.config.PidServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by baraks on 4/21/2016.
 */
@Configuration
@Import({GlobalConfiguration.class,PidServiceConfig.class})
public class StandardProcessBaseConfig {
//todo: add monitoring service

}
