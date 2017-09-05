package presidio.data.generators.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by barak_schuster on 9/4/17.
 */
public class FixedMapGenerator<K,V> extends CyclicValuesGenerator<Map<K,V>> implements IMapGenerator {

    public FixedMapGenerator(List<Map<K,V>> fixedMap) {
        super(fixedMap.toArray(new HashMap[fixedMap.size()]));
    }

}
