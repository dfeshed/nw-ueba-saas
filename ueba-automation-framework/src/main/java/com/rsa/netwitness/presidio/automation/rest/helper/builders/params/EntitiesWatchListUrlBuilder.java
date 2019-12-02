package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

import org.junit.Assert;

import java.util.Objects;


class EntitiesWatchListUrlBuilder extends PresidioUrl {

    private EntitiesWatchListUrlBuilder(String url, String method, String body) {
        super(url, method, body);
    }

    static class Builder {
        private StringBuilder URL = new StringBuilder();
        private String jsonPatch = "";
        private String entityQuery = "";
        private String method = "PATCH";

        Builder(String url) {
            Assert.assertNotNull(url);
            URL.append(url);
        }

        public Builder appendID(String entityId) {
            URL.append("/").append(entityId);
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder setJsonPatchBodyNode(String operation) {
            jsonPatch = "\"jsonPatch\":{\"operations\":[{\"op\":\"" + operation + "\",\"path\":\"/tags/-\",\"value\":\"watched\"}]}";
            return this;
        }

        public Builder setEntityQueryBodyNode(String entityType, String severity, String tags) {
            entityQuery = "\"entityQuery\":{\"entityType\":\"" + entityType + "\",\"pageSize\":500000,\"pageNumber\":0,\"isPrefix\":false," +
                    "\"tags\":[ " + tags + "],\"severity\":[\"" + severity + "\"],\"expand\":true}";
            return this;
        }

        public PresidioUrl build() {
            Objects.requireNonNull(jsonPatch);
            String body = entityQuery.isBlank() ? "{" + jsonPatch + "}" : "{" + entityQuery + "," + jsonPatch + "}";
            return new EntitiesWatchListUrlBuilder(URL.toString(), method, body);
        }
    }
}
