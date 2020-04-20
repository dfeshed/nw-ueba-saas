package presidio.data.generators.common;

import java.util.Map;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
public interface IMapGenerator<K, V> {
    boolean hasNext();

    Map<K, V> getNext();

    void reset();
}
