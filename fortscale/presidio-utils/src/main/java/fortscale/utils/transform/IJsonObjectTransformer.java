package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.json.JSONObject;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JsonObjectChainTransformer.class, name = JsonObjectChainTransformer.TYPE),
        @JsonSubTypes.Type(value = RegexCaptorAndFormatter.class, name = RegexCaptorAndFormatter.TYPE),
        @JsonSubTypes.Type(value = SwitchCaseTransformer.class, name = SwitchCaseTransformer.TYPE),
        @JsonSubTypes.Type(value = FilterTransformer.class, name = FilterTransformer.TYPE),
        @JsonSubTypes.Type(value = FilterKeyTransformer.class, name = FilterKeyTransformer.TYPE),
        @JsonSubTypes.Type(value = FindAndReplaceTransformer.class, name = FindAndReplaceTransformer.TYPE),
        @JsonSubTypes.Type(value = IfElseTransformer.class, name = IfElseTransformer.TYPE),
        @JsonSubTypes.Type(value = EpochTimeToNanoRepresentationTransformer.class, name = EpochTimeToNanoRepresentationTransformer.TYPE),
        @JsonSubTypes.Type(value = CopyValueTransformer.class, name = CopyValueTransformer.TYPE),
        @JsonSubTypes.Type(value = SetterTransformer.class, name = SetterTransformer.TYPE),
        @JsonSubTypes.Type(value = JoinTransformer.class, name = JoinTransformer.TYPE)
})
public interface IJsonObjectTransformer {

    JSONObject transform(JSONObject jsonObject);
    String getName();
}
