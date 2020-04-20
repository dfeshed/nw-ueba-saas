package presidio.data.generators.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A generator that returns a fixed map with each call to {@link IMapGenerator#getNext()}.
 * The fixed maps are returned in proper sequence, according to the array / list given in the c'tor.
 * This generator is cyclic, meaning that the array / list of maps is reiterated once the end is reached.
 *
 * @author Barak Schuster
 * @author Lior Govrin
 */
public class CyclicMapGenerator<K, V> extends CyclicValuesGenerator<Map<K, V>> implements IMapGenerator<K, V> {
    public CyclicMapGenerator(Map<K, V>[] fixedMaps) {
        super(fixedMaps);
    }

    public CyclicMapGenerator(List<Map<K, V>> fixedMaps) {
        this(toArray(fixedMaps));
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V>[] toArray(List<Map<K, V>> fixedMaps) {
        return fixedMaps.toArray((Map<K, V>[])new HashMap[fixedMaps.size()]);
    }
}
