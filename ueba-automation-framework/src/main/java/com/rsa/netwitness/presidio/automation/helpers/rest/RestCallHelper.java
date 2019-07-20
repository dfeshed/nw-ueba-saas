package com.rsa.netwitness.presidio.automation.helpers.rest;

import com.rsa.netwitness.presidio.automation.common.rest.RestAPI;
import com.rsa.netwitness.presidio.automation.common.rest.RestApiResponse;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;


public class RestCallHelper extends RestBase {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(RestCallHelper.class.getName());

    RestCallHelper(String url) {
        this.URL = url;
    }

    public List<AlertsStoredRecord> getAlerts(ParametersUrlBuilder alertsParametersUrlBuilder) {
        String URL = alertsParametersUrlBuilder.toString();

        List<AlertsStoredRecord> alertsStoredRecords = new ArrayList<>();
        RestApiResponse response;

        try {
            LOGGER.debug("Sending request: " + URL);
            response = RestAPI.sendGet(URL);

            Assert.assertEquals(200, response.getResponseCode(),
                    "Error with response code: " + response.getResponseCode() +
                            "\nURL: " + URL +
                            "\nError message: " + response.getErrorMessage());

            LOGGER.debug(response.getResultBody());
            JSONObject json;

            json = new JSONObject(response.getResultBody());
            JSONArray arr = json.getJSONArray("alerts");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject tmp = arr.getJSONObject(i);

                String id = tmp.get("id").toString();


                JSONArray probeArray = tmp.optJSONArray("classifiation");
                if (probeArray == null) {
                    System.out.println("Warning: missing classification for alert id: " + id);
                    continue;
                }
                String classificationSimpleString = probeArray.toString().replace("[", "");
                Assert.assertTrue(classificationSimpleString.length() > 3);

                classificationSimpleString = classificationSimpleString.replace("]", "");
                classificationSimpleString = classificationSimpleString.substring(1, classificationSimpleString.length() - 1);
                classificationSimpleString = classificationSimpleString.replace("\"", "");
                String[] classification = classificationSimpleString.split(",");

                String username = tmp.get("entityName").toString();

                String indicatorsNameSimpleString = tmp.getJSONArray("indicatorsName").toString().replace("[", "");
                indicatorsNameSimpleString = indicatorsNameSimpleString.replace("]", "");
                indicatorsNameSimpleString = indicatorsNameSimpleString.substring(1, indicatorsNameSimpleString.length() - 1);
                indicatorsNameSimpleString = indicatorsNameSimpleString.replace("\"", "");
                String[] indicatorsName = indicatorsNameSimpleString.split(",");

                Integer indicatorsNum = Integer.parseInt(tmp.get("indicatorsNum").toString());
                String score = tmp.get("score").toString();
                String feedback = tmp.get("feedback").toString();
                String userScoreContribution = tmp.get("entityScoreContribution").toString();
                String timeframe = tmp.get("timeframe").toString();
                String severity = tmp.get("severity").toString();
                String userId = tmp.get("entityDocumentId").toString();
                String startDate = tmp.get("startDate").toString();
                String endDate = tmp.get("endDate").toString();

                AlertsStoredRecord alert;
                if (URL.toLowerCase().contains("expand=true")) {
                    JSONArray indicators = tmp.getJSONArray("indicators");
                    alert = new AlertsStoredRecord(id, classification, username, indicatorsName, indicatorsNum, score,
                            feedback, userScoreContribution, timeframe, severity, userId, indicators, startDate, endDate);
                } else {
                    alert = new AlertsStoredRecord(id, classification, username, indicatorsName, indicatorsNum, score,
                            feedback, userScoreContribution, timeframe, severity, userId, startDate, endDate);
                }


                alertsStoredRecords.add(alert);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (alertsStoredRecords.isEmpty()) {
            LOGGER.warn("Empty alerts list return for request " + URL);
        }

        return alertsStoredRecords;
    }


    public RestApiResponse getRestApiResponse(ParametersUrlBuilder alertsParametersUrlBuilder) {
        String URL = alertsParametersUrlBuilder.toString();
        LOGGER.debug("Sending request: " + URL);
        return RestAPI.sendGet(URL);
    }

}
