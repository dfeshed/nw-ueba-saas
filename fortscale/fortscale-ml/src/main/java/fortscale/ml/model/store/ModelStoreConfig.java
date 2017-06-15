package fortscale.ml.model.store;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 6/6/17.
 */
@Configuration
public class ModelStoreConfig {
    @Bean
    public ModelStore modelStore()
    {
        return new ModelStore();
    }
}
