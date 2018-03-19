package presidio.ade.smart.correlation;

import fortscale.smart.correlation.conf.CorrelationNodeData;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Stop condition get correlationNodeData and return true if it exists in features list.
 * Usage: DescendantIterator looking for next descendant that exist in the list.
 *
 * see: fortscale.utils.DescendantIterator
 *
 */
public class StopIfFeatureExistFunction implements Function<CorrelationNodeData, Boolean> {

    private List<String> features;

    public StopIfFeatureExistFunction(Collection<FeatureCorrelation> features) {
        this.features = features.stream().map(feature -> feature.getName()).collect(Collectors.toList());
    }

    @Override
    public Boolean apply(CorrelationNodeData correlationNodeData) {
        String feature = correlationNodeData.getFeature();
        return features.contains(feature);

    }

}
