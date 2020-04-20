package fortscale.utils.json;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JsonValueExtractorJoinerCollector implements Collector<IJsonValueExtractor, JsonValueExtractorJoiner, String> {
    private String separator;
    private JSONObject jsonObject;

    public JsonValueExtractorJoinerCollector(String separator, JSONObject jsonObject){
        this.separator = separator;
        this.jsonObject = jsonObject;
    }

    @Override
    public Supplier<JsonValueExtractorJoiner> supplier() {
        return () -> new JsonValueExtractorJoiner(separator, jsonObject);
    }

    @Override
    public BiConsumer<JsonValueExtractorJoiner, IJsonValueExtractor> accumulator() {
        return JsonValueExtractorJoiner::add;
    }

    @Override
    public BinaryOperator<JsonValueExtractorJoiner> combiner() {
        return JsonValueExtractorJoiner::merge;
    }

    @Override
    public Function<JsonValueExtractorJoiner, String> finisher() {
        return JsonValueExtractorJoiner::toString;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    public static Collector<IJsonValueExtractor, JsonValueExtractorJoiner, String> joining(String separator, JSONObject jsonObject){
        return new JsonValueExtractorJoinerCollector(separator, jsonObject);
    }
}
