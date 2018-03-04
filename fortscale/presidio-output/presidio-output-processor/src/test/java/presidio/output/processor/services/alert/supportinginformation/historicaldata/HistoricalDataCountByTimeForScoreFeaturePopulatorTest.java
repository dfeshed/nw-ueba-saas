//package presidio.output.processor.services.alert.supportinginformation.historicaldata;
//
//import fortscale.common.general.CommonStrings;
//import fortscale.common.general.Schema;
//import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
//import fortscale.utils.test.mongodb.MongodbTestConfig;
//import fortscale.utils.time.TimeRange;
//import org.apache.commons.collections.CollectionUtils;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import presidio.output.domain.records.alerts.HistoricalData;
//import presidio.output.proccesor.spring.TestConfig;
//import presidio.output.processor.config.HistoricalDataConfig;
//import presidio.output.processor.config.SupportingInformationConfig;
//import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;
//import presidio.output.processor.spring.AlertServiceElasticConfig;
//
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {AlertServiceElasticConfig.class, MongodbTestConfig.class, TestConfig.class, ElasticsearchTestConfig.class})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
//public class HistoricalDataCountByTimeForScoreFeaturePopulatorTest {
//
//    @Autowired
//    private HistoricalDataCountByTimeForScoreFeaturePopulator historicalDataCountByTimeForScoreFeaturePopulator;
//
//    @Autowired
//    private SupportingInformationConfig config;
//
//    @MockBean
//    HistoricalDataFetcher historicalDataFetcher;
//
//
//    @Test
//    public void test_NoEvents() {
//        String featureName = "highestNumOfPagesScoreUserIdPrintHourly";
//        HistoricalDataConfig historicalDataConfig = config.getIndicatorConfig(featureName).getHistoricalData();
//        String contextValue = "contextValue";
//
//        List<DailyHistogram<String>> result = new ArrayList<>();
//        LocalDate date = LocalDate.of(2018, 02, 25);
//        Map<String, Double> histogram = new HashMap<>();
//        DailyHistogram<String> dailyHistogram = new DailyHistogram<>(date, histogram);
//        result.add(dailyHistogram);
//        Mockito.when(historicalDataFetcher.getDailyHistogramsForFeature(Mockito.any(TimeRange.class), Mockito.anyString(), Mockito.anyString(), Mockito.any(Schema.class), Mockito.anyString(), Mockito.any(HistoricalDataConfig.class))).thenReturn(result);
//        String anomalyValue = String.valueOf(2370.0);
//        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now().minus(2, ChronoUnit.DAYS));
//        HistoricalData historicalData = historicalDataCountByTimeForScoreFeaturePopulator.createHistoricalData(timeRange, CommonStrings.CONTEXT_USERID, contextValue, Schema.PRINT, featureName, anomalyValue, historicalDataConfig);
//        Assert.assertTrue(CollectionUtils.isEmpty(historicalData.getAggregation().getBuckets()));
//    }
//
//    @Test
//    public void test_createHistoricalData() {
//        String featureName = "highestNumOfPagesScoreUserIdPrintHourly";
//        HistoricalDataConfig historicalDataConfig = config.getIndicatorConfig(featureName).getHistoricalData();
//        String contextValue = "contextValue";
//
//        List<DailyHistogram<String>> result = new ArrayList<>();
//        LocalDate date = LocalDate.of(2018, 02, 25);
//        Map<String, Double> histogram = new HashMap<>();
//        histogram.put("one_hour_resolution_epochtime#1517400000", 120.0);
//        histogram.put("one_hour_resolution_epochtime#1517425200", 51.0);
//        histogram.put("one_hour_resolution_epochtime#1517364000", 43.0);
//        histogram.put("one_hour_resolution_epochtime#1517403600", 2370.0);
//        histogram.put("one_hour_resolution_epochtime#1517389200", 144.0);
//        histogram.put("one_hour_resolution_epochtime#1517428800", 16.0);
//        histogram.put("one_hour_resolution_epochtime#1517374800", 19.0);
//        histogram.put("one_hour_resolution_epochtime#1517432400", 32.0);
//        histogram.put("one_hour_resolution_epochtime#1517382000", 64.0);
//        DailyHistogram<String> dailyHistogram = new DailyHistogram<>(date, histogram);
//        result.add(dailyHistogram);
//        Mockito.when(historicalDataFetcher.getDailyHistogramsForFeature(Mockito.any(TimeRange.class), Mockito.anyString(), Mockito.anyString(), Mockito.any(Schema.class), Mockito.anyString(), Mockito.any(HistoricalDataConfig.class))).thenReturn(result);
//        String anomalyValue = String.valueOf(2370.0);
//        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now().minus(2, ChronoUnit.DAYS));
//        HistoricalData historicalData = historicalDataCountByTimeForScoreFeaturePopulator.createHistoricalData(timeRange, CommonStrings.CONTEXT_USERID, contextValue, Schema.PRINT, featureName, anomalyValue, historicalDataConfig);
//        Assert.assertTrue(CollectionUtils.isNotEmpty(historicalData.getAggregation().getBuckets()));
//        Assert.assertEquals(9, historicalData.getAggregation().getBuckets().size());
//
//    }
//
//}
