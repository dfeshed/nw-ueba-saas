package presidio.ade.modeling;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.DynamicModelConfServiceContainer;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataStore;
import presidio.ade.modeling.config.ModelingServiceConfiguration;
import presidio.ade.test.utils.generators.AccumulatedAggregationFeatureRecordHourlyGenerator;
import presidio.data.generators.common.GeneratorException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 9/4/17.
 */
@Category(ModuleTestCategory.class)
@ContextConfiguration
@RunWith(SpringRunner.class)
public class ModelingServiceApplicationContinousModels {
    private static final String GENERATOR_CONTEXT_ID_PATTERN = "userId\\#[a-g]{1}[1-2]{1}";
    private static final int GENERATOR_END_HOUR_OF_DAY = 16;
    private static final int GENERATOR_START_HOUR_OF_DAY = 5;
    @Autowired
    private BootShim bootShim;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private AggregationEventsAccumulationDataStore accumulationDataStore;
    @Autowired
    private ModelStore modelStore;
    private static final String FEATURE_AGGREGATION_RECORDS_LINE_FORMAT = "process --group_name feature-aggregation-record-models.file --session_id test-run --end_date %s";

    private Map<Integer, Double> aggregatedFeatureValuesMap;

    @Before
    public void setup() {
        aggregatedFeatureValuesMap = new HashMap<>();
        aggregatedFeatureValuesMap.put(5, 65D);
        aggregatedFeatureValuesMap.put(6, 75D);
        aggregatedFeatureValuesMap.put(7, 95D);
        aggregatedFeatureValuesMap.put(8, 55D);
        aggregatedFeatureValuesMap.put(9, 3D);
        aggregatedFeatureValuesMap.put(10, 42D);
        aggregatedFeatureValuesMap.put(11, 43D);
        aggregatedFeatureValuesMap.put(12, 44D);
        aggregatedFeatureValuesMap.put(13, 45D);
        aggregatedFeatureValuesMap.put(14, 46D);
        aggregatedFeatureValuesMap.put(15, 47D);
        aggregatedFeatureValuesMap.put(16, 48D);
    }

    @Test
    public void createContinuousModels() throws GeneratorException {
        generateAccumulatedDataForAllConfs();

        CommandResult commandResult = bootShim.getShell().executeCommand(String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT, Instant.now().minus(Duration.ofDays(2))));
        Assert.assertTrue(commandResult.isSuccess());
        commandResult = bootShim.getShell().executeCommand(String.format(FEATURE_AGGREGATION_RECORDS_LINE_FORMAT, Instant.now()));
        Assert.assertTrue(commandResult.isSuccess());

        List<ModelConf> modelConfs = DynamicModelConfServiceContainer.getModelConfService().getModelConfs();
        List<ModelConf> priorModelConfs = modelConfs.stream().filter(x -> x.getName().contains("prior.global")).collect(Collectors.toList());
        Assert.assertTrue("conf must have at least 1 prior model", priorModelConfs.size() > 0);
        priorModelConfs.forEach(modelConf -> {
            List<ModelDAO> modelDaos = modelStore.getAllContextsModelDaosWithLatestEndTimeLte(modelConf, Instant.now());
            Assert.assertTrue(modelDaos.size() > 0);
            Optional<ModelDAO> modelWithSegmentPrior = modelDaos.stream().filter(modelDAO -> {
                GaussianPriorModel model = (GaussianPriorModel) modelDAO.getModel();
                return model.getSegmentPriors().length > 0;
            }).findFirst();
            String modelConfName = modelConf.getName();
            Assert.assertTrue(String.format("model=%s is expected to have segment prior", modelConfName), modelWithSegmentPrior.isPresent());
        });
    }

    private void generateAccumulatedDataForAllConfs() throws GeneratorException {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();

        for (AggregatedFeatureEventConf conf : aggregatedFeatureEventConfList) {
            String featureName = conf.getName();

            AccumulatedAggregationFeatureRecordHourlyGenerator generator =
                    new AccumulatedAggregationFeatureRecordHourlyGenerator(featureName, GENERATOR_CONTEXT_ID_PATTERN,
                            aggregatedFeatureValuesMap, GENERATOR_START_HOUR_OF_DAY, GENERATOR_END_HOUR_OF_DAY);
            List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecords = generator.generate();
            accumulationDataStore.store(accumulatedAggregationFeatureRecords);
        }
    }

    @Configuration
    @Import({MongodbTestConfig.class, BootShimConfig.class, ModelingServiceConfiguration.class})
    public static class springConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer continousModelingServiceConfigurationTestPropertiesPlaceholderConfigurer() {
            Properties properties = new Properties();
            // Feature bucket conf service
            properties.put("presidio.ade.modeling.feature.bucket.confs.base.path", "classpath*:config/asl/feature-buckets/**/*.json");
            // Feature aggregation event conf service
            properties.put("presidio.ade.modeling.feature.aggregation.event.confs.base.path", "classpath*:config/asl/aggregation-records/feature-aggregation-records/file.json");
            // Smart event conf service
            properties.put("presidio.ade.smart.record.base.configurations.path", "classpath*:config/asl/smart-records/*");
            // Model conf service
            properties.put("presidio.ade.modeling.enriched.records.group.name", "enriched-record-models");
            properties.put("presidio.ade.modeling.enriched.records.base.configuration.path", "classpath*:config/asl/models/enriched-records/");
            properties.put("presidio.ade.modeling.feature.aggregation.records.group.name", "feature-aggregation-record-models");
            properties.put("presidio.ade.modeling.feature.aggregation.records.base.configuration.path", "classpath*:config/asl/models/feature-aggregation-records/");
            properties.put("presidio.ade.modeling.smart.records.group.name", "smart-record-models");
            properties.put("presidio.ade.modeling.smart.records.base.configuration.path", "classpath*:config/asl/models/smart-records/");
            // Additional properties
            properties.put("presidio.ade.modeling.feature.buckets.default.expire.after.seconds", 7776000);
            properties.put("fortscale.model.retriever.smart.oldestAllowedModelDurationDiff", "PT48H");
            properties.put("presidio.default.ttl.duration", "PT1000H");
            properties.put("presidio.default.cleanup.interval", "PT2000H");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
