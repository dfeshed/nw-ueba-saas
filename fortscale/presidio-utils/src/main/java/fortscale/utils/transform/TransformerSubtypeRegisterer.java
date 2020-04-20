package fortscale.utils.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.reflection.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class TransformerSubtypeRegisterer {
    private static final String TRANSFORM_UTILS_PACKAGE_LOCATION = "fortscale.utils.transform";

    public abstract Optional<String> additionalPackageLocation();

    public void registerSubtypes(ObjectMapper objectMapper) {
        List<String> packageLocations = new ArrayList<>();
        packageLocations.add(TRANSFORM_UTILS_PACKAGE_LOCATION);
        additionalPackageLocation().ifPresent(packageLocations::add);
        Set<Class<?>> subTypes = ReflectionUtils.getSubTypesOf(packageLocations, AbstractJsonObjectTransformer.class);
        objectMapper.registerSubtypes(subTypes);
    }
}
