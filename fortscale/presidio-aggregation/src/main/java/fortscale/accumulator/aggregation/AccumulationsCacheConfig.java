package fortscale.accumulator.aggregation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccumulationsCacheConfig {

    @Bean
    public AccumulationsCache accumulationsCache() {
        return new AccumulationsInMemory();
    }

}
