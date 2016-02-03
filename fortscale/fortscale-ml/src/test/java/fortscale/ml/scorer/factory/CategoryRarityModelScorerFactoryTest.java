package fortscale.ml.scorer.factory;

import fortscale.common.feature.Feature;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.CategoryRarityModelScorer;
import fortscale.ml.scorer.config.CategoryRarityModelScorerConf;
import fortscale.ml.scorer.config.ModelInfo;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-factory-tests-context.xml"})
public class CategoryRarityModelScorerFactoryTest {

    @Autowired
    CategoryRarityModelScorerFactory categoryRarityModelScorerFactory;

    @Autowired
    ModelConfService modelConfService;

    @Autowired
    FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

    @Test(expected = IllegalArgumentException.class)
    public void confNotOfExpectedType() {
        categoryRarityModelScorerFactory.getProduct(new FactoryConfig() {
            @Override
            public String getFactoryName() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConfTest() {
        categoryRarityModelScorerFactory.getProduct(null);
    }


    @Test
    public void getProductTest() {
        CategoryRarityModelScorerConf conf = new CategoryRarityModelScorerConf("name", new ModelInfo("model-name"), Collections.emptyList(), 6, 10);
        conf.setMinNumOfSamplesToInfluence(5);
        conf.setEnoughNumOfSamplesToInfluence(15);
        conf.setMinNumOfDistinctValuesToInfluence(12);
        conf.setEnoughNumOfDistinctValuesToInfluence(17);
        conf.setUseCertaintyToCalculateScore(true);

        List<JSONObject> dummyFunctions = new ArrayList<>();
        dummyFunctions.add(new JSONObject());
        AbstractDataRetrieverConf dataRetrieverConf = new AbstractDataRetrieverConf(10, dummyFunctions) {
            @Override
            public String getFactoryName() {
                return "dummy-data-retriever-factory-name";
            }
        };
        IModelBuilderConf modelBuilderConf = new IModelBuilderConf() {
            @Override
            public String getFactoryName() {
                return "dummy-model-factory-name";
            }
        };
        ModelConf modelConf = new ModelConf("dummy-model-conf", dataRetrieverConf, modelBuilderConf);
        List<String> contextFieldNames = new ArrayList<String>();
        contextFieldNames.add("context-field1");
        Set<String> featureNamesSet = new HashSet<String>();
        featureNamesSet.add("feature1");

        when(modelConfService.getModelConf(any(String.class))).thenReturn(modelConf);
        when(dataRetrieverFactoryService.getProduct(any())).thenReturn(new AbstractDataRetriever(dataRetrieverConf) {
            @Override
            public Object retrieve(String contextId, Date endTime) {
                return null;
            }

            @Override
            public Object retrieve(String contextId, Date endTime, Feature feature) {
                return null;
            }

            @Override
            public String getContextId(Map<String, String> context) {
                return null;
            }

            @Override
            public Set<String> getEventFeatureNames() {
                 return featureNamesSet;
            }

            @Override
            public List<String> getContextFieldNames() {

                return contextFieldNames;
            }
        });

        CategoryRarityModelScorer scorer = (CategoryRarityModelScorer)categoryRarityModelScorerFactory.getProduct(conf);

        Assert.assertEquals(conf.getName(), scorer.getName());
        Assert.assertEquals(conf.getModelInfo().getModelName(), scorer.getModelName());
        Assert.assertEquals(conf.getMaxNumOfRareFeatures(), scorer.getAlgorithm().getMaxNumOfRareFeatures());
        Assert.assertEquals(conf.getMaxRareCount(), scorer.getAlgorithm().getMaxRareCount());
        Assert.assertEquals(conf.getMinNumOfDistinctValuesToInfluence(), scorer.getMinNumOfDistinctValuesToInfluence());
        Assert.assertEquals(conf.getEnoughNumOfDistinctValuesToInfluence(), scorer.getEnoughNumOfDistinctValuesToInfluence());
        Assert.assertEquals(conf.getMinNumOfSamplesToInfluence(), scorer.getMinNumOfSamplesToInfluence());
        Assert.assertEquals(conf.isUseCertaintyToCalculateScore(), scorer.isUseCertaintyToCalculateScore());
        Assert.assertEquals(conf.getEnoughNumOfSamplesToInfluence(), scorer.getEnoughNumOfSamplesToInfluence());
        Assert.assertEquals(contextFieldNames, scorer.getContextFieldNames());
        Assert.assertEquals(featureNamesSet.toArray()[0], scorer.getFeatureName());
    }

}
