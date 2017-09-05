package presidio.data.generators.common;

import java.util.Map;

/**
 * Created by barak_schuster on 9/4/17.
 */
public interface IMapGenerator<K,V> {
    Map<K,V> getNext();
}
