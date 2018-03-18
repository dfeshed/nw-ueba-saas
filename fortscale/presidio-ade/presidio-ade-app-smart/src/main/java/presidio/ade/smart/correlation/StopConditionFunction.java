package presidio.ade.smart.correlation;

import fortscale.smart.correlation.conf.CorrelationNodeData;

import java.util.List;
import java.util.function.Function;


/**
 * Stop condition get correlationNodeData and return true if it exists in features list.
 * Usage: DescendantIterator looking for next descendant that exist in the list.
 *
 * see: fortscale.utils.DescendantIterator
 *
 */
public class StopConditionFunction implements  Function<CorrelationNodeData, Boolean> {

    private List<String> features;

    public StopConditionFunction(List<String> features) {
        this.features = features;
    }

    @Override
    public Boolean apply(CorrelationNodeData correlationNodeData) {
        String feature = correlationNodeData.getFeature();
        return features.contains(feature);

    }

}
