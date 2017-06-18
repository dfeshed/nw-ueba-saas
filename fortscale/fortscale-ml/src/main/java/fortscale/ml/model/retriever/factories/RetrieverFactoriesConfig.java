package fortscale.ml.model.retriever.factories;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 3/26/17.
 */
@Configuration
@ComponentScan(basePackageClasses = {
        RetrieverFactoriesConfig.class // i.e. EntityEventValueRetrieverFactory
})
public class RetrieverFactoriesConfig {
}
