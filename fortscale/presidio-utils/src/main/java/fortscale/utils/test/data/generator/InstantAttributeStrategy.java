package fortscale.utils.test.data.generator;

import fortscale.utils.time.SystemDateService;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.common.AttributeStrategy;
import uk.co.jemos.podam.typeManufacturers.AbstractTypeManufacturer;
import uk.co.jemos.podam.typeManufacturers.TypeManufacturer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Map;

/**
 * supplies PODAM ( POjo DAta Mocker ) generation strategy for Instant members
 * Created by barak_schuster on 5/28/17.
 */
public class InstantAttributeStrategy extends AbstractTypeManufacturer<Instant> {
    private final SystemDateService systemDateService;

    public InstantAttributeStrategy(SystemDateService systemDateService) {
        this.systemDateService = systemDateService;
    }


    @Override
    public Instant getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, Map<String, Type> genericTypesArgumentsMap) {
        return systemDateService.getInstant();
    }
}
