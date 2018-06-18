package org.apache.flume.interceptor.presidio.transform.predicate;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonObjectChainPredicate extends AbstractJsonObjectPredicate {

    public static final String TYPE = "chain";


    private LogicalOperation operation;
    private List<IJsonObjectPredicate> predicateList;
    @JsonIgnore
    private Predicate<JSONObject> allPredicates;

    @JsonCreator
    public JsonObjectChainPredicate(@JsonProperty("name")String name, @JsonProperty("operation")LogicalOperation operation,
                                    @JsonProperty("predicateList")List<IJsonObjectPredicate> predicateList){
        super(name);
        this.operation = Validate.notNull(operation, "operation can not be null");
        this.predicateList = Validate.notEmpty(predicateList, "predicate list should not be empty.");

        for (IJsonObjectPredicate predicate: predicateList){
            if (allPredicates == null){
                allPredicates = predicate;
            } else if (this.operation.equals(LogicalOperation.AND)){
                allPredicates = allPredicates.and(predicate);
            } else{
                allPredicates = predicate.or(allPredicates);
            }
        }
    }

    @Override
    public boolean test(JSONObject jsonObject) {
        boolean ret = allPredicates.test(jsonObject);
        return ret;
    }

    public enum LogicalOperation {

        AND,
        OR;

        public static List<String> names() {
            return Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
        }

    }
}
