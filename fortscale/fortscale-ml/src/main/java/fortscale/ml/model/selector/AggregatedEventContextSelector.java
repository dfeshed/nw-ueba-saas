package fortscale.ml.model.selector;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.ml.model.Exceptions.InvalidAggregatedFeatureEventConfNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class AggregatedEventContextSelector implements IContextSelector {
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

    private AggregatedFeatureEventConf aggregatedFeatureEventConf;
    private String aggregatedFeatureEventConfName;
    public AggregatedEventContextSelector(AggregatedEventContextSelectorConf config) {
        aggregatedFeatureEventConfName = config.getAggregatedFeatureEventConfName();
        aggregatedFeatureEventConf = aggregatedFeatureEventsConfService
                .getAggregatedFeatureEventConf(aggregatedFeatureEventConfName);
        validate();
    }

    private void validate() {
        if(aggregatedFeatureEventConf==null)
            throw new InvalidAggregatedFeatureEventConfNameException(aggregatedFeatureEventConfName);
    }

    @Override
    public List<String> getContexts(Date startTime, Date endTime) {
        return aggregatedFeatureEventsReaderService.findDistinctContextsByTimeRange(
                aggregatedFeatureEventConf, startTime, endTime);
    }
}
