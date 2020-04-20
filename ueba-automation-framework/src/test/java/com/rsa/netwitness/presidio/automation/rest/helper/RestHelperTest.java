package com.rsa.netwitness.presidio.automation.rest.helper;

import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.junit.Test;

import static org.junit.Assert.*;

public class RestHelperTest {

    @Test
    public void alerts() {
        RestHelper helper = new RestHelper();
        PresidioUrl url1 = helper.alerts().url().withNoParameters();
        PresidioUrl url2 = helper.alerts().url().withMaxSizeAndExpendedParameters();

        assertTrue(url1.toString().startsWith("http://"));
        assertTrue(url1.toString().endsWith("/alerts"));
        assertTrue(url2.toString().endsWith("/alerts?pageSize=10000&pageNumber=0&expand=true"));
    }
    @Test
    public void alertsWithId() {
        RestHelper helper = new RestHelper();
        PresidioUrl url1 = helper.alerts().withId("123").url().withNoParameters();
        PresidioUrl url2 = helper.alerts().withId("123").url().withMaxSizeAndExpendedParameters();

        assertTrue(url2.toString().startsWith("http://"));
        assertTrue(url1.toString().endsWith("/alerts/123"));
        assertTrue(url2.toString().endsWith("/alerts/123?pageSize=10000&pageNumber=0&expand=true"));
    }
    @Test
    public void alertsWithIdIndicators() {
        RestHelper helper = new RestHelper();
        PresidioUrl url1 = helper.alerts().withId("123").indicators().url().withNoParameters();
        PresidioUrl url2 = helper.alerts().withId("123").indicators().url().withMaxSizeAndExpendedParameters();

        assertTrue(url2.toString().startsWith("http://"));
        assertTrue(url1.toString().endsWith("/alerts/123/indicators"));
        assertTrue(url2.toString().endsWith("/alerts/123/indicators?pageSize=10000&pageNumber=0&expand=true"));
    }
    @Test
    public void alertsWithIdIndicatorsWithId() {
        RestHelper helper = new RestHelper();
        PresidioUrl url1 = helper.alerts().withId("123").indicators().withId("321").url().withNoParameters();
        PresidioUrl url2 = helper.alerts().withId("123").indicators().withId("321").url().withMaxSizeAndExpendedParameters();

        assertTrue(url2.toString().startsWith("http://"));
        assertTrue(url1.toString().endsWith("/alerts/123/indicators/321"));
        assertTrue(url2.toString().endsWith("/alerts/123/indicators/321?pageSize=10000&pageNumber=0&expand=true"));
    }
    @Test
    public void dailyMetrics() {
        RestHelper helper = new RestHelper();
        PresidioUrl url1 = helper.dailyMetrics().url().withNoParameters();
        PresidioUrl url2 = helper.dailyMetrics().url().withPageParameters("page");

        assertTrue(url1.toString().startsWith("http://"));
        assertTrue(url2.toString().startsWith("http://"));
        assertTrue(url1.toString().endsWith("/ueba-daily-metrics"));
        assertTrue(url2.toString().endsWith("/ueba-daily-metrics?metricNames=page"));
    }
    @Test
    public void entitiesNoParameters() {
        RestHelper helper = new RestHelper();
        PresidioUrl url1 = helper.entities().url().withNoParameters();

        assertTrue(url1.toString().startsWith("http://"));
        assertTrue(url1.toString().endsWith("/entities"));
    }
    @Test
    public void entitiesWithMaxSizeAndExpendedParameters() {
        RestHelper helper = new RestHelper();
        PresidioUrl url1 = helper.entities().url().withMaxSizeAndExpendedParameters();

        assertTrue(url1.toString().startsWith("http://"));
        assertTrue(url1.toString().endsWith("/entities?pageSize=10000&pageNumber=0&expand=true"));
    }
}