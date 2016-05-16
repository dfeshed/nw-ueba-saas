package fortscale.monitoring.metricAdapter;

import fortscale.monitoring.metricAdapter.config.MetricAdapterConfig;
import fortscale.monitoring.metricAdapter.config.MetricAdapterProperties;
import fortscale.monitoring.metricAdapter.impl.MetricAdapterServiceImpl;
import fortscale.monitoring.samza.converter.SamzaMetricToStatsService;
import fortscale.monitoring.samza.topicReader.SamzaMetricsTopicSyncReader;
import fortscale.monitoring.samza.topicReader.SamzaMetricsTopicSyncReaderResponse;
import fortscale.utils.influxdb.InfluxdbService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.models.engine.*;
import fortscale.utils.samza.metricMessageModels.Header;
import fortscale.utils.samza.metricMessageModels.MetricMessage;
import fortscale.utils.samza.metricMessageModels.Metrics;
import fortscale.utils.spring.MainProcessPropertiesConfigurer;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MetricAdapterTest {
    @Configuration
    public static class metricAdapterTestSpringConfig {

        private long metricsAdapterMajorVersion = 1;
        private String dbName = "dbName";
        private String retentionName = "retentionName";
        private String retentionDuration = "1w";
        private String retentionReplication = "1";
        private long waitBetweenWriteRetries = 1;
        private long waitBetweenInitRetries = 1;
        private long waitBetweenReadRetries = 1;
        private long waitBetweenEmptyReads = 1;
        private String engineDataMetricName = "engineData";
        private String engineDataMetricPackage = "enginePackage";
        private long initiationWaitTimeInSeconds = 1;

        @Mock
        private InfluxdbService influxdbService;
        @Mock
        private SamzaMetricsTopicSyncReader samzaMetricsTopicSyncReader;
        @Mock
        private StatsService statsService;


        @Bean
        public SamzaMetricToStatsService samzaMetricWriter() {
            return new SamzaMetricToStatsService(statsService);
        }

        @Bean(destroyMethod = "shutDown")
        MetricAdapterService metricAdapter() {
            return new MetricAdapterServiceImpl(statsService, initiationWaitTimeInSeconds, influxdbService,
                    samzaMetricsTopicSyncReader, metricsAdapterMajorVersion, dbName, retentionName, retentionDuration,
                    retentionReplication, waitBetweenWriteRetries, waitBetweenInitRetries, waitBetweenReadRetries, waitBetweenEmptyReads,
                    engineDataMetricName, engineDataMetricPackage, false);
        }

        @Bean
        private static PropertySourceConfigurer metricAdapterEnvironmentPropertyConfigurer() {
            Properties properties = MetricAdapterProperties.getProperties();
            PropertySourceConfigurer configurer = new PropertySourceConfigurer(MetricAdapterConfig.class, properties);

            return configurer;
        }
    }

    @Before
    public void setup() {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("theater", "cameri"));
        tags.add(new Tag("show", "Macbeth"));
        List<DoubleField> doubleFields = new ArrayList<>();
        doubleFields.add(new DoubleField("rating", 4.5));
        List<LongField> longFields = new ArrayList<>();
        longFields.add(new LongField("guests", 150000L));
        List<StringField> stringFields = new ArrayList<>();
        stringFields.add(new StringField("quote", "Come what come may, time and the hour runs through the roughest day"));
        Long time = 1460976051L;
        MetricGroup metricGroup = new MetricGroup("shakespeares MetricGroup", "shakespeareClass", time, tags, longFields, doubleFields, stringFields);
        List<MetricGroup> metricGroups = new ArrayList<>();
        metricGroups.add(metricGroup);
        engineData = new EngineData(100L, metricGroups);

        Map<String, Map<String, Object>> metricsMap = new HashMap<>();
        Map<String, Object> metricsDataMap = new HashMap<>();
        String engineDataString = "{\"version\":100,\"metricGroups\":[{\"groupName\":\"AAA-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineBaseTest\",\"measurementEpoch\":101122,\"tags\":[{\"name\":\"TAG-AAA-1\",\"value\":\"AAA-AAA-AAA-1\"},{\"name\":\"TAG-AAA-2\",\"value\":\"AAA-AAA-AAA-2\"}],\"longFields\":[{\"name\":\"longAAA1\",\"value\":100101},{\"name\":\"longAAA2\",\"value\":100102},{\"name\":\"longAAA3\",\"value\":100103},{\"name\":\"longAAA4\",\"value\":100104}],\"doubleFields\":[{\"name\":\"doubleAAA1\",\"value\":100201.11},{\"name\":\"doubleAAA2\",\"value\":100202.22},{\"name\":\"doubleAAA3\",\"value\":100203.33}],\"stringFields\":[{\"name\":\"stringAAA1\",\"value\":\"AAA-AAA-100301\"},{\"name\":\"stringAAA2\",\"value\":\"AAA-AAA-100302\"},{\"name\":\"stringAAA3\",\"value\":\"AAA-AAA-100303\"}]},{\"groupName\":\"BBB-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineBaseTest\",\"measurementEpoch\":201122,\"tags\":[{\"name\":\"TAG-BBB-1\",\"value\":\"BBB-BBB-BBB-1\"},{\"name\":\"TAG-BBB-2\",\"value\":\"BBB-BBB-BBB-2\"}],\"longFields\":[{\"name\":\"longBBB1\",\"value\":200101},{\"name\":\"longBBB2\",\"value\":200102},{\"name\":\"longBBB3\",\"value\":200103},{\"name\":\"longBBB4\",\"value\":200104}],\"doubleFields\":[{\"name\":\"doubleBBB1\",\"value\":200201.11},{\"name\":\"doubleBBB2\",\"value\":200202.22},{\"name\":\"doubleBBB3\",\"value\":200203.33}],\"stringFields\":[{\"name\":\"stringBBB1\",\"value\":\"BBB-BBB-200301\"},{\"name\":\"stringBBB2\",\"value\":\"BBB-BBB-200302\"},{\"name\":\"stringBBB3\",\"value\":\"BBB-BBB-200303\"}]},{\"groupName\":\"CCC-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineBaseTest\",\"measurementEpoch\":301122,\"tags\":[{\"name\":\"TAG-CCC-1\",\"value\":\"CCC-CCC-CCC-1\"},{\"name\":\"TAG-CCC-2\",\"value\":\"CCC-CCC-CCC-2\"}],\"longFields\":[{\"name\":\"longCCC1\",\"value\":300101},{\"name\":\"longCCC2\",\"value\":300102},{\"name\":\"longCCC3\",\"value\":300103},{\"name\":\"longCCC4\",\"value\":300104}],\"doubleFields\":[{\"name\":\"doubleCCC1\",\"value\":300201.11},{\"name\":\"doubleCCC2\",\"value\":300202.22},{\"name\":\"doubleCCC3\",\"value\":300203.33}],\"stringFields\":[{\"name\":\"stringCCC1\",\"value\":\"CCC-CCC-300301\"},{\"name\":\"stringCCC2\",\"value\":\"CCC-CCC-300302\"},{\"name\":\"stringCCC3\",\"value\":\"CCC-CCC-300303\"}]}]}";
        metricsDataMap.put("engineData", engineDataString);
        metricsMap.put("enginePackage", metricsDataMap);
        Metrics metrics = new Metrics(metricsMap);
        metricEngineData = new MetricMessage(new Header("job-id", "samza-version", "job-name", "host", 0, "container-name", "source", 0, "version"), metrics);

    }

    private EngineData engineData;
    private MetricMessage metricEngineData;

    @Autowired
    MetricAdapterService metricAdapterServiceImpl;

    @Test
    public void ShouldConvertEngineDataToPointsSuccessfully() {
        List<Point> points = metricAdapterServiceImpl.engineDataToPoints(engineData);
        Assert.assertEquals(points.get(0).toString(), "Point [name=shakespeares MetricGroup, time=1460976051, tags={show=Macbeth, theater=cameri}, precision=SECONDS, fields={guests=150000, quote=Come what come may, time and the hour runs through the roughest day, rating=4.5}, useInteger=true]");
    }

    @Test
    public void shouldConvertMetricsMessagesToBatchPointsSuccessfully() {
        SamzaMetricsTopicSyncReaderResponse samzaMetricsTopicSyncReaderResponse = new SamzaMetricsTopicSyncReaderResponse();
        samzaMetricsTopicSyncReaderResponse.setMetricMessage(metricEngineData);
        BatchPoints batchPoints = metricAdapterServiceImpl.metricsMessagesToBatchPoints(Arrays.asList(samzaMetricsTopicSyncReaderResponse));
        Assert.assertEquals(batchPoints.toString(), "BatchPoints [database=dbName, retentionPolicy=null, tags={}, points=[Point [name=AAA-GRP, time=101122, tags={TAG-AAA-1=AAA-AAA-AAA-1, TAG-AAA-2=AAA-AAA-AAA-2}, precision=SECONDS, fields={doubleAAA1=100201.11, doubleAAA2=100202.22, doubleAAA3=100203.33, longAAA1=100101, longAAA2=100102, longAAA3=100103, longAAA4=100104, stringAAA1=AAA-AAA-100301, stringAAA2=AAA-AAA-100302, stringAAA3=AAA-AAA-100303}, useInteger=true], Point [name=BBB-GRP, time=201122, tags={TAG-BBB-1=BBB-BBB-BBB-1, TAG-BBB-2=BBB-BBB-BBB-2}, precision=SECONDS, fields={doubleBBB1=200201.11, doubleBBB2=200202.22, doubleBBB3=200203.33, longBBB1=200101, longBBB2=200102, longBBB3=200103, longBBB4=200104, stringBBB1=BBB-BBB-200301, stringBBB2=BBB-BBB-200302, stringBBB3=BBB-BBB-200303}, useInteger=true], Point [name=CCC-GRP, time=301122, tags={TAG-CCC-1=CCC-CCC-CCC-1, TAG-CCC-2=CCC-CCC-CCC-2}, precision=SECONDS, fields={doubleCCC1=300201.11, doubleCCC2=300202.22, doubleCCC3=300203.33, longCCC1=300101, longCCC2=300102, longCCC3=300103, longCCC4=300104, stringCCC1=CCC-CCC-300301, stringCCC2=CCC-CCC-300302, stringCCC3=CCC-CCC-300303}, useInteger=true]]]");
    }
}
