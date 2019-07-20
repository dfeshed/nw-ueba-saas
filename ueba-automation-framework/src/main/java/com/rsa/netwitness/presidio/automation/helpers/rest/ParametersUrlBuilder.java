package com.rsa.netwitness.presidio.automation.helpers.rest;

import org.junit.Assert;

import java.io.UnsupportedEncodingException;

import static java.net.URLEncoder.encode;


public class ParametersUrlBuilder {

    private final String URL;

    private ParametersUrlBuilder(String url) {
        URL = url;
    }


    // pageSize=10000&pageNumber=0&expand=true
    // sortDirection=DESC&sortFieldNames=START_DATE&pageSize=200&pageNumber=0
    public static class Builder {

        private StringBuilder URL = new StringBuilder();

        private final String PAGE_SIZE = "pageSize";
        private final String PAGE_NUMBER = "pageNumber";
        private final String EXPAND = "expand";
        private final String SORT_DIRECTION = "sortDirection";
        private final String SORT_FIELD_NAMES = "sortFieldNames";
        private final String MIN_SCORE = "maxScore";
        private final String MAX_SCORE = "maxScore";
        private final String ENTITY_NAMES = "entityNames";
        private final String INDICATOR_NAME = "indicatorsName";
        private final String SEVERITY = "severity";
        private final String AGGREGATE_BY = "aggregateBy";

        Builder(String base_url) {
            Assert.assertNotNull(base_url);
            URL.append(base_url).append("?");
        }

        public Builder setPageSize(int val) {
            URL.append(PAGE_SIZE).append("=").append(val).append("&");
            return this;
        }

        public Builder setPageNumber(int val) {
            URL.append(PAGE_NUMBER).append("=").append(val).append("&");
            return this;
        }

        public Builder setSortDirection(String val) {
            URL.append(SORT_DIRECTION).append("=").append(val).append("&");
            return this;
        }

        public Builder setSortFieldNames(String val) {
            URL.append(SORT_FIELD_NAMES).append("=").append(val).append("&");
            return this;
        }

        public Builder setExpand(boolean val) {
            URL.append(EXPAND).append("=").append(val).append("&");
            return this;
        }

        public Builder setMinScore(long val) {
            URL.append(MIN_SCORE).append("=").append(val).append("&");
            return this;
        }

        public Builder setMaxScore(long val) {
            URL.append(MAX_SCORE).append("=").append(val).append("&");
            return this;
        }

        public Builder setEntityNames(String val) {
            try {
                URL.append(ENTITY_NAMES).append("=").append(encode(val, "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder setIndicatorName(String val) {
            URL.append(INDICATOR_NAME).append("=").append(val).append("&");
            return this;
        }

        public Builder setSeverity(String val) {
            URL.append(SEVERITY).append("=").append(val).append("&");
            return this;
        }

        public Builder setAggregateBy(String val) {
            URL.append(AGGREGATE_BY).append("=").append(val).append("&");
            return this;
        }


        public ParametersUrlBuilder build() {
            return new ParametersUrlBuilder(URL.toString().substring(0, URL.length()-1));
        }
    }

    @Override
    public String toString() {
        return URL;
    }

}
