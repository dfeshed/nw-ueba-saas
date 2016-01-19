package fortscale.common.feature.extraction;

import java.util.Map;
import java.util.Set;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;

/**
 * Created by amira on 15/06/2015.
 */
public interface IFeatureExtractService {
    Feature extract(String featureName, Event event);
    Map<String, Feature> extract(Set<String> featureNames, Event event);
}
