package com.rsa.netwitness.presidio.automation.rest.helper.rest;

import com.rsa.netwitness.presidio.automation.rest.client.RestAPI;
import com.rsa.netwitness.presidio.automation.rest.client.RestApiResponse;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import static org.assertj.core.api.Assertions.assertThat;

public interface IRestCallHelper {
    ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(IRestCallHelper.class.getName());

    default RestApiResponse getRestApiResponse(ParametersUrlBuilder parametersUrlBuilder) {
        String URL = parametersUrlBuilder.toString();
        LOGGER.debug("Sending request: " + URL);
        return RestAPI.sendGet(URL);
    }

    default JSONObject getRestApiResponseAsJsonObj(ParametersUrlBuilder url) {
        JSONObject users;
        RestApiResponse response = getRestApiResponse(url);
        assertThat(response).as(url + "\nReturn null").isNotNull();
        assertThat(response.getResponseCode()).as(url + "\nResponse code != 200").isEqualTo(200);
        try {
            users = new JSONObject(response.getResultBody());
            return users;
        } catch (JSONException e) {
            Assert.fail(url+"\nUnable to parse Json response");
            e.printStackTrace();
            return null;
        }
    }

}
