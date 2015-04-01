package fortscale.streaming.feature.extractor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import net.minidev.json.JSONObject;


@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,include=JsonTypeInfo.As.PROPERTY,property="type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=EventFeatureExtractor.class, name=EventFeatureExtractor.EVENT_FEATURE_EXTRACTOR_TYPE),
    @JsonSubTypes.Type(value=PriorityContainerFeatureExtractor.class, name=PriorityContainerFeatureExtractor.PRIORITY_CONTAINER_FEATURE_EXTRACTOR_TYPE)
})
public interface FeatureExtractor {

	public Object extract(JSONObject message);
}
