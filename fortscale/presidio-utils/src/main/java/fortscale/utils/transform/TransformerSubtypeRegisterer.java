package fortscale.utils.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.reflection.PresidioReflectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class TransformerSubtypeRegisterer {

    private static final String TRANSFORMERS_UTIL_PACKAGE_LOCATION = "fortscale.utils.transform";

    public abstract Optional<String> additionalPackageLocation();

    public void registerSubtypes(ObjectMapper objectMapper) {
        ArrayList<String> transformerPackageLocations = new ArrayList<>();
        transformerPackageLocations.add(TRANSFORMERS_UTIL_PACKAGE_LOCATION);
        additionalPackageLocation().ifPresent(transformerPackageLocations::add);
        Collection<Class<? extends AbstractJsonObjectTransformer>> subTypes =
                PresidioReflectionUtils.getSubTypes(transformerPackageLocations, AbstractJsonObjectTransformer.class);
        List<Class<?>> subTypesAsGenericClasses = subTypes.stream().map(x -> (Class<?>) x).collect(Collectors.toList());
        objectMapper.registerSubtypes(subTypesAsGenericClasses);
    }
}
