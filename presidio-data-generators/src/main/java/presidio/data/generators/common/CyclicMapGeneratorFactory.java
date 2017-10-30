package presidio.data.generators.common;

import java.util.List;
import java.util.Map;

/**
 * A factory that creates {@link CyclicMapGenerator}s, each one with the same fixed maps.
 *
 * @author Lior Govrin
 */
public class CyclicMapGeneratorFactory<K, V> {
    private final Map<K, V>[] fixedMaps;

    public CyclicMapGeneratorFactory(Map<K, V>[] fixedMaps) {
        this.fixedMaps = fixedMaps;
    }

    public CyclicMapGeneratorFactory(List<Map<K, V>> fixedMaps) {
        this(CyclicMapGenerator.toArray(fixedMaps));
    }

    public CyclicMapGenerator<K, V> createCyclicMapGenerator() {
        return new CyclicMapGenerator<>(fixedMaps);
    }
}
