package fortscale.streaming.aggregation.feature.functions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.extraction.EventFeatureExtractor;
import fortscale.streaming.aggregation.feature.extraction.PriorityContainerFeatureExtractor;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;

import java.util.Map;

/**
 * Created by amira on 16/06/2015.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,include=JsonTypeInfo.As.PROPERTY,property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value= AggrFeatureHistogramFunc.class, name= AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE),
        @JsonSubTypes.Type(value= AggrFeatureAvStdNFunc.class, name= AggrFeatureAvStdNFunc.AGGR_FEATURE_FUNCTION_TYPE)
})
public interface AggrFeatureFunction {
    Object updateAggrFeature(AggregatedFeatureConf aggregatedFeatureConf, Map<String, Feature> features, Feature aggrFeature);
}
