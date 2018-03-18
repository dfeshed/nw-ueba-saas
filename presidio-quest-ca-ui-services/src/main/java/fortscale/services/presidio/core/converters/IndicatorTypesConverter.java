package fortscale.services.presidio.core.converters;

import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.rest.AlertRestFilter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shays on 11/09/2017.
 */
public class IndicatorTypesConverter {

    public List<String> convertUiFilterToQueryDto(Set<String> indicatorTypes){
        if (CollectionUtils.isEmpty(indicatorTypes)){
            return null;
        }
//        Set<String> anomalyType = new HashSet<>();
//        indicatorTypes.forEach(dataSourceAnomalyTypePair -> {
//            anomalyType.add(dataSourceAnomalyTypePair.getAnomalyType());
//        });
        return new ArrayList<String>(indicatorTypes);
    }
}