package com.rsa.netwitness.presidio.automation.utils.output;

import com.rsa.netwitness.presidio.automation.config.AutomationConf;
import com.rsa.netwitness.presidio.automation.domain.activedirectory.OutputActiveDirectoryEnrichedStoredData;
import com.rsa.netwitness.presidio.automation.domain.authentication.OutputAuthenticationEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.file.OutputFileEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.SmartUserIdStoredRecored;
import com.rsa.netwitness.presidio.automation.domain.process.OutputProcessEnrichedStoredData;
import com.rsa.netwitness.presidio.automation.domain.registry.OutputRegistryEnrichedStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.rest.client.RestAPI;
import com.rsa.netwitness.presidio.automation.rest.client.RestApiResponse;
import com.rsa.netwitness.presidio.automation.mapping.indicators.IndicatorsInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.collections.Maps;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class OutputTestsUtils {
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(OutputTestsUtils.class.getName());

    public static void skipTest(String message) {
        LOGGER.error("TEST SKIPPED. Reason: " + message);
        throw new SkipException(message);
    }
    public static List<SmartUserIdStoredRecored> sortByStartTime(List<SmartUserIdStoredRecored> records) {
        List<SmartUserIdStoredRecored> sorted = new ArrayList<>();
        while (records.size() > 0) {
            int minIndex = findMinIndex(records);
            sorted.add(records.get(minIndex));
            records.remove(minIndex);
        }

        return sorted;
    }

    private static int findMinIndex(List<SmartUserIdStoredRecored> records) {
        int minIndex = 0;
        long minValue = Integer.MAX_VALUE;

        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getStartInstant().toEpochMilli() <= minValue) {
                minValue = records.get(i).getStartInstant().toEpochMilli();
                minIndex = i;
            }
        }
        return minIndex;
    }

    public static List<String> buildUserIdListFromSmartsList(List<SmartUserIdStoredRecored> smarts) {
        List<String> userIds = new ArrayList<String>();

        for (SmartUserIdStoredRecored item : smarts) {
            String userId = item.getContext().getUserId();

            if (!userIds.contains(userId)) {
                userIds.add(userId);
            }
        }

        return userIds;
    }

    public static List<EntitiesStoredRecord> filterUserWithAlerts(List<EntitiesStoredRecord> users) {
        List<EntitiesStoredRecord> filteredUsers = new ArrayList<>();
        for (EntitiesStoredRecord user : users) {
            if (user.getAlertCount() > 0) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    public static Map<String, String> convertUserIdsToUsernameMap(List<EntitiesStoredRecord> userList, List<String> ids) {
        Map<String, String> userNamesMap = new HashMap<String, String>();

        for (String id : ids) {
            for (EntitiesStoredRecord rec : userList) {
                if (rec.getEntityId().equals(id)) {
                    int index = 0;
                    if (rec.getEntityName().contains("\\")) {
                        index = rec.getEntityName().indexOf("\\") + 1;
                    }
                    userNamesMap.put(id, rec.getEntityName().substring(index));
                }
            }
        }

        return userNamesMap;
    }

    public static List<String> convertIndicatorArrayToFeatureNameList(String[] indicators) {
        List<String> featureList = new ArrayList<>();

        for (String indicator : indicators) {
            featureList.add(IndicatorsInfo.getFeatureNameByIndicator(indicator));
        }

        return featureList;
    }

    public static List<String> getUserIdsListFromOutputCollections(OutputActiveDirectoryStoredDataRepository adRepository,
                                                                   OutputAuthenticationStoredDataRepository authRepository,
                                                                   OutputFileStoredDataRepository fileRepository,
                                                                   OutputProcessStoredDataRepository processRepository,
                                                                   OutputRegistryStoredDataRepository registryRepository) {
        List<OutputActiveDirectoryEnrichedStoredData> adList = adRepository.findAll();
        List<OutputAuthenticationEnrichStoredData> authList = authRepository.findAll();
        List<OutputFileEnrichStoredData> fileList = fileRepository.findAll();
        List<OutputProcessEnrichedStoredData> processList = processRepository.findAll();
        List<OutputRegistryEnrichedStoredData> registryList = registryRepository.findAll();

        List<String> userIds = new ArrayList<>();

        for (OutputActiveDirectoryEnrichedStoredData item : adList) {
            if (!userIds.contains(item.getUserId())) {
                userIds.add(item.getUserId());
            }
        }

        for (OutputAuthenticationEnrichStoredData item : authList) {
            if (!userIds.contains(item.getUserId())) {
                userIds.add(item.getUserId());
            }
        }

        for (OutputFileEnrichStoredData item : fileList) {
            if (!userIds.contains(item.getUserId())) {
                userIds.add(item.getUserId());
            }
        }
        for (OutputProcessEnrichedStoredData item : processList) {
            if (!userIds.contains(item.getUserId())) {
                userIds.add(item.getUserId());
            }
        }
        for (OutputRegistryEnrichedStoredData item : registryList) {
            if (!userIds.contains(item.getUserId())) {
                userIds.add(item.getUserId());
            }
        }

        return userIds;
    }

    public static List<String> getUserNamesListFromOutputCollections(OutputActiveDirectoryStoredDataRepository adRepository, OutputAuthenticationStoredDataRepository authRepository, OutputFileStoredDataRepository fileRepository, OutputProcessStoredDataRepository processRepository, OutputRegistryStoredDataRepository registryRepository) {
        List<OutputActiveDirectoryEnrichedStoredData> adList = adRepository.findAll();
        List<OutputAuthenticationEnrichStoredData> authList = authRepository.findAll();
        List<OutputFileEnrichStoredData> fileList = fileRepository.findAll();
        List<OutputProcessEnrichedStoredData> processList = processRepository.findAll();
        List<OutputRegistryEnrichedStoredData> registryList = registryRepository.findAll();

        List<String> userId = new ArrayList<>();
        for (OutputActiveDirectoryEnrichedStoredData item : adList) {
            if (item.getUserId().equals("initiator_ad_user_id")) {
                if (!userId.contains(item.getUserName())) {
                    userId.add(item.getUserName());
                }
            } else {
                if (!userId.contains(item.getUserId())) {
                    userId.add(item.getUserId());
                }
            }

        }
        for (OutputAuthenticationEnrichStoredData item : authList) {
            if (!userId.contains(item.getUserId())) {
                userId.add(item.getUserId());
            }
        }
        for (OutputFileEnrichStoredData item : fileList) {
            if (!userId.contains(item.getUserId())) {
                userId.add(item.getUserId());
            }
        }
        for (OutputProcessEnrichedStoredData item : processList) {
            if (!userId.contains(item.getUserId())) {
                userId.add(item.getUserId());
            }
        }
        for (OutputRegistryEnrichedStoredData item : registryList) {
            if (!userId.contains(item.getUserId())) {
                userId.add(item.getUserId());
            }
        }
        return userId;
    }

    public static List<String> extractUsernameListsFromUserList(List<EntitiesStoredRecord> users) {
        List<String> userList = new ArrayList<>();

        for (EntitiesStoredRecord user : users) {
            String userName = user.getEntityName();
            if (userName.contains("\\") && !userName.contains("TestContains")) {
                userName = userName.split("\\\\")[1];
            }
            userList.add(userName);
        }

        return userList;
    }

    public static List<String> extractUserIdListsFromUserList(List<EntitiesStoredRecord> users) {
        List<String> userList = new ArrayList<>();

        for (EntitiesStoredRecord user : users) {
            String userId = user.getEntityId();
            if (userId.contains("\\") && !userId.contains("TestContains")) {
                userId = userId.split("\\\\")[1];
            }
            userList.add(userId);
        }

        return userList;
    }

    public static List<AlertsStoredRecord.Indicator> sortIndicatorListByScoreContribution(List<AlertsStoredRecord.Indicator> indicators) {
        List<AlertsStoredRecord.Indicator> sorted = new ArrayList<>();
        List<AlertsStoredRecord.Indicator> tmp = indicators;
        while (!tmp.isEmpty()) {
            int greater = getIndexOfMaxScoreContributionIndicator(tmp);
            sorted.add(tmp.get(greater));
            tmp.remove(greater);
        }

        return sorted;
    }

    public static int getIndexOfMaxScoreContributionIndicator(List<AlertsStoredRecord.Indicator> indicators) {
        int maxIndex = 0;
        double maxScore = 0;

        for (int i = 0; i < indicators.size(); i++) {
            if (indicators.get(i).getScoreContribution() > maxScore) {
                maxScore = indicators.get(i).getScoreContribution();
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static HashMap<String, Integer> countAlertSeverities(List<AlertsStoredRecord> alerts) {
        HashMap<String, Integer> severitiesCount = new HashMap<>();
        for (AlertsStoredRecord alert : alerts) {
            if (severitiesCount.containsKey(alert.getSeverity())) {
                int tmp = severitiesCount.get(alert.getSeverity());
                severitiesCount.put(alert.getSeverity(), tmp + 1);
            } else {
                severitiesCount.put(alert.getSeverity(), 1);
            }
        }

        return severitiesCount;
    }

    public static HashMap<String, Integer> countAlertField(JSONObject alertsObj, String field) {
        JSONArray alerts = null;
        try {
            alerts = alertsObj.getJSONArray("alerts");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, Integer> count = new HashMap<>();

        for (int i = 0; i < alerts.length(); i++) {
            try {
                if (count.containsKey(alerts.getJSONObject(i).getString(field))) {
                    int tmp = count.get(alerts.getJSONObject(i).getString(field));
                    count.put(alerts.getJSONObject(i).getString(field), tmp + 1);
                } else {
                    count.put(alerts.getJSONObject(i).getString(field), 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return count;
    }


    public static int findEarlierEventTimeIndex(JSONArray events) {
        long min = Long.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < events.length(); i++) {
            try {
                long curr = events.getJSONObject(i).getJSONObject("eventDate").getLong("epochSecond");
                if (curr < min) {
                    min = curr;
                    index = i;
                }
            } catch (Exception e) {
                Assert.fail("Could not get the eventDate.\n" + e.getMessage());
            }
        }

        return index;
    }

    public static Map<String, Integer> getSeveritiesValues() {
        Map<String, Integer> severities = new HashMap<>();
        JSONObject body = getOutputProcessorConfiguration();

        try {
            JSONObject severity = body.getJSONObject("severity");
            Iterator<String> keyItr = severity.keys();
            while (keyItr.hasNext()) {
                String key = keyItr.next();
                Object value = severity.get(key);

                if (key.equals("mid")) key = "medium";
                severities.put(key, Integer.parseInt(value.toString()));
            }

            if (!severities.containsKey("low")) {
                severities.put("low", 0);
            }

        } catch (JSONException e) {
            String msg = "Unable to read the severity keys from the JSON.\n" +
                    "Error message: " + e.getMessage();
            Assert.fail(msg);
        }

        return severities;
    }

    private static int getMinScore(List<EntitiesStoredRecord> users) {
        int minScore = Integer.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < users.size(); i++) {
            if (Integer.parseInt(users.get(i).getScore()) < minScore) {
                minScore = Integer.parseInt(users.get(i).getScore());
                index = i;
            }
        }
        return index;
    }

    public static Map<String, Integer> getAlertEntityScoreContributions() {
        JSONObject outputProcessorConfigurationBody = getOutputProcessorConfiguration();
        Map<String, Integer> scoreContributions = Maps.newHashMap();

        try {
            JSONObject contributionScores = outputProcessorConfigurationBody
                    .getJSONObject("entity")
                    .getJSONObject("score")
                    .getJSONObject("alert")
                    .getJSONObject("contribution");

            assertThat(contributionScores.length()).isGreaterThan(0);

            contributionScores.keys()
                    .forEachRemaining(e -> scoreContributions.put(e.toString(), contributionScores.getInt(e.toString())));
        } catch (JSONException e) {
            String msg = "Unable to read the severity keys from the JSON.\n" +
                    "Error message: " + e.getMessage();
            Assert.fail(msg);
        }

        return scoreContributions;
    }

    private static JSONObject getOutputProcessorConfiguration() {
        RestApiResponse response = new RestAPI().sendGet("http://" + AutomationConf.UEBA_HOST + ":8888/output-processor.json");
        Assert.assertEquals(200, response.getResponseCode());
        JSONObject body = null;
        try {
            body = new JSONObject(response.getResultBody());
        } catch (JSONException e) {
            String msg = "Could not parse the output-processor.json into a JSONObject.\n" +
                    "Error message: " + e.getMessage();
            Assert.fail(msg);
        }
        return body;
    }
}
