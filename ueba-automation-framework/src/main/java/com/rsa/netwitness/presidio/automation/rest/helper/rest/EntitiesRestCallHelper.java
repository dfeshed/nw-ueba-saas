package com.rsa.netwitness.presidio.automation.rest.helper.rest;

import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.client.RestAPI;
import com.rsa.netwitness.presidio.automation.rest.client.RestApiResponse;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;


public class EntitiesRestCallHelper implements IRestCallHelper{
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(EntitiesRestCallHelper.class.getName());

    public List<EntitiesStoredRecord> getEntities(ParametersUrlBuilder parametersUrlBuilder) {

        String URL = parametersUrlBuilder.toString();

        LOGGER.debug("Sending request: " + URL);
        RestApiResponse response = RestAPI.sendGet(URL);

        Assert.assertEquals(200, response.getResponseCode(),
                "Error with response code: " + response.getResponseCode() +
                        "\nURL: " + URL +
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

                List<AlertsStoredRecord> alerts = null;
                if(URL.toLowerCase().contains("expand=true")) {

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
                                tmpAlert.get("entityName").toString(),
                                indicatorsName, Integer.parseInt(tmpAlert.get("indicatorsNum").toString()),
                                tmpAlert.get("score").toString(), tmpAlert.get("feedback").toString(),
                                tmpAlert.get("entityScoreContribution").toString(), tmpAlert.get("timeframe").toString(),
                                tmpAlert.get("severity").toString(), tmpAlert.get("entityDocumentId").toString(),
                                tmpAlert.get("startDate").toString(), tmpAlert.get("endDate").toString()));
                    }
                }

                entitiesStoredRecords.add(new EntitiesStoredRecord(id, alerts, entityName, entityId, entityType, tags, score, severity, alertCount, alertClassifications));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return entitiesStoredRecords;
    }

}
