package presidio.ade.domain.store.translators;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 5/21/17.
 */
@Configuration
public class ADEInputDataToCollectionNameTranslatorConfig {

    @Bean
    public ADEInputDataToCollectionNameTranslator adeInputDataToCollectionNameTranslator()
    {
        return new ADEInputDataToCollectionNameTranslator();
    }
}
