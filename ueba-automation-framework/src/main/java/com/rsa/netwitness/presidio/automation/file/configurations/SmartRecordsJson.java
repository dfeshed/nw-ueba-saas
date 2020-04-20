package com.rsa.netwitness.presidio.automation.file.configurations;

import com.google.gson.*;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SmartRecordsJson {
    private final String PATH = "/var/lib/netwitness/presidio/asl/smart-records/smart_records.json";
    private JsonObject rootObj;

    private SmartRecordsJson() {
        resolve();
    }

    public static SmartRecordsJson getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public SmartRecordsJson removeTlsNewAccuraciesFromExcludedAggregationRecords() {
        Predicate<String> filterCondition = element -> !(element.contains("ForNew") || element.contains("ToNew"));
        return editExcludedAggregationRecords(filterCondition);
    }

    private SmartRecordsJson editExcludedAggregationRecords(Predicate<String> filterCondition) {
        JsonArray smartRecordConfs = rootObj.getAsJsonArray("SmartRecordConfs");

        for (JsonElement next : smartRecordConfs) {
            List<String> excludedRecords = new ArrayList<>();
            next.getAsJsonObject().getAsJsonArray("excludedAggregationRecords").forEach(e -> excludedRecords.add(e.getAsString()));
            List<String> recordsToSave = excludedRecords.stream().filter(filterCondition).collect(Collectors.toList());

            next.getAsJsonObject().remove("excludedAggregationRecords");
            JsonElement jsonElement = new Gson().toJsonTree(recordsToSave);
            next.getAsJsonObject().add("excludedAggregationRecords", jsonElement);
        }
        return this;
    }

    public void flush() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String newContent = gson.toJson(rootObj);

        SshHelper sshHelper = new SshHelper();
        SshResponse response = sshHelper.uebaHostExec().withExitCodeValidation().withTimeout(10, TimeUnit.SECONDS)
                .run("echo \"" + newContent + "\" > " + PATH);
    }

    public synchronized void resolve() {
        Gson gson = new Gson();
        String json = getJsonString();
        rootObj = gson.fromJson(json, JsonObject.class);
    }

    private String getJsonString() {
        SshHelper sshHelper = new SshHelper();
        SshResponse response = sshHelper.uebaHostExec().withExitCodeValidation().withTimeout(10, TimeUnit.SECONDS).run("cat ".concat(PATH));
        assertThat(response.output).hasSizeGreaterThan(10);
        return String.join("\n", response.output).trim();
    }

    private static class InstanceHolder {
        private static SmartRecordsJson INSTANCE = new SmartRecordsJson();
    }

}
