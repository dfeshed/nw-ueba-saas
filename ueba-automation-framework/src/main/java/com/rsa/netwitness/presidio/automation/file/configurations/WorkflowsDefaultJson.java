package com.rsa.netwitness.presidio.automation.file.configurations;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;

public class WorkflowsDefaultJson {
    private JsonObject adeModelsNode;

    private WorkflowsDefaultJson() {
        resolve();
    }

    public static WorkflowsDefaultJson getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static WorkflowsDefaultJson INSTANCE = new WorkflowsDefaultJson();
    }


    public EnrichedRecords getEnrichedRecordsConf() {
        JsonObject enrichedRecords = adeModelsNode.getAsJsonObject("enriched_records");
        return new EnrichedRecords(enrichedRecords.getAsJsonPrimitive("build_model_interval_in_days").getAsInt(),
                enrichedRecords.getAsJsonPrimitive("feature_aggregation_buckets_interval_in_days").getAsInt(),
                enrichedRecords.getAsJsonPrimitive("min_data_time_range_for_building_models_in_days").getAsInt());
    }

    public FeatureAggregationRecords getFeatureAggregationRecordsConf() {
        JsonObject enrichedRecords = adeModelsNode.getAsJsonObject("feature_aggregation_records");
        return new FeatureAggregationRecords(enrichedRecords.getAsJsonPrimitive("build_model_interval_in_days").getAsInt(),
                enrichedRecords.getAsJsonPrimitive("accumulate_interval_in_days").getAsInt(),
                enrichedRecords.getAsJsonPrimitive("min_data_time_range_for_building_models_in_days").getAsInt());
    }

    public SmartRecords getSmartRecordsConf() {
        JsonObject enrichedRecords = adeModelsNode.getAsJsonObject("smart_records");
        return new SmartRecords(enrichedRecords.getAsJsonPrimitive("build_model_interval_in_days").getAsInt(),
                enrichedRecords.getAsJsonPrimitive("accumulate_interval_in_days").getAsInt(),
                enrichedRecords.getAsJsonPrimitive("min_data_time_range_for_building_models_in_days").getAsInt());
    }




    public synchronized void resolve() {
        Gson gson = new Gson();
        String json = getJsonString();
        JsonObject rootObj = gson.fromJson(json, JsonObject.class);
        adeModelsNode = rootObj.getAsJsonObject("components").getAsJsonObject("ade").getAsJsonObject("models");
    }





    private String getJsonString() {
        String filePath = "/etc/netwitness/presidio/configserver/configurations/airflow/workflows-default.json";
        SshHelper sshHelper = new SshHelper();
        SshResponse response = sshHelper.uebaHostExec().withTimeout(10, TimeUnit.SECONDS).run("cat ".concat(filePath));
        assertThat(response.exitCode).isEqualTo(0);
        assertThat(response.output).hasSizeGreaterThan(10);
        return String.join("\n", response.output).trim();
    }

    public class EnrichedRecords {
        public final int buildModelIntervalInDays;
        public final int featureAggregationBucketsIntervalInDays;
        public final int minDataTimeRangeForBuildingModelsInDays;

        private EnrichedRecords(int buildModelIntervalInDays, int featureAggregationBucketsIntervalInDays, int minDataTimeRangeForBuildingModelsInDays) {
            this.buildModelIntervalInDays = buildModelIntervalInDays;
            this.featureAggregationBucketsIntervalInDays = featureAggregationBucketsIntervalInDays;
            this.minDataTimeRangeForBuildingModelsInDays = minDataTimeRangeForBuildingModelsInDays;
        }
    }

    public class FeatureAggregationRecords {
        public final int buildModelIntervalInDays;
        public final int accumulateIntervalInDays;
        public final int minDataTimeRangeForBuildingModelsInDays;

        private FeatureAggregationRecords(int buildModelIntervalInDays, int accumulateIntervalInDays, int minDataTimeRangeForBuildingModelsInDays) {
            this.buildModelIntervalInDays = buildModelIntervalInDays;
            this.accumulateIntervalInDays = accumulateIntervalInDays;
            this.minDataTimeRangeForBuildingModelsInDays = minDataTimeRangeForBuildingModelsInDays;
        }
    }

    public class SmartRecords {
        public final int buildModelIntervalInDays;
        public final int accumulateIntervalInDays;
        public final int minDataTimeRangeForBuildingModelsInDays;

        private SmartRecords(int buildModelIntervalInDays, int accumulateIntervalInDays, int minDataTimeRangeForBuildingModelsInDays) {
            this.buildModelIntervalInDays = buildModelIntervalInDays;
            this.accumulateIntervalInDays = accumulateIntervalInDays;
            this.minDataTimeRangeForBuildingModelsInDays = minDataTimeRangeForBuildingModelsInDays;
        }
    }
}
