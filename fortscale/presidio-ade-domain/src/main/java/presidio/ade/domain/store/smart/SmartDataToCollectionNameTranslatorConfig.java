package presidio.ade.domain.store.smart;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmartDataToCollectionNameTranslatorConfig {

    @Bean
    public SmartDataToCollectionNameTranslator smartDataToCollectionNameTranslator() {
        return new SmartDataToCollectionNameTranslator();
    }
}
