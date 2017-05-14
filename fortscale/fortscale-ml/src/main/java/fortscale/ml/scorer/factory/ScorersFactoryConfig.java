package fortscale.ml.scorer.factory;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 3/26/17.
 */
@Configuration
@ComponentScan(basePackageClasses = {
        ScorersFactoryConfig.class // i.e. CategoryRarityModelScorerFactory
})
public class ScorersFactoryConfig {
}
