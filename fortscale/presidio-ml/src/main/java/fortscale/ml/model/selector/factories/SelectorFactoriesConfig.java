package fortscale.ml.model.selector.factories;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 3/26/17.
 */
@Configuration
@ComponentScan(basePackageClasses = {
        SelectorFactoriesConfig.class // i.e. EntityEventContextSelectorFactory
})
public class SelectorFactoriesConfig {
}
