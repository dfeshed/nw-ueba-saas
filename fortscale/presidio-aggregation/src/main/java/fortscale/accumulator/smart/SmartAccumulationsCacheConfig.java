package fortscale.accumulator.smart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmartAccumulationsCacheConfig {

    @Bean
    public SmartAccumulationsCache smartAccumulationsCache() {
        return new SmartAccumulationsInMemory();
    }

}
