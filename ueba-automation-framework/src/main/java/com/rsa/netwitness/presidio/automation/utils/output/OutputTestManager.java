package com.rsa.netwitness.presidio.automation.utils.output;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rsa.netwitness.presidio.automation.context.AutomationConf;
import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.domain.output.*;
import com.rsa.netwitness.presidio.automation.rest.client.RestAPI;
import com.rsa.netwitness.presidio.automation.rest.client.RestApiResponse;
import com.rsa.netwitness.presidio.automation.ssh.SSHManager;
import com.rsa.netwitness.presidio.automation.ssh.TerminalCommands;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.ssh.RunCmdUtils.printLogFile;

public class OutputTestManager {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(OutputTestManager.class.getName());

    @Autowired
    private MongoTemplate mongoTemplate;

    private ExportDataFromMongoToJson exportToJSON;
    private ScoredEntityNormalizedUsernameConverter converter = new ScoredEntityNormalizedUsernameConverter();

    public List<ScoredEntityEventNormalizedUsernameStoredRecored> convertToStoredRecordsDaily(List<ScoredEntityEventNormalizedUsernameDailyStoredData> entities){
        return converter.convertDaily(entities);
    }

    private boolean importToMongoFromJSONFile(String path, String username, String collectionName, boolean drop){

        String com = "mongoimport --db presidio --collection " + collectionName + " --file " + path + "/" + username + ".txt  -u presidio -p P@ssw0rd";
        if(drop){
            return sendProcessCommand(com, path, "--drop");
        } else
            return sendProcessCommand(com, path, "");

    }

    private boolean sendProcessCommand(String command, String execPath, String... args){
        SSHManager.Response proc = TerminalCommands.runCommand(command,true, execPath, args);
        return proc.exitCode == 0;
    }

    public boolean updateAlertsDateTime(){

        String execPath = "/home/presidio/presidio-integration-test/presidio-integration-output-utils/src/test/resources/";
        String com = "mongo " + AutomationConf.UEBA_HOST + ":27017/presidio -u presidio -p P@ssw0rd " + execPath + "updateAlertsDateTime.js";
        return sendProcessCommand(com, execPath, "");
    }

    //java -cp /home/presidio/presidio-core/bin/presidio-output-processor-1.0.0-SNAPSHOT.jar -Dloader.main=presidio.output.processor.FortscaleOutputProcessorApplication org.springframework.boot.loader.PropertiesLauncher run --schema FILE --fixed_duration_strategy 3600.0 --start_date 2017-05-01T11:00:00Z --end_date 2017-07-31T12:00:00Z
    @Deprecated
    public void process(Instant startDate, Instant endDate, String smart_record_conf_name) {
        // store the data in the collections for data source
        String logFile = "/tmp/presidio-output-processor_run_" + smart_record_conf_name + "_" + startDate.toString() + "_" + endDate.toString() + ".log";

        SSHManager.Response p = TerminalCommands.runCommand(
                Consts.PRESIDIO_OUTPUT, true, Consts.PRESIDIO_DIR, "run" , "--start_date " + startDate,
                "--end_date " + endDate , "--smart_record_conf_name " + smart_record_conf_name + " " + " > " + logFile);

        printLogFile(logFile);
        Assert.assertEquals(0,p.exitCode, "Shell command failed. exit value: " + p.exitCode);
    }

    @Deprecated
    public void recalculate_user_score(Instant startDate, Instant endDate, String entity) {
        // store the data in the collections for data source
        String logFile = "/tmp/presidio-output_recalc_user_score_" + entity + "_" + startDate.toString() + "_" + endDate.toString() + ".log";

        SSHManager.Response p = TerminalCommands.runCommand(Consts.PRESIDIO_OUTPUT, true, Consts.PRESIDIO_DIR,
                "recalculate-entity-score", "--start_date " + startDate, "--end_date " + endDate ,
                " --fixed_duration_strategy 86400.0 " , " --smart_record_conf_name " + entity + "_hourly ",
                " --entity_type " + entity + " > " + logFile);

        printLogFile(logFile);
        Assert.assertEquals(0,p.exitCode, "Shell command failed. exit value: " + p.exitCode);
    }

    public int getAlertCount() {
        RestApiResponse response = RestAPI.sendGet("http://" + AutomationConf.UEBA_HOST + ":9200/presidio-output/alert/_count");
        JSONObject json;
        try {
            json = new JSONObject(response.getResultBody());
            return Integer.parseInt(json.get("count").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void importDB() {
        List<String> users = new ArrayList<>();
        File dir = new File(".");

        users = new ArrayList<>();
        users.add("dlpfileusr10");
        users.add("dlpfileusr26");
        users.add("maildlpusr6");
        users.add("dlpfileusr26");
        users.add("dlpfileusr26");

        boolean drop = true;
        for(String usr : users){
            String pathDaily = null;
            String pathHourly = null;
            try {
                pathDaily = dir.getCanonicalPath() + "/src/test/resources/inputScenarios/alerts/scored___entity_event__normalized_username_daily";
                pathHourly = dir.getCanonicalPath() + "/src/test/resources/inputScenarios/alerts/scored___entity_event__normalized_username_hourly";
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean success = importToMongoFromJSONFile(pathDaily,usr + "@somebigcompany.com", "ADE_SMARTS", drop);
            Assert.assertEquals(true, success, "fail to insert daily scored entities");
            drop = false;

            success = importToMongoFromJSONFile(pathHourly,usr + "@somebigcompany.com", "ADE_SMARTS", drop);
            Assert.assertEquals(true, success, "fail to insert hourly scored entities");
        }
    }

    // TODO: need to rewrite - JSONObject does not alow the same key in twice in the object
    public JSONObject sendGetAlertsURL(String additionalURLInfo, boolean printRequest) {
        if(!additionalURLInfo.startsWith("/")) {
            additionalURLInfo = "?" + additionalURLInfo;
        }
        String url = "http://" + AutomationConf.getOutputRestIpAndPort() + "/alerts" + additionalURLInfo;
        if(printRequest) System.out.println("Sending request: " + url);
        RestApiResponse response = RestAPI.sendGet(url);
        Assert.assertEquals(200, response.getResponseCode(), "Error with response code --> response code = " + response.getResponseCode());
        JSONObject json = null;
        try {
            json = new JSONObject(response.getResultBody());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject sendGetAlertStatusURL(long startRange, long endRange) {
        String url = "http://" + AutomationConf.getOutputRestIpAndPort() + "/alerts/statistics?start_range=" + startRange + "," + endRange;
        System.out.println("Sending request: " + url);
        RestApiResponse response = RestAPI.sendGet(url);
        Assert.assertEquals(200, response.getResponseCode(), "Error with response code --> response code = " + response.getResponseCode());
        JSONObject json = null;
        try {
            json = new JSONObject(response.getResultBody());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    public List<AlertsStoredRecord> getAlerts(){
        return getAlerts(null, false);
    }

    public List<AlertsStoredRecord> getAlerts(boolean printRequest){
        return getAlerts(null, printRequest);
    }

    public List<AlertsStoredRecord> getAlerts(String additionalUrlFlags){
        return getAlerts(additionalUrlFlags, false);
    }

    public List<AlertsStoredRecord> getAlerts(String additionalUrlFlags, boolean verbose){
        if(additionalUrlFlags == null){
            additionalUrlFlags = "";
        } else{
            if(!additionalUrlFlags.startsWith("/")){
                additionalUrlFlags = "?" + additionalUrlFlags;
            }
        }

        List<AlertsStoredRecord> alertsStoredRecords = null;
        RestApiResponse response;

        try {
            String url = "http://" + AutomationConf.getOutputRestIpAndPort() + "/alerts" + additionalUrlFlags;
            if (verbose) System.out.println("Sending request: " + url);
            response = RestAPI.sendGet(url);
            Assert.assertEquals(200, response.getResponseCode(), "Error with response code --> response code = " + response.getResponseCode());
            alertsStoredRecords = new ArrayList<>();

            JSONObject json ;

            json = new JSONObject(response.getResultBody());
            JSONArray arr = json.getJSONArray("alerts");
            for (int i = 0 ; i < arr.length() ; i++){
                JSONObject tmp = arr.getJSONObject(i);

                String id = tmp.get("id").toString();


                JSONArray probeArray = tmp.optJSONArray("classifiation");
                if (probeArray == null) {
                    System.out.println("Warning: missing classification for alert id: " + id);
                    continue;
                }
                String classificationSimpleString = probeArray.toString().replace("[","");
                Assert.assertTrue(classificationSimpleString.length() > 3);

                classificationSimpleString = classificationSimpleString.replace("]", "");
                classificationSimpleString = classificationSimpleString.substring(1, classificationSimpleString.length()-1);
                classificationSimpleString = classificationSimpleString.replace("\"", "");
                String[] classification = classificationSimpleString.split(",");

                String username = tmp.get("entityName").toString();

                String indicatorsNameSimpleString = tmp.getJSONArray("indicatorsName").toString().replace("[","");
                indicatorsNameSimpleString = indicatorsNameSimpleString.replace("]", "");
                indicatorsNameSimpleString = indicatorsNameSimpleString.substring(1, indicatorsNameSimpleString.length()-1);
                indicatorsNameSimpleString = indicatorsNameSimpleString.replace("\"", "");
                String[] indicatorsName = indicatorsNameSimpleString.split(",");

                Integer indicatorsNum = Integer.parseInt(tmp.get("indicatorsNum").toString());
                String score = tmp.get("score").toString();
                String feedback = tmp.get("feedback").toString();
                String userScoreContribution = tmp.get("entityScoreContribution").toString();
                String timeframe = tmp.get("timeframe").toString();
                String severity = tmp.get("severity").toString();
                String userId = tmp.get("entityDocumentId").toString();
                Instant startDate = Instant.ofEpochMilli(tmp.getLong("startDate"));;
                Instant endDate = Instant.ofEpochMilli(tmp.getLong("endDate"));;

                AlertsStoredRecord alert;
                if(additionalUrlFlags.toLowerCase().contains("expand=true")) {
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
        } catch (NullPointerException e) {
            if(alertsStoredRecords == null) {
                alertsStoredRecords = new ArrayList<>();
            } else {
                e.printStackTrace();
            }
        }
        return alertsStoredRecords;
    }

    /**
     *      Get users methods
     */

    public JSONObject sendGetEntitiesURL(String additionalURLInfo, boolean printRequest) {
        String url = "http://" + AutomationConf.getOutputRestIpAndPort() + "/entities/" + additionalURLInfo;
        if(printRequest) System.out.println("Sending request: " + url);
        RestApiResponse response = RestAPI.sendGet(url);
        Assert.assertEquals(200, response.getResponseCode(), "Error with response code --> response code = " + response.getResponseCode());
        JSONObject json = null;
        try {
            json = new JSONObject(response.getResultBody());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public RestApiResponse sendApiRequest(String queryStr) {
        String url = "http://" + AutomationConf.getOutputRestIpAndPort() + "/" + queryStr;
        System.out.println("Sending request: " + url);
        RestApiResponse response = RestAPI.sendGet(url);
        return response;
    }

    public RestApiResponse sendApiPostRequest(String queryStr, String messageBody, boolean verbose) {
        String url = "http://" + AutomationConf.getOutputRestIpAndPort() + "/" + queryStr;
        if (verbose) System.out.println("Sending request: " + url);
        RestApiResponse response = RestAPI.sendPost(url, messageBody);
        return response;
    }

    public RestApiResponse sendApiPostRequest(String queryStr, String messageBody) {
        return sendApiPostRequest(queryStr, messageBody, true);
    }

    public List<EntitiesStoredRecord> getEntities(boolean printRequest){
        return getEntities(null, printRequest);
    }

    public List<EntitiesStoredRecord> getEntities(String additionalUrlFlags) {
        return getEntities(additionalUrlFlags, false);
    }

    public List<EntitiesStoredRecord> getEntities(String additionalUrlFlags, boolean verbose) {
        if(additionalUrlFlags == null){
            additionalUrlFlags = "";
        } else{
            if(!additionalUrlFlags.startsWith("/")){
                additionalUrlFlags = "?" + additionalUrlFlags;
            }
        }

        String url = "http://" + AutomationConf.getOutputRestIpAndPort() + "/entities" + additionalUrlFlags;
        if(verbose) System.out.println("Sending get request: " + url);

        RestApiResponse response = null;
        response = RestAPI.sendGet(url);
        Assert.assertEquals(200, response.getResponseCode(), "Error with response code --> response code = " + response.getResponseCode());

        JSONObject json;
        List<EntitiesStoredRecord> entitiesStoredRecords = new ArrayList<>();
        boolean result = false;

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
                if(additionalUrlFlags.toLowerCase().contains("expand=true")) {

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
                                Instant.ofEpochMilli(tmpAlert.getLong("startDate")), Instant.ofEpochMilli(tmpAlert.getLong("endDate"))));
                    }
                }

                entitiesStoredRecords.add(new EntitiesStoredRecord(id, alerts, entityName, entityId, entityType, tags, score, severity, alertCount, alertClassifications));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return entitiesStoredRecords;
    }

    public JSONObject getAggregationData(String aggregateBy, String collection, boolean printRequest) {
        String url = "http://" + AutomationConf.getOutputRestIpAndPort() + "/" + collection + "?aggregateBy=" + aggregateBy;
        if(printRequest) System.out.println("Sending request: " + url);
        RestApiResponse response = RestAPI.sendGet(url);
        Assert.assertEquals(200, response.getResponseCode(), "Error with response code --> response code = " + response.getResponseCode());

        JSONObject json;
        try {
            json = new JSONObject(response.getResultBody());
            if(json.has("aggregationData")) {
                return json.getJSONObject("aggregationData");
            }
        } catch (Exception e) {
            System.out.println("Exception while parsing response for aggregation data: " + e.getLocalizedMessage());
        }
        return null;
    }

    private JSONObject getOutputProcessorDefault(){
        String url = "http://" + AutomationConf.UEBA_HOST + ":8888/output-processor/default";
        RestApiResponse defaults = RestAPI.sendGet(url);
        Assert.assertEquals(200, defaults.getResponseCode(), "Error with response code --> response code = " + defaults.getResponseCode());
        JSONObject json = null;

        try{
            json = new JSONObject(defaults.getResultBody());
        } catch(Exception e){
            e.printStackTrace();
        }

        return json;
    }

    public JSONObject getuserScoreThresholdFromElasticSearch(){
        String url = "http://" + AutomationConf.UEBA_HOST + ":9200/presidio-output-user-severities-range/_search";
        RestApiResponse defaults = RestAPI.sendGet(url);
        JSONObject json = null;

        try{
            json = new JSONObject(defaults.getResultBody());
        } catch(Exception e){
            e.printStackTrace();
        }

        return json;
    }

    public String getSmartThresholdScore(){
        JSONObject defaults = getOutputProcessorDefault();
        String thresholdScore = null;
        try {
            JSONArray propertySources = defaults.getJSONArray("propertySources");
            for(int i=0 ; i < propertySources.length() ; i++){
                String name = propertySources.getJSONObject(i).getString("name");
                if(name.contains("output-processor.properties")){
                    thresholdScore = propertySources.getJSONObject(i).getJSONObject("source").getString("smart.threshold.score");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return thresholdScore;
    }


    private String buildIndicatorRequest(String alertId, String indicatorId, boolean expand) {
        return "http://" + AutomationConf.getOutputRestIpAndPort() +
                "/alerts/" + alertId +
                "/indicators/" + indicatorId;
    }

    public IndicatorsStoredRecord getIndicator(String alertId, String indicatorId){
        Type type = new TypeToken<IndicatorsStoredRecord>() {}.getType();

        RestApiResponse response = RestAPI.sendGet(buildIndicatorRequest(alertId, indicatorId, false));
        String jsonStr = response.getResultBody();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        return gson.fromJson(jsonStr, type);
    }


    public ResponseEntity<String> patch(URI uri, String updateBody) {

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate patchRestTemplate = new RestTemplate(factory);

        ResponseEntity<String> responseEntity =
                patchRestTemplate.exchange(uri, HttpMethod.PATCH, getPostRequestHeaders(updateBody), String.class);

        return responseEntity;
    }

    public ResponseEntity<String> patchAlert(String alertId, String updateBody) {

        URI uri = URI.create("http://" + AutomationConf.getOutputRestIpAndPort() + "/alerts/" + alertId);
        ResponseEntity<String> responseEntity = patch(uri, updateBody);
        return responseEntity;
    }

    public ResponseEntity<String> patchAlertComment(String alertId, String commentId, String updateBody) {

        URI uri = URI.create("http://" + AutomationConf.getOutputRestIpAndPort() + "/alerts/" + alertId + "/comments/" + commentId);
        ResponseEntity<String> responseEntity = patch(uri, updateBody);
        return responseEntity;
    }

    private HttpEntity getPostRequestHeaders(String jsonPostBody) {
        List<MediaType> acceptTypes = new ArrayList<>();
        acceptTypes.add(MediaType.APPLICATION_JSON);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        reqHeaders.setAccept(acceptTypes);

        return new HttpEntity<>(jsonPostBody, reqHeaders);
    }

    public String getIndicatorId(AlertsJsonRecord alert, String indicator_name) {
        for (IndicatorsStoredRecord indicator: alert.getIndicators()) {
            if (indicator.getName().equalsIgnoreCase(indicator_name)) return indicator.getId();
        }
        return null;
    }

    private String buildEntityRequest(String entityName) {
        String requestUrlStr = "http://" + AutomationConf.getOutputRestIpAndPort() +
                "/entities?expand=true&freeText=" + entityName +
                "&isPrefix=false&pageNumber=0&pageSize=10&sortDirection=ASC";
        System.out.println(requestUrlStr);
        return requestUrlStr;
    }

    private String buildUserAlertsRequest(String userName) {
        String requestUrlStr =  "http://" + AutomationConf.getOutputRestIpAndPort() +
                "/alerts?sortDirection=DESC&sortFieldNames=START_DATE&userName=" + userName;
        System.out.println(requestUrlStr);
        return requestUrlStr;
    }

    private String buildAlertRequest(String alertId) {
        String requestUrlStr =  "http://" + AutomationConf.getOutputRestIpAndPort() +
                "/alerts/" + alertId + "?expand=true";
        System.out.println(requestUrlStr);
        return requestUrlStr;
    }

    private String buildIndicatorRequest(String alertId, String indicatorId) {
        String requestUrlStr =  "http://" + AutomationConf.getOutputRestIpAndPort() +
                "/alerts/" + alertId +
                "/indicators/" + indicatorId + "?expand=true";
        System.out.println(requestUrlStr);
        return requestUrlStr;
    }

    private String buildIndicatorEventsRequest(String alertId, String indicatorId) {
        String requestUrlStr =  "http://" + AutomationConf.getOutputRestIpAndPort() +
                "/alerts/" + alertId +
                "/indicators/" + indicatorId + "/events";
        System.out.println(requestUrlStr);
        return requestUrlStr;
    }

    public EntitiesJsonObject getExpectedEntities(String entityName){

        RestApiResponse response = RestAPI.sendGet(buildEntityRequest(entityName));
        Assert.assertEquals(200, response.getResponseCode());
        String jsonStr = response.getResultBody();

        Gson gson = new Gson();
        EntitiesJsonObject user = gson.fromJson(jsonStr, EntitiesJsonObject.class);
        Assert.assertNotNull(user);
        return user;
    }

    public AlertsJsonObject getUserAlerts(String userName){

        RestApiResponse response = RestAPI.sendGet(buildUserAlertsRequest(userName));
        Assert.assertEquals(200, response.getResponseCode());
        String jsonStr = response.getResultBody();

        Gson gson = new Gson();
        AlertsJsonObject alerts = gson.fromJson(jsonStr, AlertsJsonObject.class);
        Assert.assertNotNull(alerts);
        return alerts;
    }

    public AlertsJsonRecord getAlertRecord(String alertId){

        RestApiResponse response = RestAPI.sendGet(buildAlertRequest(alertId));
        Assert.assertEquals(200, response.getResponseCode());
        String jsonStr = response.getResultBody();

        Gson gson = new Gson();
        AlertsJsonRecord alerts = gson.fromJson(jsonStr, AlertsJsonRecord.class);
        Assert.assertNotNull(alerts);
        return alerts;
    }

    public IndicatorsStoredRecord getIndicator(String alertId, String indicatorId, Type type){
        RestApiResponse response = RestAPI.sendGet(buildIndicatorRequest(alertId, indicatorId));
        String jsonStr = response.getResultBody();
        Assert.assertEquals(200, response.getResponseCode());

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        return gson.fromJson(jsonStr, type);
    }

    public String getIndicatorEvents(String alertId, String indicatorId){
        RestApiResponse response = RestAPI.sendGet(buildIndicatorEventsRequest(alertId, indicatorId));
        Assert.assertEquals(200, response.getResponseCode());

        return response.getResultBody();
    }
}
