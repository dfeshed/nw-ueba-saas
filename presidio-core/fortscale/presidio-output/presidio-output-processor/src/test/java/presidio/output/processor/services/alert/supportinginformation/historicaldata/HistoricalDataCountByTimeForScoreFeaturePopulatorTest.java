package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;
import presidio.output.processor.spring.AlertClassificationPriorityConfig;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class HistoricalDataCountByTimeForScoreFeaturePopulatorTest {
    @Configuration
    @Import({AlertClassificationPriorityConfig.class, TestConfig.class})
    public static class HistoricalDataCountByTimeForScoreFeaturePopulatorTestConfiguration {
        @Bean
        public AdeManagerSdk adeManagerSdk() {
            return Mockito.mock(AdeManagerSdk.class);
        }
    }

    @Autowired
    private SupportingInformationConfig config;

    private HistoricalDataFetcher historicalDataFetcher =
            Mockito.mock(HistoricalDataFetcher.class);
    private AggregationDataCountByTimeForScoreFeaturePopulator aggregationDataCountByTimeForScoreFeaturePopulator =
            new AggregationDataCountByTimeForScoreFeaturePopulator(historicalDataFetcher);

    @Test
    public void test_NoEvents() {
        String featureName = "highestNumOfPagesScoreUserIdPrintHourly";
        List<HistoricalDataConfig> historicalDataConfig = config.getIndicatorConfig(featureName).getHistoricalData();
        String contextValue = "contextValue";
        List<DailyHistogram<String, Number>> result = new ArrayList<>();
        LocalDate date = LocalDate.of(2018, 2, 25);
        Map<String, Number> histogram = new HashMap<>();
        DailyHistogram<String, Number> dailyHistogram = new DailyHistogram<>(date, histogram);
        result.add(dailyHistogram);
        Mockito.when(historicalDataFetcher.getDailyHistogramsForFeature(
                Mockito.any(TimeRange.class),
                Mockito.anyMapOf(String.class, String.class),
                Mockito.any(Schema.class),
                Mockito.anyString(),
                Mockito.any(HistoricalDataConfig.class),
                Mockito.eq(false)))
                .thenReturn(result);
        String anomalyValue = String.valueOf(2370.0);
        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now().minus(2, ChronoUnit.DAYS));
        Map<String, String> contexts = Collections.singletonMap(CommonStrings.CONTEXT_USERID, contextValue);
        Aggregation aggregation = aggregationDataCountByTimeForScoreFeaturePopulator.createAggregationData(
                timeRange, contexts, Schema.PRINT, featureName, anomalyValue,
                historicalDataConfig.get(0), true, new Date(Long.parseLong("1517403600")));
        HistoricalData historicalData = new HistoricalData(Collections.singletonList(aggregation));
        Assert.assertTrue(CollectionUtils.isEmpty(historicalData.getAggregation().get(0).getBuckets()));
    }

    @Test
    public void test_createHistoricalData() {
        String featureName = "highestNumOfPagesScoreUserIdPrintHourly";
        List<HistoricalDataConfig> historicalDataConfig = config.getIndicatorConfig(featureName).getHistoricalData();
        String contextValue = "contextValue";
        List<DailyHistogram<String, Number>> result = new ArrayList<>();
        LocalDate date = LocalDate.of(2018, 2, 25);
        Map<String, Number> histogram = new HashMap<>();
        histogram.put("one_hour_resolution_epochtime#1517400000", 120.0);
        histogram.put("one_hour_resolution_epochtime#1517425200", 51.0);
        histogram.put("one_hour_resolution_epochtime#1517364000", 43.0);
        histogram.put("one_hour_resolution_epochtime#1517403600", 2370.0);
        histogram.put("one_hour_resolution_epochtime#1517389200", 144.0);
        histogram.put("one_hour_resolution_epochtime#1517428800", 16.0);
        histogram.put("one_hour_resolution_epochtime#1517374800", 19.0);
        histogram.put("one_hour_resolution_epochtime#1517432400", 32.0);
        histogram.put("one_hour_resolution_epochtime#1517382000", 64.0);
        DailyHistogram<String, Number> dailyHistogram = new DailyHistogram<>(date, histogram);
        result.add(dailyHistogram);
        Mockito.when(historicalDataFetcher.getDailyHistogramsForFeature(
                Mockito.any(TimeRange.class),
                Mockito.anyMapOf(String.class, String.class),
                Mockito.any(Schema.class),
                Mockito.anyString(),
                Mockito.any(HistoricalDataConfig.class),
                Mockito.eq(false)))
                .thenReturn(result);
        String anomalyValue = String.valueOf(2370);
        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now().minus(2, ChronoUnit.DAYS));
        Map<String, String> contexts = Collections.singletonMap(CommonStrings.CONTEXT_USERID, contextValue);
        Date anomalyDate = new Date(Long.parseLong("1517403600"));
        Aggregation aggregation = aggregationDataCountByTimeForScoreFeaturePopulator.createAggregationData(
                timeRange, contexts, Schema.PRINT, featureName, anomalyValue,
                historicalDataConfig.get(0), true, anomalyDate);
        HistoricalData historicalData = new HistoricalData(Collections.singletonList(aggregation));
        Assert.assertTrue(CollectionUtils.isNotEmpty(historicalData.getAggregation().get(0).getBuckets()));
        Assert.assertEquals(9, historicalData.getAggregation().get(0).getBuckets().size());

        for (Object object : historicalData.getAggregation().get(0).getBuckets()) {
            Bucket bucket = (Bucket)object;
            if (anomalyValue.equals(bucket.getValue().toString())) Assert.assertTrue(bucket.isAnomaly());
        }
    }
}
