package fortscale.services.presidio.core.converters;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by shays on 24/09/2017.
 */
public class AggregationConverterHelper {
    private Logger logger = Logger.getLogger(this.getClass());

    public Map<String,Integer> convertAggregation(Map<String, Map<String, Long>> map, String key){
        for (Map.Entry<String,Map<String, Long>> aggregation : map.entrySet()) {
            if (aggregation.getKey().toLowerCase().equals(key.toLowerCase())) {
                return aggregation.getValue().entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> new Integer(entry.getValue().intValue())));
            }
        }
        logger.debug("Map {} is not found",key);
        return MapUtils.EMPTY_MAP;
    }
}
