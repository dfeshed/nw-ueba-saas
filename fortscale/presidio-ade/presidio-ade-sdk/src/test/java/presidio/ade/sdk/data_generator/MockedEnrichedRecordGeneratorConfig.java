package presidio.ade.sdk.data_generator;

import fortscale.utils.test.data.generator.InstantAttributeStrategyConfig;
import fortscale.utils.time.SystemDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.typeManufacturers.AbstractTypeManufacturer;

import java.time.Instant;

/**
 * Created by barak_schuster on 5/28/17.
 */
@Configuration
@Import(InstantAttributeStrategyConfig.class)
public class MockedEnrichedRecordGeneratorConfig {
    @Autowired
    private AbstractTypeManufacturer attributeStrategy;
    @Autowired
    private SystemDateService systemDateService;

    @Bean
    public MockedEnrichedRecordGenerator mockedEnrichedRecordGenerator() {
        PodamFactory dataFactory = new PodamFactoryImpl();
        dataFactory.getStrategy().addOrReplaceTypeManufacturer(Instant.class, attributeStrategy);
        return new MockedEnrichedRecordGenerator(dataFactory, systemDateService);
    }
}
