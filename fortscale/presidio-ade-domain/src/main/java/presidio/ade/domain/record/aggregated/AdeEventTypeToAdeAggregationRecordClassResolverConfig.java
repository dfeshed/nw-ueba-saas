package presidio.ade.domain.record.aggregated;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdeEventTypeToAdeAggregationRecordClassResolverConfig {
    @Bean
    public AdeEventTypeToAdeAggregationRecordClassResolver adeEventTypeToAdeAggregationRecordClassResolver()
    {
        return new AdeEventTypeToAdeAggregationRecordClassResolver(this.getClass().getPackage().getName());
    }
}
