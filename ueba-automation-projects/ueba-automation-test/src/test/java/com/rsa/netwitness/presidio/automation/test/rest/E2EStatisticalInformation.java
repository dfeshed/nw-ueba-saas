package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import com.rsa.netwitness.presidio.automation.utils.common.FileCommands;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class})
public class E2EStatisticalInformation extends AbstractTestNGSpringContextTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<String> scoreAggregationList;
    private List<String> staticIndicatorList;
    private List<String> featureAggregationList;
    private List<String> distinctFeatureAggregationList;
    private RestHelper restHelper = new RestHelper();

    @Test
    public void prepareStatisticalInformation() throws IOException {
        Set<String> collections = mongoTemplate.getCollectionNames();
        StringJoiner stringJoiner = new StringJoiner("\n");

        for (String collectionName : collections) {
            stringJoiner.add(String.format("%12d : %s", mongoTemplate.getCollection(collectionName).count(), collectionName));
        }
        FileCommands.makeDirectory(Consts.STATISTICAL_INFORMATION_DIRECTORY_PATH);
        FileUtils.cleanDirectory(new File(Consts.STATISTICAL_INFORMATION_DIRECTORY_PATH));
        FileCommands.writeToFile(Consts.COUNT_DOCUMENTS_COLLECTIONS_MONGODB, stringJoiner.toString());

        // Alerts: count of each severity alerts
        PresidioUrl url = restHelper.alerts().url().withAggregatedFieldParameter("SEVERITY");
        JSONObject aggregatedBySeverity = restHelper.alerts().request().getRestApiResponseAsJsonObj(url);
        assertThat(aggregatedBySeverity.has("aggregationData"))
                .as(url + "\nMissing field: 'aggregationData'.")
                .isTrue();

        String aggregationData = aggregatedBySeverity.getJSONObject("aggregationData").toString();
        FileCommands.writeToFile(Consts.COUNT_AGGREGATION_ALERTS_BY_SEVERITY, aggregationData);

        // Users: Risky users and all
        url = restHelper.entities().url().withMaxSizeAndSortedParameters("DESC", "SCORE");
        List<EntitiesStoredRecord> users = restHelper.entities().request().getEntities(url);
        assertThat(users).as(url + "\nempty response").isNotEmpty();
        StringJoiner stringJoinerUsers = new StringJoiner("\n");
        for (EntitiesStoredRecord user : users) {
            stringJoinerUsers.add(String.format("%6s : %s", user.getScore(), user.getEntityId()));
        }

        FileCommands.writeToFile(Consts.COUNT_ALL_USERS_WITH_SCORE, stringJoinerUsers.toString());

        // Count indicators each type
        CountIndicatorsEachType();

        StringJoiner numbersOfIndicatorsEachType = new StringJoiner("\n");
        numbersOfIndicatorsEachType.add("SCORE_AGGREGATION: " + scoreAggregationList.size());
        for (String s : scoreAggregationList)
            numbersOfIndicatorsEachType.add(s);
        numbersOfIndicatorsEachType.add("\nSTATIC_INDICATOR: " + staticIndicatorList.size());
        for (String s : staticIndicatorList)
            numbersOfIndicatorsEachType.add(s);
        numbersOfIndicatorsEachType.add("\nFEATURE_AGGREGATION: " + featureAggregationList.size());
        for (String s : featureAggregationList)
            numbersOfIndicatorsEachType.add(s);
        numbersOfIndicatorsEachType.add("\nFEATURE_AGGREGATION: " + distinctFeatureAggregationList.size());
        for (String s : distinctFeatureAggregationList)
            numbersOfIndicatorsEachType.add(s);

        FileCommands.writeToFile(Consts.COUNT_INDICATORS_EACH_TYPE, numbersOfIndicatorsEachType.toString());
    }

    private void CountIndicatorsEachType() {
        PresidioUrl url = restHelper.alerts().url().withMaxSizeAndExpendedParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);
        Assert.assertTrue(alerts.size() > 0, "Unable commit the alert tests. Alerts list is empty or unable to getOperationTypeToCategoryMap response from the output.");

        scoreAggregationList = new ArrayList<>();
        staticIndicatorList = new ArrayList<>();
        featureAggregationList = new ArrayList<>();
        distinctFeatureAggregationList = new ArrayList<>();

        for(AlertsStoredRecord alert : alerts){
            List<AlertsStoredRecord.Indicator> indicatorsList = alert.getIndicatorsList();
            if(indicatorsList != null && indicatorsList.size() > 0){
                for(AlertsStoredRecord.Indicator indicator : indicatorsList){
                    String indicatorType = indicator.getType();
                    if(indicatorType.equals("SCORE_AGGREGATION") && !scoreAggregationList.contains(indicator.getName())){
                        scoreAggregationList.add(indicator.getName());
                    }
                    else if(indicatorType.equals("STATIC_INDICATOR") && !staticIndicatorList.contains(indicator.getName())){
                        staticIndicatorList.add(indicator.getName());
                    }
                    else if(indicatorType.equals("FEATURE_AGGREGATION") && !featureAggregationList.contains(indicator.getName()) && !indicator.getName().contains("distinct")){
                        featureAggregationList.add(indicator.getName());
                    }
                    else if(indicatorType.equals("FEATURE_AGGREGATION") && !distinctFeatureAggregationList.contains(indicator.getName()) && indicator.getName().contains("distinct")){
                        distinctFeatureAggregationList.add(indicator.getName());
                    }
                }
            }
        }

    }
}
