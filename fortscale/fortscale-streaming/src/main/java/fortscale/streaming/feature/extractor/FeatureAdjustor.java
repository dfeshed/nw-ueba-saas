package fortscale.streaming.feature.extractor;

import net.minidev.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,include=JsonTypeInfo.As.PROPERTY,property="type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=InverseValueFeatureAdjustor.class, name=InverseValueFeatureAdjustor.INVERSE_VALUE_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=RateFeatureAdjustor.class, name=RateFeatureAdjustor.RATE_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=ConstantValueFeatureAdjustor.class, name=ConstantValueFeatureAdjustor.CONSTANT_VALUE_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=ChainFeatureAdjustor.class, name=ChainFeatureAdjustor.CHAIN_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=PatternReplacementFeatureAdjustor.class, name=PatternReplacementFeatureAdjustor.PATTERN_REPLACEMENT_FEATURE_ADJUSTOR_TYPE)
})
public interface FeatureAdjustor {

	public Object adjust(Object feature, JSONObject message);
}
