package com.rsa.netwitness.presidio.automation.rest.model;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.joining;

public class HistoricalData {
    public final String type;
    public final Map<String, Object> contexts;
    public final List<HistoricalDataBucket> allBuckets = Lists.newArrayList();
    public final List<HistoricalDataBucket> anomalyBuckets;

    HistoricalData(JsonElement json) {
        type = json.getAsJsonObject().get("type").getAsString();

        Type contextsTypeToken = new TypeToken<Map<String, Object>>() { }.getType();
        contexts = new Gson().fromJson(json.getAsJsonObject().get("contexts").toString(), contextsTypeToken);

        JsonArray buckets = json.getAsJsonObject().getAsJsonArray("buckets");
        boolean isValueArray = buckets.getAsJsonArray().get(0).getAsJsonObject().get("value").isJsonArray();

        if (isValueArray) {
            buckets.forEach(bucket -> bucket.getAsJsonObject().get("value").getAsJsonArray().iterator()
                    .forEachRemaining(value -> getAllBuckets.apply(value.getAsJsonObject()).ifPresent(allBuckets::add)));
        } else {
            buckets.forEach(bucket -> getAllBuckets.apply(bucket.getAsJsonObject()).ifPresent(allBuckets::add));
        }

        anomalyBuckets = allBuckets.parallelStream().filter(e -> e.anomaly.equals(true)).collect(Collectors.toList());
    }

    public String contextToString() {
        return contexts.entrySet().parallelStream().sorted(comparingByKey())
                .map(entry -> entry.getKey() + "==" + entry.getValue())
                .collect(joining("#"));
    }

    private Function<JsonObject, Optional<HistoricalDataBucket>> getAllAnomalyBuckets = obj -> {
        if (obj.has("anomaly")) {
            HistoricalDataBucket bucket = new HistoricalDataBucket();
            bucket.key = obj.get("key").getAsString();
            bucket.value = obj.get("value").getAsString();
            bucket.anomaly = obj.get("anomaly").getAsBoolean();
            return Optional.of(bucket);
        } else {
            return Optional.empty();
        }
    };

    private Function<JsonObject, Optional<HistoricalDataBucket>> getAllBuckets = obj -> {
        HistoricalDataBucket bucket = new HistoricalDataBucket();
        bucket.key = obj.get("key").getAsString();
        bucket.value = obj.get("value").getAsString();
        bucket.anomaly = obj.has("anomaly") && obj.get("anomaly").getAsBoolean();
        return Optional.of(bucket);
    };

}