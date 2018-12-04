package fortscale.utils.recordreader.getter;

import fortscale.utils.recordreader.transformation.Transformation;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Getter {
    private final Map<String, Transformation<?>> transformations;

    /**
     * C'tor for subclasses in package.
     * @param transformations A map containing the transformations that are used when keys are missing.
     */
    Getter(Map<String, Transformation<?>> transformations) {
        this.transformations = Objects.requireNonNull(transformations);
    }

    /**
     * @return The value of the given key from the underlying instance,
     *         or the result of a transformation configured with this feature name.
     */
    public Object get(String key) {
        return isPresentInInstance(key) ? getFromInstance(key) : getFromTransformation(key);
    }

    /**
     * @return The class of the underlying instance. For private use.
     */
    abstract Class<?> getInstanceClass();

    /**
     * @return True if the given key is present in the underlying instance, false otherwise. For private use.
     */
    abstract boolean isPresentInInstance(String key);

    /**
     * @return The value of the given key from the underlying instance. For private use.
     * @throws NoSuchElementException If the given key is not present in the underlying instance.
     */
    abstract Object getFromInstance(String key);

    private Object getFromTransformation(String key) {
        if (transformations.containsKey(key)) {
            Transformation<?> transformation = transformations.get(key);
            Map<String, Object> requiredFieldNameToValueMap = transformation.getRequiredFieldNames().stream()
                    .collect(Collectors.toMap(Function.identity(), this::getFromInstance));
            return transformation.transform(requiredFieldNameToValueMap);
        } else {
            throw new NoSuchElementException(String.format("%s is not present in instance of %s, and there is no " +
                    "transformation configured with this feature name.", key, getInstanceClass().getSimpleName()));
        }
    }
}
