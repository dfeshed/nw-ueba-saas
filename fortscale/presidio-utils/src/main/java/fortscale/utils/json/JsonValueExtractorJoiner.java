package fortscale.utils.json;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JsonValueExtractorJoiner {

    private String separator;
    private JSONObject jsonObject;
    private List<Object> values = new ArrayList<>();

    public JsonValueExtractorJoiner(String separator, JSONObject jsonObject){
        this.separator = separator;
        this.jsonObject = jsonObject;
    }

    public JsonValueExtractorJoiner add(IJsonValueExtractor extractor){
        if(values != null) {
            Object value = extractor.getValue(jsonObject);
            if (value == null) {
                values = null;
            } else {
                values.add(value);
            }
        }
        return this;
    }

    public JsonValueExtractorJoiner addAll(List<IJsonValueExtractor> extractors){
        for(IJsonValueExtractor extractor: extractors){
            add(extractor);
        }

        return this;
    }

    public JsonValueExtractorJoiner merge(JsonValueExtractorJoiner other){
        if(values == null || other.values == null){
            values = null;
        } else{
            values.addAll(other.values);
        }
        return this;
    }

    @Override
    public String toString(){
        String ret = null;
        if(values != null) {
            ret = StringUtils.join(values, separator);
        }
        return ret;
    }

    public static String joining(String separator, JSONObject jsonObject, List<IJsonValueExtractor> jsonValueExtractors){
        JsonValueExtractorJoiner joiner = new JsonValueExtractorJoiner(separator, jsonObject);
        joiner.addAll(jsonValueExtractors);
        return joiner.toString();
    }



}
