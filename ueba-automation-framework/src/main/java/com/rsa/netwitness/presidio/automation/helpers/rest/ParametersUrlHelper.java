package com.rsa.netwitness.presidio.automation.helpers.rest;

import org.slf4j.LoggerFactory;


public class ParametersUrlHelper extends RestBase {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(ParametersUrlHelper.class.getName());

    ParametersUrlHelper(String url) {
        this.URL = url;
    }

    public ParametersUrlBuilder.Builder parametersBuilder() {
        return new ParametersUrlBuilder.Builder(URL);
    }

    public ParametersUrlBuilder withNoParameters() {
        return new ParametersUrlBuilder.Builder(URL).build();
    }

    public ParametersUrlBuilder withPageParameters(int pageSize, int pageNumber) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(pageSize)
                .setPageNumber(pageNumber)
                .build();
    }

    public ParametersUrlBuilder withMaxSizeParameters() {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setExpand(false)
                .build();
    }

    public ParametersUrlBuilder withMaxSizeAndExpendedParameters() {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setExpand(true)
                .build();
    }

    public ParametersUrlBuilder withMaxSizeAndSortedParameters(String sortDirection, String sortFieldNames) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setSortDirection(sortDirection)
                .setSortFieldNames(sortFieldNames)
                .build();
    }

    public ParametersUrlBuilder withMaxSizeAndSortedAscAndMinMaxScoreParameters(long minScore, long maxScore) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setSortDirection("ASC")
                .setSortFieldNames("SCORE")
                .setMinScore(minScore)
                .setMaxScore(maxScore)
                .build();
    }

    public ParametersUrlBuilder withMaxSizeAndSortedAscAndMinScoreParameters(long minScore) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setSortDirection("ASC")
                .setSortFieldNames("SCORE")
                .setMinScore(minScore)
                .build();
    }

    public ParametersUrlBuilder withMaxSizeAndSortedAscAndMaxScoreParameters(long maxScore) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setSortDirection("ASC")
                .setSortFieldNames("SCORE")
                .setMaxScore(maxScore)
                .build();
    }

    public ParametersUrlBuilder withEntityNamesParameters(String entityNames) {
        return new ParametersUrlBuilder.Builder(URL)
                .setEntityNames(entityNames)
                .build();
    }

    public ParametersUrlBuilder withEntityNamesExpandedParameters(String entityNames) {
        return new ParametersUrlBuilder.Builder(URL)
                .setEntityNames(entityNames)
                .setExpand(true)
                .build();
    }

    public ParametersUrlBuilder withSeverityParameter(String severity) {
        return new ParametersUrlBuilder.Builder(URL)
                .setSeverity(severity)
                .build();
    }

    public ParametersUrlBuilder withMaxSizeAndIndicatorNameParameters(String indicatorName) {
        return new ParametersUrlBuilder.Builder(URL)
                .setIndicatorName(indicatorName)
                .setPageSize(10000)
                .setPageNumber(0)
                .build();
    }

    public ParametersUrlBuilder withAggregatedFieldParameter(String fieldName) {
        return new ParametersUrlBuilder.Builder(URL)
                .setAggregateBy(fieldName)
                .build();
    }

    public ParametersUrlBuilder withExpandedParameter() {
        return new ParametersUrlBuilder.Builder(URL)
                .setExpand(true)
                .build();
    }


}
