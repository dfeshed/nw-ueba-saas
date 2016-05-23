package fortscale.monitoring.metrics.adapter;

import fortscale.monitoring.metrics.adapter.config.MetricAdapterConfig;
import fortscale.monitoring.metrics.adapter.config.MetricAdapterProperties;
import fortscale.monitoring.metrics.adapter.topicReader.EngineDataTopicSyncReader;
import fortscale.monitoring.metrics.adapter.topicReader.EngineDataTopicSyncReaderResponse;
import fortscale.monitoring.metrics.adapter.impl.MetricAdapterServiceImpl;
import fortscale.utils.influxdb.InfluxdbService;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.models.engine.*;
import fortscale.utils.spring.PropertySourceConfigurer;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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
        private long initiationWaitTimeInSeconds = 1;

        @Mock
        private InfluxdbService influxdbService;

        @Mock
        private StatsService statsService;
        @Mock
        private EngineDataTopicSyncReader engineDataTopicSyncReader;


        @Bean(destroyMethod = "shutDown")
        MetricAdapterService metricAdapter() {
            return new MetricAdapterServiceImpl(statsService, initiationWaitTimeInSeconds, influxdbService,
                    engineDataTopicSyncReader,metricsAdapterMajorVersion, dbName, retentionName, retentionDuration,
                    retentionReplication, waitBetweenWriteRetries, waitBetweenInitRetries, waitBetweenReadRetries, waitBetweenEmptyReads,
                    false);
        }

        @Bean
        private static PropertySourceConfigurer metricAdapterEnvironmentPropertyConfigurer() {
            Properties properties = MetricAdapterProperties.getProperties();

            return new PropertySourceConfigurer(MetricAdapterConfig.class, properties);
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
    }

    private EngineData engineData;

    @Autowired
    MetricAdapterService metricAdapterServiceImpl;

    @Test
    public void ShouldConvertEngineDataToPointsSuccessfully() {
        List<Point> points = metricAdapterServiceImpl.engineDataToPoints(engineData);
        Assert.assertEquals(points.get(0).toString(), "Point [name=shakespeares MetricGroup, time=1460976051, tags={show=Macbeth, theater=cameri}, precision=SECONDS, fields={guests=150000, quote=Come what come may, time and the hour runs through the roughest day, rating=4.5}, useInteger=true]");
    }

    @Test
    public void shouldConvertMetricsMessagesToBatchPointsSuccessfully() {
        EngineDataTopicSyncReaderResponse samzaMetricsTopicSyncReaderResponse = new EngineDataTopicSyncReaderResponse();
        samzaMetricsTopicSyncReaderResponse.addMessage(engineData);
        BatchPoints batchPoints = metricAdapterServiceImpl.EnginDataToBatchPoints(samzaMetricsTopicSyncReaderResponse);
        Assert.assertEquals(batchPoints.toString(), "BatchPoints [database=dbName, retentionPolicy=null, tags={}, points=[Point [name=AAA-GRP, time=101122, tags={TAG-AAA-1=AAA-AAA-AAA-1, TAG-AAA-2=AAA-AAA-AAA-2}, precision=SECONDS, fields={doubleAAA1=100201.11, doubleAAA2=100202.22, doubleAAA3=100203.33, longAAA1=100101, longAAA2=100102, longAAA3=100103, longAAA4=100104, stringAAA1=AAA-AAA-100301, stringAAA2=AAA-AAA-100302, stringAAA3=AAA-AAA-100303}, useInteger=true], Point [name=BBB-GRP, time=201122, tags={TAG-BBB-1=BBB-BBB-BBB-1, TAG-BBB-2=BBB-BBB-BBB-2}, precision=SECONDS, fields={doubleBBB1=200201.11, doubleBBB2=200202.22, doubleBBB3=200203.33, longBBB1=200101, longBBB2=200102, longBBB3=200103, longBBB4=200104, stringBBB1=BBB-BBB-200301, stringBBB2=BBB-BBB-200302, stringBBB3=BBB-BBB-200303}, useInteger=true], Point [name=CCC-GRP, time=301122, tags={TAG-CCC-1=CCC-CCC-CCC-1, TAG-CCC-2=CCC-CCC-CCC-2}, precision=SECONDS, fields={doubleCCC1=300201.11, doubleCCC2=300202.22, doubleCCC3=300203.33, longCCC1=300101, longCCC2=300102, longCCC3=300103, longCCC4=300104, stringCCC1=CCC-CCC-300301, stringCCC2=CCC-CCC-300302, stringCCC3=CCC-CCC-300303}, useInteger=true]]]");
    }
}
