package org.apache.flume.interceptor.presidio.transform.predicate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.json.JSONObject;

import java.util.function.Predicate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JsonObjectChainPredicate.class, name = JsonObjectChainPredicate.TYPE),
        @JsonSubTypes.Type(value = JsonObjectKeyExistPredicate.class, name = JsonObjectKeyExistPredicate.TYPE),
        @JsonSubTypes.Type(value = JsonObjectRegexPredicate.class, name = JsonObjectRegexPredicate.TYPE)
})
public interface IJsonObjectPredicate extends Predicate<JSONObject> {

    String getName();
}
