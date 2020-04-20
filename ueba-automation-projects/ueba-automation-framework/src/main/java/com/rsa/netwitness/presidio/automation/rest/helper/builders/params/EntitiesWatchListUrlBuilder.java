package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

import com.rsa.netwitness.presidio.automation.rest.client.HttpMethod;
import org.junit.Assert;

import java.util.Objects;


class EntitiesWatchListUrlBuilder extends PresidioUrl {

    private EntitiesWatchListUrlBuilder(String url, HttpMethod method, String body) {
        super(url, method, body);
    }

    static class Builder {
        private StringBuilder URL = new StringBuilder();
        private String jsonPatch = "";
        private String entityQuery = "";
        private HttpMethod method = HttpMethod.PATCH;

        Builder(String url) {
            Assert.assertNotNull(url);
            URL.append(url);
        }

        public Builder appendID(String entityId) {
            URL.append("/").append(entityId);
            return this;
        }

        public Builder setMethod(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder setJsonPatchBodyNode(String operation) {
            jsonPatch = "\"operations\":[{\"op\":\"" + operation + "\",\"path\":\"/tags/-\",\"value\":\"watched\"}]";
            return this;
        }

        //{"entityQuery":{"entityType":"userId","pageSize":500000,"pageNumber":0,"isPrefix":false,"tags":[],"severity":["HIGH"],"expand":true},
        // "jsonPatch":{"operations":[{"op":"add","path":"/tags/-","value":"watched"}]}}
        public Builder setEntityQueryBodyNode(String entityType, String severity, boolean filterByWatchedTag) {
            final String watchedTag = filterByWatchedTag ? "\"watched\"" : "";
            entityQuery = "\"entityQuery\":{\"entityType\":\"" + entityType + "\",\"pageSize\":500000,\"pageNumber\":0,\"isPrefix\":false," +
                    "\"tags\":[" + watchedTag + "],\"severity\":[\"" + severity + "\"],\"expand\":true}";
            return this;
        }

        public PresidioUrl build() {
            Objects.requireNonNull(jsonPatch);
            String body = entityQuery.isBlank() ? "{" + jsonPatch + "}" : "{" + entityQuery + ", \"jsonPatch\": {" + jsonPatch + "}}";
            return new EntitiesWatchListUrlBuilder(URL.toString(), method, body);
        }
    }
}
