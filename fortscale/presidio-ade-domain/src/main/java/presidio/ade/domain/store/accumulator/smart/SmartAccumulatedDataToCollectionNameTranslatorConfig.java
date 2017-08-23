package presidio.ade.domain.store.accumulator.smart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmartAccumulatedDataToCollectionNameTranslatorConfig {
    @Bean
    public SmartAccumulatedDataToCollectionNameTranslator smartAccumulatedDataToCollectionNameTranslator() {
        return new SmartAccumulatedDataToCollectionNameTranslator();
    }
}
