package fortscale.utils.recordreader.getter;

import fortscale.utils.recordreader.transformation.Transformation;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class MapGetter extends Getter {
    private static final Class<?> clazz = Map.class;

    private final Map map;

    public MapGetter(Map<String, Transformation<?>> transformations, Map map) {
        super(transformations);
        this.map = Objects.requireNonNull(map);
    }

    @Override
    Class<?> getInstanceClass() {
        return clazz;
    }

    @Override
    boolean isPresentInInstance(String key) {
        return map.containsKey(key);
    }

    @Override
    Object getFromInstance(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            throw new NoSuchElementException(String.format("%s does not " +
                    "contain a key named %s.", clazz.getSimpleName(), key));
        }
    }
}
