package com.rsa.netwitness.presidio.automation.rest.helper.rest;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.client.RestAPI;
import com.rsa.netwitness.presidio.automation.rest.client.RestApiResponse;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rsa.netwitness.presidio.automation.rest.client.HttpMethod.GET;
import static com.rsa.netwitness.presidio.automation.rest.client.HttpMethod.PATCH;


public class EntitiesRestCallHelper implements IRestCallHelper {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(EntitiesRestCallHelper.class);

    private RestApiResponse getResponse(PresidioUrl presidioUrl) {
        if (presidioUrl.METHOD.equals(GET)) {
            return RestAPI.sendGet(presidioUrl.URL);
        } else if (presidioUrl.METHOD.equals(PATCH)) {
            return RestAPI.sendPatch(presidioUrl.URL, presidioUrl.JSON_BODY);
        } else {
            throw new RuntimeException("No such method: " + presidioUrl.METHOD);
        }
    }

    public List<EntitiesStoredRecord> getEntities(PresidioUrl presidioUrl) {

        LOGGER.debug("Sending request: " + presidioUrl);
        RestApiResponse response = getResponse(presidioUrl);

        Assert.assertEquals(200, response.getResponseCode(),
                "Error with response code: " + response.getResponseCode() +
                        "\nRequest: " + presidioUrl.print() +
                        "\nError message: " + response.getErrorMessage());

        LOGGER.debug(response.getResultBody());
        JSONObject json;
        List<EntitiesStoredRecord> entitiesStoredRecords = new ArrayList<>();

        try {
            json = new JSONObject(response.getResultBody());
            JSONArray arr;
            if(json.has("entities")) {
                arr = json.getJSONArray("entities");
            } else {
                arr = new JSONArray();
                arr.put(json);
            }

            for (int i = 0 ; i < arr.length() ; i++){
                JSONObject tmp = arr.getJSONObject(i);

                String id = tmp.get("id").toString();
                String[] tags = {};

                if(!tmp.isNull("tags")){
                    String tagsSimpleString = tmp.getJSONArray("tags").toString().replace("[","");
                    tagsSimpleString = tagsSimpleString.replace("]", "");
                    if(tagsSimpleString.length() > 0){
                        tagsSimpleString = tagsSimpleString.substring(1, tagsSimpleString.length()-1);
                        tagsSimpleString = tagsSimpleString.replace("\"", "");
                    }
                    tags = tagsSimpleString.split(",");
                }


                String entityName = tmp.get("entityName").toString();
                String entityId = tmp.getString("entityId");
                String entityType = tmp.get("entityType").toString();
                String alertClassificationSimpleString = tmp.get("alertClassifications").toString();
                String[] alertClassifications = null;
                if(alertClassificationSimpleString.length() > 2) {
                    alertClassifications = alertClassificationSimpleString.split(",");
                }
                Integer alertCount = Integer.parseInt(tmp.get("alertsCount").toString());

                String score = tmp.get("score").toString();
                String severity = tmp.get("severity").toString();

                Map<String, Integer> trendingScore = new HashMap<>();
                JSONObject trendingScoreObj = tmp.getJSONObject("trendingScore");
                trendingScore.put("daily" , trendingScoreObj.getInt("daily"));
                trendingScore.put("weekly" , trendingScoreObj.getInt("weekly"));

                List<AlertsStoredRecord> alerts = null;
                if(presidioUrl.URL.toLowerCase().contains("expand=true")) {

                    alerts = new ArrayList<>();
                    JSONArray alertsJson = tmp.getJSONArray("alerts");

                    for(int j = 0 ; j < alertsJson.length() ; j++) {
                        JSONObject tmpAlert = alertsJson.getJSONObject(j);

                        JSONArray probeArray = tmpAlert.optJSONArray("classifiation");
                        if (probeArray == null) {
                            System.out.println("Warning: missing classification for id: " + id);
                            continue;
                        }

                        String classificationSimpleString = probeArray.toString().replace("[","");
                        classificationSimpleString = classificationSimpleString.replace("]", "");
                        classificationSimpleString = classificationSimpleString.substring(1, classificationSimpleString.length()-1);
                        classificationSimpleString = classificationSimpleString.replace("\"", "");
                        String[] classification = classificationSimpleString.split(",");

                        String indicatorsNameSimpleString = tmpAlert.getJSONArray("indicatorsName").toString().replace("[","");
                        indicatorsNameSimpleString = indicatorsNameSimpleString.replace("]", "");
                        indicatorsNameSimpleString = indicatorsNameSimpleString.substring(1, indicatorsNameSimpleString.length()-1);
                        indicatorsNameSimpleString = indicatorsNameSimpleString.replace("\"", "");
                        String[] indicatorsName = indicatorsNameSimpleString.split(",");

                        /*
                        String indicatorsNameSimpleString = tmpAlert.get("indicatorsName").toString();
                        String[] indicatorsName = null;
                        if(indicatorsNameSimpleString.length() > 2) {
                            indicatorsName = indicatorsNameSimpleString.split(",");
                        }
                        */

                        alerts.add(new AlertsStoredRecord(
                                tmpAlert.get("id").toString(), classification,
                                tmpAlert.get("entityName").toString(), tmpAlert.get("entityType").toString(),
                                indicatorsName, Integer.parseInt(tmpAlert.get("indicatorsNum").toString()),
                                tmpAlert.get("score").toString(), tmpAlert.get("feedback").toString(),
                                tmpAlert.get("entityScoreContribution").toString(), tmpAlert.get("timeframe").toString(),
                                tmpAlert.get("severity").toString(), tmpAlert.get("entityDocumentId").toString(),
                                Instant.ofEpochMilli(tmpAlert.getLong("startDate")), Instant.ofEpochMilli(tmpAlert.getLong("endDate"))));
                    }
                }

                entitiesStoredRecords.add(new EntitiesStoredRecord(id, alerts, entityName, entityId, entityType, tags, score, severity, alertCount, alertClassifications, trendingScore));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return entitiesStoredRecords;
    }

}
