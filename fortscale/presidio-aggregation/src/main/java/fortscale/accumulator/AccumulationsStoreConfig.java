package fortscale.accumulator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;

import java.util.List;

@Configuration
public class AccumulationsStoreConfig {

    @Bean
    public AccumulationsStore accumulationsStore() {
        return new AccumulationsInMemory();
    }

}
