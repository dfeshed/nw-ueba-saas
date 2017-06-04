package fortscale.aggregation.feature.event;

import fortscale.aggregation.DataSourcesSyncTimer;
import fortscale.aggregation.feature.bucket.FeatureBucketsService;
import fortscale.aggregation.feature.functions.IAggrFeatureEventFunctionsService;
import fortscale.common.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.common.feature.AggrFeatureValue;
import junitparams.JUnitParamsRunner;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Created by amira on 01/10/2015.
 */
@RunWith(JUnitParamsRunner.class)
public class AggrFeatureEventBuilderServiceTest {

    @MockBean
    DataSourcesSyncTimer dataSourcesSyncTimer;

    @MockBean
    IAggrFeatureEventFunctionsService aggrFeatureFuncService;

    @MockBean
    FeatureBucketsService featureBucketsService;

    @MockBean
    AggrEventTopologyService aggrEventTopologyService;

    private static ClassPathXmlApplicationContext testContextManager;

    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;
    private AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService;

    @BeforeClass
    public static void setUpClass() {
        testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/aggr-feature-event-builder-context.xml");
    }

    @Before
    public void setUp() throws Exception {
        aggrFeatureEventBuilderService = testContextManager.getBean(AggrFeatureEventBuilderService.class);
        aggregatedFeatureEventsConfUtilService = testContextManager.getBean(AggregatedFeatureEventsConfUtilService.class);

    }

    @Test
    public void test1() throws Exception{
        Map<String, List<String>> parameters2featuresListMap = new HashMap<>();
        List<String> aggrFeatureNames = new ArrayList<>();
        aggrFeatureNames.add("letters");
        parameters2featuresListMap.put("groupBy", aggrFeatureNames);
        JSONObject funcJSONObj = new JSONObject();
        funcJSONObj.put("type", "aggr_feature_distinct_values_counter_func");
        funcJSONObj.put("includeValues", true);
        AggregatedFeatureEventConf conf = new AggregatedFeatureEventConf("my_number_of_distinct_values", "F", "bc1", 1, 1, 0, "HighestScore", parameters2featuresListMap, funcJSONObj);
        FeatureBucketConf bucketConf = mock(FeatureBucketConf.class);
        List<String> dataSources = new ArrayList<>();
        dataSources.add("ssh");
        when(bucketConf.getDataSources()).thenReturn(dataSources);
        conf.setBucketConf(bucketConf);

        Map<String, String> context = new HashMap<>();

        long startTimeSec = 1000000L;
        long endTimeSec =   2000000L;
        String dataSource = "ssh";

        Feature feature = new Feature("letters", new AggrFeatureValue(9.0, 1L));

        JSONObject eventJsonObj = aggrFeatureEventBuilderService.buildEvent(conf, context, feature, startTimeSec, endTimeSec);
        eventJsonObj.put("score", 100.0);
        
        AggrEvent aggrEvent = aggrFeatureEventBuilderService.buildEvent(eventJsonObj);

        JSONObject eventJsonObj2 = aggrFeatureEventBuilderService.getAggrFeatureEventAsJsonObject(aggrEvent);

        Assert.assertEquals(eventJsonObj, eventJsonObj2);
        Assert.assertEquals(eventJsonObj.get("aggregated_feature_value"), 9.0);
        Assert.assertEquals(eventJsonObj.get("event_type"), "aggr_event");
        Assert.assertEquals(eventJsonObj.get("data_source"), "aggr_event.bc1.my_number_of_distinct_values");
        Assert.assertEquals(eventJsonObj.get("score"), 100.0);
        Assert.assertEquals(eventJsonObj.get("aggregated_feature_type"), "F");
        Assert.assertEquals(eventJsonObj.get("data_sources").toString(), "[\"ssh\"]");
        Assert.assertEquals(eventJsonObj.get("date_time_unix"), 2000000L);
        Assert.assertEquals(eventJsonObj.get("start_time_unix"), 1000000L);
        Assert.assertEquals(eventJsonObj.get("end_time_unix"), 2000000L);
        Assert.assertEquals(eventJsonObj.get("end_time"),"1970-01-24 05:33:20" );
        Assert.assertEquals(eventJsonObj.get("bucket_conf_name"), "bc1");
        Assert.assertEquals(eventJsonObj.get("aggregated_feature_name"),"my_number_of_distinct_values");
        Assert.assertEquals(eventJsonObj.get("start_time"), "1970-01-12 15:46:40");
        Assert.assertEquals(eventJsonObj.get("aggregated_feature_info").toString(), "{\"total\":1}");

    }
}
