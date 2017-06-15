package fortscale.common.feature.extraction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.common.feature.FeatureValue;
import fortscale.common.event.Event;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,include=JsonTypeInfo.As.PROPERTY,property="type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=InverseValueFeatureAdjustor.class, name=InverseValueFeatureAdjustor.INVERSE_VALUE_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=NumberDividerFeatureAdjustor.class, name=NumberDividerFeatureAdjustor.NUMBER_DIVIDER_FEATURE_ADJUSTOR),
    @JsonSubTypes.Type(value=ConstantValueFeatureAdjustor.class, name=ConstantValueFeatureAdjustor.CONSTANT_VALUE_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=ChainFeatureAdjustor.class, name=ChainFeatureAdjustor.CHAIN_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=IPv4FeatureAdjustor.class, name=IPv4FeatureAdjustor.IPV4_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=PatternReplacementFeatureAdjustor.class, name=PatternReplacementFeatureAdjustor.PATTERN_REPLACEMENT_FEATURE_ADJUSTOR_TYPE),
    @JsonSubTypes.Type(value=HourOfDayFeatureAdjustor.class, name=HourOfDayFeatureAdjustor.HOUR_OF_DAY_FEATURE_ADJUSTOR),
    @JsonSubTypes.Type(value=TimestampResolutionFeatureAdjuster.class, name=TimestampResolutionFeatureAdjuster.TIMESTAMP_RESOLUTION_FEATURE_ADJUSTER)
})
public interface FeatureAdjustor {
	FeatureValue adjust(FeatureValue value, Event event) throws Exception;
}
