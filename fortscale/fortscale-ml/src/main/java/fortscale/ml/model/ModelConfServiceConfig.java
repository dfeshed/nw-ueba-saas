package fortscale.ml.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 3/26/17.
 */
@Configuration
public class ModelConfServiceConfig {
    @Bean
    public ModelConfService modelConfService() {
        return new ModelConfService();
    }
}
