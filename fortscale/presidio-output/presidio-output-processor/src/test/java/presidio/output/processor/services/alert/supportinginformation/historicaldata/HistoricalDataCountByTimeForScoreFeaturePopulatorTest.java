package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.spring.AlertServiceElasticConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AlertServiceElasticConfig.class, MongodbTestConfig.class, TestConfig.class, ElasticsearchTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HistoricalDataCountByTimeForScoreFeaturePopulatorTest {

    @Autowired
    private HistoricalDataCountByTimeForScoreFeaturePopulator historicalDataCountByTimeForScoreFeaturePopulator;

    @Autowired
    private SupportingInformationConfig config;

    String featureName = "highestNumOfPagesScoreUserIdPrintHourly";
    HistoricalDataConfig historicalDataConfig = config.getIndicatorConfig(featureName).getHistoricalData();
    String contextValue = "contextValue";

    @Test
    public void test_NoEvents() {
        String anomalyValue = null;
        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now().minus(2, ChronoUnit.DAYS));
        HistoricalData historicalData = historicalDataCountByTimeForScoreFeaturePopulator.createHistoricalData(timeRange, CommonStrings.CONTEXT_USERID, contextValue, Schema.PRINT, featureName, anomalyValue, historicalDataConfig);
        Assert.assertTrue(CollectionUtils.isEmpty(historicalData.getAggregation().getBuckets()));
    }

    @Test
    public void test() {

    }

}
