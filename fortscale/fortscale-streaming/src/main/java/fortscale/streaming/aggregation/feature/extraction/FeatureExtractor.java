package fortscale.streaming.aggregation.feature.extraction;

import net.minidev.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by amira on 15/06/2015.
 */

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,include=JsonTypeInfo.As.PROPERTY,property="featureExtractorType")
@JsonSubTypes({
        @JsonSubTypes.Type(value= EventFeatureExtractor.class, name= EventFeatureExtractor.EVENT_FEATURE_EXTRACTOR_TYPE),
        @JsonSubTypes.Type(value= PriorityContainerFeatureExtractor.class, name= PriorityContainerFeatureExtractor.PRIORITY_CONTAINER_FEATURE_EXTRACTOR_TYPE)
})
public interface FeatureExtractor {
    Object extract(JSONObject message);
}
