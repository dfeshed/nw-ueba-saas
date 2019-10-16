package com.rsa.netwitness.presidio.automation.rest.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.joining;

public class IndicatorREST {

    public final JsonElement json;
    public final String type, schema, anomalyValue, name;
    public final String id;
    public final Instant startDate;
    public final Instant endDate;
    public final int scoreContribution;
    public final double score;
    public final List<HistoricalData> historicalData;
    public final Map<String, Object> contexts;
    public final int eventsNum;

    public IndicatorREST(JsonElement json) {
        this.json = json;

        id = json.getAsJsonObject().get("id").getAsString();
        name = json.getAsJsonObject().get("name").getAsString();

        startDate = Instant.ofEpochSecond(json.getAsJsonObject().get("startDate").getAsLong());
        endDate = Instant.ofEpochSecond(json.getAsJsonObject().get("endDate").getAsLong());

        anomalyValue = json.getAsJsonObject().get("anomalyValue").getAsString();
        schema = json.getAsJsonObject().get("schema").getAsString();
        scoreContribution = json.getAsJsonObject().get("scoreContribution").getAsInt();
        type = json.getAsJsonObject().get("type").getAsString();
        score = json.getAsJsonObject().get("score").getAsDouble();

        Type contextsTypeToken = new TypeToken<Map<String, Object>>() { }.getType();
        contexts = new Gson().fromJson(json.getAsJsonObject().get("contexts").toString(), contextsTypeToken);

        eventsNum = json.getAsJsonObject().get("eventsNum").getAsInt();
        historicalData = new ArrayList<>();

        if (json.getAsJsonObject().has("historicalData")) {
            Spliterator<JsonElement> historicalDataIt = json.getAsJsonObject().get("historicalData").getAsJsonArray().spliterator();
            historicalDataIt.tryAdvance(e -> historicalData.add(new HistoricalData(e)));
        }

    }

    public String contextToString() {
        return contexts.entrySet().parallelStream().sorted(comparingByKey())
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(joining("#"));
    }
}
