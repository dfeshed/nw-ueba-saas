package fortscale.utils.monitoring.stats.config;

import fortscale.utils.monitoring.stats.impl.StatsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Null stats service config is a bean of StatsService that always return null. It is designed to be used at unit tests
 * that autowire to stats service (but does not actually use it)
 *
 * Created by gaashh on 6/27/16.
 */

@Configuration
public class NullStatsServiceConfig {

    /**
     *
     * The main bean function, always return null the stats service bean
     *
     * @return always null
     */
    @Bean
    public StatsServiceImpl nullStatsService() {

        return null;

    }
}

