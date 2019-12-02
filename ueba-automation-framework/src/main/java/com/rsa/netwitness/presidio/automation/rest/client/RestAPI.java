package com.rsa.netwitness.presidio.automation.rest.client;

import com.rsa.netwitness.presidio.automation.config.AutomationConf;
import org.apache.commons.net.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class RestAPI {
    public static final String USERNAME = "presidio";
    public static final String PASSWORD = "P@ssw0rd";
    public static final String COLLECTOR_JSON_FILE = "/home/presidio/presidio-core/configurations/collector.json";
//    public static final String CONFIGURATION_REQ_HTTP = "http://" + AutomationConf.UEBA_HOST + ":8080/presidio-manager/configuration";
    public static final String CONFIGURATION_REQ_HTTP = "http://" + AutomationConf.UEBA_IP + ":8080/presidio-manager/configuration";
//    public static final String CONFIGURATION_REQ_HTTPS = "https://" + AutomationConf.UEBA_HOST + "/configuration";
    public static final String CONFIGURATION_REQ_HTTPS = "https://" + AutomationConf.UEBA_IP + "/configuration";


    private static String defaultAuthorizationID;
    private static String messageBodyConfiguration;
    public static String webhookUrl;
    public static String heartbeatUrl;

    public static String getMessageBodyConfiguration() {
        return messageBodyConfiguration;
    }

    public static RestApiResponse sendPatch(String urlAddress, String messageBody) {
        RestApiResponse response = new RestApiResponse();
        URL url;
        HttpURLConnection conn;

        try {
            url = new URL(urlAddress);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");

            OutputStream os = conn.getOutputStream();
            os.write(messageBody.getBytes());
            os.flush();
            os.close();

            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));

            conn.disconnect();
        }  catch (IOException e) {
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    public static RestApiResponse sendPost(String urlAddress, String messageBody) {
        RestApiResponse response = new RestApiResponse();
        URL url;
        HttpURLConnection conn;

        try {
            url = new URL(urlAddress);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("AuthorizationId", defaultAuthorizationID);

            OutputStream os = conn.getOutputStream();
            os.write(messageBody.getBytes());
            os.flush();
            os.close();

            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));

            conn.disconnect();
        }  catch (IOException e) {
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    public static RestApiResponse sendPostElasticSearch(String urlAddress, String messageBody) {
        RestApiResponse response = new RestApiResponse();
        URL url;
        HttpURLConnection conn;

        try {
            url = new URL(urlAddress);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(messageBody.getBytes());
            os.flush();
            os.close();

            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));

            conn.disconnect();
        }  catch (IOException e) {
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    public static RestApiResponse sendHttpsPost(String urlAddress, String messageBody) {
        RestApiResponse response = new RestApiResponse();
        URL url;

        disableSSL();

        HttpsURLConnection conn;
        //System.out.println("Sending POST" + " request: " + urlAddress);
        try {
            url = new URL(urlAddress);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("AuthorizationId", defaultAuthorizationID);


            String user_pass = USERNAME + ":" + PASSWORD;
            String encoded = Base64.encodeBase64String(user_pass.getBytes());
            encoded = encoded.replace("\n", "").replace("\r", "");
            conn.setRequestProperty("Authorization", "Basic " + encoded);


            OutputStream os = conn.getOutputStream();
            os.write(messageBody.getBytes());
            os.flush();
            os.close();

            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));

            conn.disconnect();

        }  catch (IOException e) {
            response.setErrorMessage(e.getMessage());
            System.out.println("Warning: Exception in send HTTPS post. Error: " + e.getMessage());
        }

        return response;
    }

    private static void disableSSL() {
        TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) { System.out.println("Warning: bypassing SSL. Error message: " + e.getMessage());}
    }

    public static RestApiResponse sendHttpsPostCleanAndRun(String urlAddress) {
        RestApiResponse response = new RestApiResponse();
        URL url;

        disableSSL();

        HttpsURLConnection conn;

        try {
            url = new URL(urlAddress);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            String user_pass = USERNAME + ":" + PASSWORD;
            String encoded = Base64.encodeBase64String(user_pass.getBytes());
            encoded = encoded.replace("\n", "").replace("\r", "");
            conn.setRequestProperty("Authorization", "Basic " + encoded);


            OutputStream os = conn.getOutputStream();
            os.flush();
            os.close();

            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));

            conn.disconnect();

        }  catch (IOException e) {
            response.setErrorMessage(e.getMessage());
            System.out.println("Warning: Exception in send HTTPS post. Error: " + e.getMessage());
        }

        return response;
    }

    public static RestApiResponse sendGet(String urlAddress) {
        RestApiResponse response = null;
        URL url;
        HttpURLConnection conn;
//        System.out.println("Sending GET request: " + urlAddress);
        try {
            url = new URL(urlAddress);
            conn = buildRestRequest(url, "GET", null);
            response = createResponse(conn);

            conn.disconnect();

        } catch(IOException e){
            if(response != null){
                response.setErrorMessage(e.getMessage());
            }
            System.err.println(e.getMessage());
        }

        return response;
    }

    public static RestApiResponse sendHttpsGet(String urlAddress) {
        RestApiResponse response = null;
        URL url;
        HttpsURLConnection conn;
//        System.out.println("Sending GET request: " + urlAddress);
        disableSSL();
        try {
            url = new URL(urlAddress);
            conn = buildHttpsRestRequest(url, "GET", null);
            response = createHttpsResponse(conn);

            conn.disconnect();

        } catch(IOException e){
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    public static RestApiResponse sendDelete(String urlAddress){
        RestApiResponse response = null;
        HttpURLConnection conn;
        URL url;

        try{
            url = new URL(urlAddress);
            conn = buildRestRequest(url,"DELETE", null);
            response = createResponse(conn);

            conn.disconnect();
        } catch (IOException e) {
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    private static HttpURLConnection buildRestRequest(URL url, String restMethod, String authorizationID) throws IOException{
        HttpURLConnection conn;

        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(100000);
        conn.setConnectTimeout(100000);
        conn.setDoOutput(true);
        conn.setRequestMethod(restMethod);

        if(!restMethod.equals("GET") && !restMethod.equals("DELETE")) {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("AuthorizationId", authorizationID);
        }

        return conn;
    }

    private static HttpsURLConnection buildHttpsRestRequest(URL url, String restMethod, String authorizationID) throws IOException{
        HttpsURLConnection conn;

        conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(100000);
        conn.setConnectTimeout(100000);
        conn.setDoOutput(true);
        conn.setRequestMethod(restMethod);

        String user_pass = USERNAME + ":" + PASSWORD;
        String encoded = Base64.encodeBase64String(user_pass.getBytes());
        encoded = encoded.replace("\n", "").replace("\r", "");
        conn.setRequestProperty("Authorization", "Basic " + encoded);

        if(!restMethod.equals("GET")) {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("AuthorizationId", authorizationID);
        }

        return conn;
    }

    private static RestApiResponse createResponse(HttpURLConnection con) throws IOException{
        RestApiResponse response = new RestApiResponse();

        response.setResponseCode(con.getResponseCode());
        Assert.assertTrue(response.getResponseCode() < 300 || response.getResponseCode() == 404,
                con.getURL() + "\nResponse code: " + response.getResponseCode() + ".");

        response.setResponseMessage(con.getResponseMessage());

        InputStream in = new BufferedInputStream(con.getInputStream());
        response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));

        return response;
    }

    private static RestApiResponse createHttpsResponse(HttpsURLConnection con) throws IOException{
        RestApiResponse response = new RestApiResponse();

        response.setResponseCode(con.getResponseCode());
        Assert.assertTrue(response.getResponseCode() < 300 || response.getResponseCode() == 404, "Response code: " + response.getResponseCode() + ".");

        response.setResponseMessage(con.getResponseMessage());

        InputStream in = new BufferedInputStream(con.getInputStream());
        response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));

        return response;
    }

    private static RestApiResponse sendRestRequest(String urlAddress, String body, String requestMethod){
        RestApiResponse response = new RestApiResponse();
        URL url;
        HttpURLConnection conn;
//        System.out.println("Sending " + requestMethod + " request: " + urlAddress);
        try {
            url = new URL(urlAddress);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setDoOutput(true);
            conn.setRequestMethod(requestMethod.toUpperCase());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            os.close();

            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));

            conn.disconnect();

        }  catch (IOException e) {
            response.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    private static RestApiResponse sendHttpsRestRequest(String urlAddress, String body, String requestMethod){
        RestApiResponse response = new RestApiResponse();
        URL url;

        TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) { }

        HttpsURLConnection conn;
//        System.out.println("Sending " + requestMethod + " request: " + urlAddress);
        try {
            url = new URL(urlAddress);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setDoOutput(true);
            conn.setRequestMethod(requestMethod.toUpperCase());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            String user_pass = USERNAME + ":" + PASSWORD;
            String encoded = Base64.encodeBase64String(user_pass.getBytes());
            encoded = encoded.replace("\n", "").replace("\r", "");
            conn.setRequestProperty("Authorization", "Basic " + encoded);

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            os.close();

            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));

            conn.disconnect();

        }  catch (IOException e) {
            response.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    public static RestApiResponse sendRegister(Map<String, Integer> schemaAndDelay) {
        JSONObject body = new JSONObject();
        JSONArray dataAvailbilityConf = new JSONArray();
        for(Map.Entry<String,Integer> schema : schemaAndDelay.entrySet()){
            try {
                JSONObject schemas = new JSONObject();
                schemas.put("schema", schema.getKey().toUpperCase().replace(" ", "_"));
                schemas.put("delay", schema.getValue());
                dataAvailbilityConf.put(schemas);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject webhookConf = new JSONObject();
        try {
            webhookConf.put("batchSize", 1000);
            webhookConf.put("heartbeatInterval", 1);
            webhookConf.put("notificationInterval", 5);
            webhookConf.put("notificationUrl", "/api/v1.0/webhook");
            webhookConf.put("subscriptionId", "<subscription Id>");

            JSONObject connector = new JSONObject();
            connector.put("connectorStrategy", "quest-webhook");
            connector.put("dataAvailabilityStrategy", "time-heuristic");
            connector.put("dataAvailbilityConf", dataAvailbilityConf);
            connector.put("webhookConf", webhookConf);

            body.put("connector", connector);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestApiResponse response = sendRestRequest("http://" + AutomationConf.UEBA_HOST + ":8080/presidio-ca-connector-webapp/webhook/register", body.toString(), "POST");
        JSONObject json;
        try {
            json = new JSONObject(response.getResultBody());
            defaultAuthorizationID = json.get("authorizationId").toString();
            System.out.println(defaultAuthorizationID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static RestApiResponse sendHttpsRegister(Map<String, Integer> schemaAndDelay) {
        JSONObject body = new JSONObject();
        JSONArray dataAvailbilityConf = new JSONArray();
        for(Map.Entry<String,Integer> schema : schemaAndDelay.entrySet()){
            try {
                JSONObject schemas = new JSONObject();
                schemas.put("schema", schema.getKey().toUpperCase().replace(" ", "_"));
                schemas.put("delay", schema.getValue());
                dataAvailbilityConf.put(schemas);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject webhookConf = new JSONObject();
        try {
            webhookConf.put("batchSize", 1000);
            webhookConf.put("heartbeatInterval", 1);
            webhookConf.put("notificationInterval", 5);
            webhookConf.put("notificationUrl", "/api/v1.0/webhook");
            webhookConf.put("subscriptionId", "<subscription Id>");

            JSONObject connector = new JSONObject();
            connector.put("connectorStrategy", "quest-webhook");
            connector.put("dataAvailabilityStrategy", "time-heuristic");
            connector.put("dataAvailbilityConf", dataAvailbilityConf);
            connector.put("webhookConf", webhookConf);

            body.put("connector", connector);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestApiResponse response = sendHttpsRestRequest("https://" + AutomationConf.UEBA_HOST + "/connector/webhook/register", body.toString(), "POST");
        JSONObject json;
        try {
            json = new JSONObject(response.getResultBody());
            webhookUrl = json.get("notificationUrl").toString();
            heartbeatUrl = json.get("heartbeatUrl").toString();
            defaultAuthorizationID = json.get("authorizationId").toString();
            System.out.println(defaultAuthorizationID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static RestApiResponse sendConfiguration(List<String> schema, int startDaysBack) {

        JSONObject body = new JSONObject();
        JSONArray schemas = new JSONArray();

        JSONObject system = new JSONObject();
        JSONObject dataPipeline = new JSONObject();

        String hostnameWithoutDomain = AutomationConf.UEBA_HOST;
        hostnameWithoutDomain = hostnameWithoutDomain.substring(0, hostnameWithoutDomain.indexOf(".fortscale.dom"));
        try {
            system.put("username", hostnameWithoutDomain + "@fortscale.dom");
            system.put("password", PASSWORD);
            system.put("ldapUrl", "ldap://192.168.0.31/DC=FORTSCALE,DC=DOM?userPrincipalName?sub");
            system.put("realmName", "FORTSCALE.DOM");
            system.put("analystGroup", "CN=VPN-Users,OU=Fortscale-Users,DC=Fortscale,DC=dom");
            system.put("krbServiceName", "HTTP/"+ AutomationConf.UEBA_HOST + "@FORTSCALE.DOM");
            system.put("smtpHost", "name.of-server.com:25");

            for(String sch : schema) {
                schemas.put(sch);
            }

            dataPipeline.put("schemas", schemas);

            Instant instant = Instant.now();
            instant = instant.minus(startDaysBack, ChronoUnit.DAYS);
            Instant instant0_17AM = instant.truncatedTo(ChronoUnit.DAYS).plus(17,ChronoUnit.MINUTES);

            dataPipeline.put("startTime", instant0_17AM);

            body.put("system", system);
            body.put("dataPipeline", dataPipeline);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestApiResponse response = sendRestRequest("http://" + AutomationConf.UEBA_HOST + ":8080/presidio-manager/configuration", body.toString(), "PUT");
        validationPostRequest(response);
        return response;
    }

    public static RestApiResponse sendHttpsConfiguration(List<String> schema, int startDaysBack) {

        JSONObject body = new JSONObject();
        JSONArray schemas = new JSONArray();

        JSONObject system = new JSONObject();
        JSONObject dataPipeline = new JSONObject();

        String hostnameWithoutDomain = AutomationConf.UEBA_HOST;
        hostnameWithoutDomain = hostnameWithoutDomain.substring(0, hostnameWithoutDomain.indexOf(".fortscale.dom"));
        try {
            system.put("username", hostnameWithoutDomain + "@fortscale.dom");
            system.put("password", PASSWORD);
            system.put("ldapUrl", "ldap://192.168.0.31/DC=FORTSCALE,DC=DOM?userPrincipalName?sub");
            system.put("realmName", "FORTSCALE.DOM");
            system.put("analystGroup", "CN=VPN-Users,OU=Fortscale-Users,DC=Fortscale,DC=dom");
            system.put("krbServiceName", "HTTP/"+ AutomationConf.UEBA_HOST + "@FORTSCALE.DOM");
            system.put("smtpHost", "name.of-server.com:25");

            for(String sch : schema) {
                schemas.put(sch);
            }

            dataPipeline.put("schemas", schemas);

            Instant instant = Instant.now();
            instant = instant.minus(startDaysBack, ChronoUnit.DAYS);
            Instant instant0_17AM = instant.truncatedTo(ChronoUnit.DAYS).plus(17,ChronoUnit.MINUTES);

            dataPipeline.put("startTime", instant0_17AM);

            body.put("system", system);
            body.put("dataPipeline", dataPipeline);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        messageBodyConfiguration = body.toString();
        RestApiResponse response = sendHttpsRestRequest("https://" + AutomationConf.UEBA_HOST + "/configuration", body.toString(), "PUT");
        validationPostRequest(response);
        return response;
    }

    public static RestApiResponse sendHttpsConfiguration(List<String> schema, Instant startDate) {

        JSONObject body = new JSONObject();
        JSONArray schemas = new JSONArray();

        JSONObject system = new JSONObject();
        JSONObject dataPipeline = new JSONObject();

        String hostnameWithoutDomain = AutomationConf.UEBA_HOST;
        hostnameWithoutDomain = hostnameWithoutDomain.substring(0, hostnameWithoutDomain.indexOf(".fortscale.dom"));
        try {
            system.put("username", hostnameWithoutDomain + "@fortscale.dom");
            system.put("password", PASSWORD);
            system.put("ldapUrl", "ldap://192.168.0.31/DC=FORTSCALE,DC=DOM?userPrincipalName?sub");
            system.put("realmName", "FORTSCALE.DOM");
            system.put("analystGroup", "CN=VPN-Users,OU=Fortscale-Users,DC=Fortscale,DC=dom");
            system.put("krbServiceName", "HTTP/"+ AutomationConf.UEBA_HOST + "@FORTSCALE.DOM");
            system.put("smtpHost", "name.of-server.com:25");

            for(String sch : schema) {
                schemas.put(sch);
            }

            dataPipeline.put("schemas", schemas);

            dataPipeline.put("startTime", startDate);

            body.put("system", system);
            body.put("dataPipeline", dataPipeline);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestApiResponse response = sendHttpsRestRequest("https://" + AutomationConf.UEBA_HOST + "/configuration", body.toString(), "PUT");
        validationPostRequest(response);
        return response;
    }

    public static RestApiResponse sendHttpsLogPointConfiguration(List<String> schema, Instant startDate) {

        JSONObject parent = new JSONObject();
        JSONObject connector = new JSONObject();
        JSONArray schemas = new JSONArray();

        JSONArray repos = new JSONArray();
        JSONArray queryArray = new JSONArray();
        JSONObject queryObject = new JSONObject();
        JSONObject output = new JSONObject();
        JSONObject dataPipeline = new JSONObject();

        try {
            connector.put("serverUrl", "fs-logpoint-01");
            connector.put("secretKey", "c39572f74f51f2ced4cfa707a2fa3ae5");
            connector.put("username", "admin");

            repos.put("127.0.0.1:5504/ueba_repo");
            connector.put("repos", repos);

            for(String sch : schema) {
                queryObject.put("schema", sch);
                queryObject.put("query", "norm_id=WinServer2008 event_source=\\\"Microsoft-Windows-Security-Auditing\\\" (event_id=4624 logon_process=User32) OR (event_id=4769 -service=*$  -service=krbtgt service=*) OR event_id in [4625, 4768, 4771, 4776] | rename norm_id as dataSource,logon_guid as userId,user_id as userId, caller_sid as userId, target_sid as userId, authentication_type as operationType, host as machineName, source_address as srcMachineId, target_domain as dstMachineDomain, domain as dstMachineDomain, sub_status_code as resultCode, result_code as resultCode, failure_code as resultCode, status_code as resultCode,error_code as resultCode | fields dataSource, userId, operationType, srcMachineId, dstMachineDomain, machineName, resultCode, result");
                queryArray.put(queryObject);
            }

            output.put("syslogHost_A", "name.of-server.com,601");
            output.put("severityLevel_A", "alert");
            output.put("facilityCode_A", "local0");
            output.put("syslogHost_B", "name.of-server.com,602");
            output.put("severityLevel_B", "alert");
            output.put("facilityCode_B", "local1");

            for(String sch : schema) {
                schemas.put(sch);
            }

            dataPipeline.put("schemas", schemas);
            dataPipeline.put("startTime", startDate);

            parent.put("connector", connector);
            parent.put("query", queryArray);
            parent.put("output", output);
            parent.put("dataPipeline", dataPipeline);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestApiResponse response = sendHttpsRestRequest("https://" + AutomationConf.UEBA_HOST + "/configuration", parent.toString(), "PUT");
        validationPostRequest(response);
        return response;
    }

    public static RestApiResponse sendKeytabFileRestRequest(String urlAddress){
        RestApiResponse response = new RestApiResponse();
        String keytabFile = "/home/presidio/krb5.keytab";
        File keytabFileToUpload = new File(keytabFile);
        String boundary =  "";
        URL url;

        TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) { }

        HttpsURLConnection conn;
//        System.out.println("Sending POST" + " request: " + urlAddress);
        try {
            url = new URL(urlAddress);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(100000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            String user_pass = USERNAME + ":" + PASSWORD;
            String encoded = Base64.encodeBase64String(user_pass.getBytes());
            encoded = encoded.replace("\n", "").replace("\r", "");
            conn.setRequestProperty("Authorization", "Basic " + encoded);

            OutputStream os = conn.getOutputStream();

            BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(os));
            FileInputStream inputStreamToLogFile = new FileInputStream(keytabFileToUpload);
            int bytesRead;
            byte[] dataBuffer = new byte[1024];
            while((bytesRead = inputStreamToLogFile.read(dataBuffer)) != -1) {
                os.write(dataBuffer, 0, bytesRead);
            }

            os.flush();
            httpRequestBodyWriter.flush();
            os.close();
            httpRequestBodyWriter.close();

            response.setResponseCode(conn.getResponseCode());
            response.setResponseMessage(conn.getResponseMessage());

            /*InputStream in = new BufferedInputStream(conn.getInputStream());
            response.setResultBody(org.apache.commons.io.IOUtils.toString(in, "UTF-8"));*/

            conn.disconnect();

        }  catch (IOException e) {
            response.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    public static void setAuthorizationID(String authorizationID){
        defaultAuthorizationID = authorizationID;
    }

    public static void setWebhookUrl(String webhookUrl) {
        RestAPI.webhookUrl = webhookUrl;
    }

    public static void setHeartbeatUrl(String heartbeatUrl) {
        RestAPI.heartbeatUrl = heartbeatUrl;
    }

    public static String getAuthorizationID() {
        return defaultAuthorizationID;
    }

    private static void validationPostRequest(RestApiResponse response) {
        if (response.getResponseCode() < 200 || response.getResponseCode() > 300
                && response.getResponseCode() != 0 && response.getErrorMessage() != null)  {
            Assert.fail("REST API - POST request got error code: " + response.getResponseCode() +
                    "\nResponse Message: " + response.getResponseMessage() +
                    "\nError Message: " + response.getErrorMessage() +
                    "\nResult Body: " + response.getResultBody());
        }
    }

    public static String buildHeartbeatEvent(Instant startTime) {
        JSONObject parentJson = new JSONObject();
        JSONObject childJson = new JSONObject();

        try {
            parentJson.put("heartbeat", childJson);
            childJson.put("subscriptionId", "d0742d52-5bc1-4a69-8edc-886226defaf1");
            childJson.put("timeSent", Instant.now());
            childJson.put("time", startTime);
            childJson.put("sender", "FS-EPO");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parentJson.toString();
    }

    public static String getCurrentAuthorizationId() {
        String collectorJsonStr = null;
        String authorizationId = null;
        try {
            collectorJsonStr = new String(Files.readAllBytes(Paths.get(COLLECTOR_JSON_FILE)));
        } catch (IOException e) {
            Assert.fail("Can't read collector properties file: " + COLLECTOR_JSON_FILE);
        }

        try {
            JSONObject jsonObj = new JSONObject(collectorJsonStr);
            authorizationId = jsonObj.getString("authorizationId");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return authorizationId;
    }

    public static void setup() {
        /** for online events generation, when sending REST requests on same machine **/
        String webhookURL = "https://" + AutomationConf.UEBA_HOST + "/api/v1.0/webhook";
        String heartBeatURL = "https://" + AutomationConf.UEBA_HOST + "/api/v1.0/heartbeat";
        String authorizationID = RestAPI.getCurrentAuthorizationId();
        RestAPI.setWebhookUrl(webhookURL);
        RestAPI.setHeartbeatUrl(heartBeatURL);
        RestAPI.setAuthorizationID(authorizationID);

        System.out.println("\nStarting events generation:\n");
        System.out.println("Webhook URL: " + webhookURL);
        System.out.println("HeartBeat URL: " + heartBeatURL);
        System.out.println("AuthorizationID: " + authorizationID);
    }

    public static void setup(String authorizationID) {
        /** for online events generation, when sending REST requests on any machine **/
        String webhookURL = "https://" + AutomationConf.UEBA_HOST + "/api/v1.0/webhook";
        String heartBeatURL = "https://" + AutomationConf.UEBA_HOST + "/api/v1.0/heartbeat";
        RestAPI.setWebhookUrl(webhookURL);
        RestAPI.setHeartbeatUrl(heartBeatURL);
        RestAPI.setAuthorizationID(authorizationID);

        System.out.println("\nEvents generation - run environment :\n");
        System.out.println("Webhook URL: " + webhookURL);
        System.out.println("HeartBeat URL: " + heartBeatURL);
        System.out.println("AuthorizationID: " + authorizationID);
    }


}