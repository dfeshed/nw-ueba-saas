package fortscale.collection.jobs.accumulator;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.configuration.CollectionPropertiesResolver;
import fortscale.collection.services.CollectionStatsMetricsService;
import fortscale.monitor.JobProgressReporter;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by barak_schuster on 10/18/16.
 */
@Configuration
@Profile("test")
public class FortscaleJobMockedTestSpringConfig {

    @Bean
    public JobProgressReporter jobProgressReporter()
    {
        return Mockito.mock(JobProgressReporter.class);
    }

    @Bean
    public CollectionStatsMetricsService collectionStatsMetricsService()
    {
        return Mockito.mock(CollectionStatsMetricsService.class);
    }

    @Bean
    public CollectionPropertiesResolver collectionPropertiesResolver()
    {
        return Mockito.mock(CollectionPropertiesResolver.class);
    }

    @Bean
    public JobDataMapExtension jobDataMapExtension()
    {
        return Mockito.mock(JobDataMapExtension.class);
    }
}
