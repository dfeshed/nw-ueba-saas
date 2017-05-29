package fortscale.utils.test.data.generator;

import fortscale.utils.time.SystemDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.jemos.podam.common.AttributeStrategy;
import uk.co.jemos.podam.typeManufacturers.AbstractTypeManufacturer;

/**
 * Created by barak_schuster on 5/28/17.
 */
@Configuration
public class InstantAttributeStrategyConfig {

    @Autowired
    private SystemDateService systemDateService;

    @Bean
    public AbstractTypeManufacturer instantAttributeStrategy() {
        return new InstantAttributeStrategy(systemDateService);
    }
}
