package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.gaussian.ContinuousHistogramModelBuilderFactory;
import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 3/26/17.
 */
@Configuration
@ComponentScan(basePackageClasses = {
        BuilderFactoriesConfig.class,
        GaussianPriorModelBuilderFactory.class,
        ContinuousHistogramModelBuilderFactory.class
})
public class BuilderFactoriesConfig {
}
