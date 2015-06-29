package fortscale.streaming.aggregation.feature.extraction;

import net.minidev.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,include=JsonTypeInfo.As.PROPERTY,property="type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=InverseValueFeatureAdjustor.class, name=InverseValueFeatureAdjustor.INVERSE_VALUE_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=NumberDividerFeatureAdjustor.class, name= NumberDividerFeatureAdjustor.NUMBER_DIVIDER_FEATURE_ADJUSTOR),
    @JsonSubTypes.Type(value=ConstantValueFeatureAdjustor.class, name=ConstantValueFeatureAdjustor.CONSTANT_VALUE_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=ChainFeatureAdjustor.class, name=ChainFeatureAdjustor.CHAIN_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=IPv4FeatureAdjustor.class, name=IPv4FeatureAdjustor.IPV4_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=PatternReplacementFeatureAdjustor.class, name=PatternReplacementFeatureAdjustor.PATTERN_REPLACEMENT_FEATURE_ADJUSTOR_TYPE)
})
public interface FeatureAdjustor {
	Object adjust(Object value, JSONObject message);
}
