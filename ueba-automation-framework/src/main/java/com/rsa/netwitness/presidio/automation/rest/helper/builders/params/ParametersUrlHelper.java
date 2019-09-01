package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

public class ParametersUrlHelper {
    private String URL;

    public ParametersUrlHelper(String url) {
        this.URL = url;
    }

    public PresidioUrl withNoParameters() {
        return new ParametersUrlBuilder.Builder(URL).build();
    }

    public PresidioUrl withPageParameters(int pageSize, int pageNumber) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(pageSize)
                .setPageNumber(pageNumber)
                .build();
    }

    public PresidioUrl withMaxSizeParameters() {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setExpand(false)
                .build();
    }

    public PresidioUrl withMaxSizeAndExpendedParameters() {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setExpand(true)
                .build();
    }

    public PresidioUrl withSortedParameters(String sortDirection, String sortFieldNames) {
        return new ParametersUrlBuilder.Builder(URL)
                .setSortDirection(sortDirection)
                .setSortFieldNames(sortFieldNames)
                .build();
    }

    public PresidioUrl withMaxSizeAndSortedParameters(String sortDirection, String sortFieldNames) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setSortDirection(sortDirection)
                .setSortFieldNames(sortFieldNames)
                .build();
    }

    public PresidioUrl withMaxSizeAndSortedAndExpendedParameters(String sortDirection, String sortFieldNames) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setExpand(true)
                .setSortDirection(sortDirection)
                .setSortFieldNames(sortFieldNames)
                .build();
    }

    public PresidioUrl withMaxSizeAndSortedAscAndMinMaxScoreParameters(long minScore, long maxScore) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setSortDirection("ASC")
                .setSortFieldNames("SCORE")
                .setMinScore(minScore)
                .setMaxScore(maxScore)
                .build();
    }

    public PresidioUrl withMaxSizeAndSortedAscAndMinScoreParameters(long minScore) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setSortDirection("ASC")
                .setSortFieldNames("SCORE")
                .setMinScore(minScore)
                .build();
    }

    public PresidioUrl withMaxSizeAndSortedAscAndMaxScoreParameters(long maxScore) {
        return new ParametersUrlBuilder.Builder(URL)
                .setPageSize(10000)
                .setPageNumber(0)
                .setSortDirection("ASC")
                .setSortFieldNames("SCORE")
                .setMaxScore(maxScore)
                .build();
    }

    public PresidioUrl withEntityNamesParameters(String entityNames) {
        return new ParametersUrlBuilder.Builder(URL)
                .setEntityNames(entityNames)
                .build();
    }

    public PresidioUrl withEntityNamesExpandedParameters(String entityNames) {
        return new ParametersUrlBuilder.Builder(URL)
                .setEntityNames(entityNames)
                .setExpand(true)
                .build();
    }

    public PresidioUrl withSeverityParameter(String severity) {
        return new ParametersUrlBuilder.Builder(URL)
                .setSeverity(severity)
                .build();
    }

    public PresidioUrl withMaxSizeAndIndicatorNameParameters(String indicatorName) {
        return new ParametersUrlBuilder.Builder(URL)
                .setIndicatorName(indicatorName)
                .setPageSize(10000)
                .setPageNumber(0)
                .build();
    }

    public PresidioUrl withAggregatedFieldParameter(String fieldName) {
        return new ParametersUrlBuilder.Builder(URL)
                .setAggregateBy(fieldName)
                .build();
    }

    public PresidioUrl withExpandedParameter() {
        return new ParametersUrlBuilder.Builder(URL)
                .setExpand(true)
                .build();
    }


}
