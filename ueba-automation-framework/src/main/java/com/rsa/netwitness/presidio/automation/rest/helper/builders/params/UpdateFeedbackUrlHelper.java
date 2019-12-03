package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rsa.netwitness.presidio.automation.rest.client.HttpMethod;

import java.lang.reflect.Type;
import java.util.List;

public class UpdateFeedbackUrlHelper {
    private String URL;

    public UpdateFeedbackUrlHelper(String url) {
        this.URL = url;
    }

    public PresidioUrl setNotA_Risk(List<String> alertIds) {
        final String alertIdsArray = toJsonArray(alertIds);
        final String body = "{\"alertIds\":" + alertIdsArray + ",\"feedback\":\"NOT_RISK\"}";
        return new UpdateFeedbackUrl(URL, body);
    }

    public PresidioUrl removeNotA_Risk(List<String> alertIds) {
        final String alertIdsArray = toJsonArray(alertIds);
        final String body = "{\"alertIds\":" + alertIdsArray + ",\"feedback\":\"NONE\"}";
        return new UpdateFeedbackUrl(URL, body);
    }

    private String toJsonArray(List<String> alertIds) {
        Gson converter = new Gson();
        Type type = new TypeToken<List<String>>(){}.getType();
        return converter.toJson(alertIds, type);
    }


    private class UpdateFeedbackUrl extends PresidioUrl {
        private UpdateFeedbackUrl(String url, String jsonBody) {
            super(url, HttpMethod.POST, jsonBody);
        }
    }


}
