package fortscale.ml.scorer.spring.config;

import fortscale.ml.model.ModelConfServiceConfig;
import fortscale.ml.model.retriever.factories.RetrieverFactoriesConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 6/15/17.
 */
@Configuration
@ComponentScan("fortscale.ml.scorer.factory")
@Import({ScorerFactoryServiceConfig.class,RetrieverFactoriesConfig.class, ModelConfServiceConfig.class})
public class ScorersFactoriesConfig {

}
