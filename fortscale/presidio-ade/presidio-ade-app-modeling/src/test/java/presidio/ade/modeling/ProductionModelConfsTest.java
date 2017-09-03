package presidio.ade.modeling;

import fortscale.aggregation.configuration.AslResourceFactory;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.IContextSelectorConf;
import fortscale.utils.factory.FactoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.modeling.config.ModelingServiceConfigurationTest;

import java.util.List;

import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * @author Lior Govrin
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ModelingServiceConfigurationTest.class)
public class ProductionModelConfsTest {
	@Value("${presidio.ade.modeling.enriched.records.base.configuration.path}")
	private String enrichedRecordsBaseConfigurationPath;
	@Value("${presidio.ade.modeling.feature.aggregation.records.base.configuration.path}")
	private String featureAggrRecordsBaseConfigurationPath;
	@Value("${presidio.ade.modeling.smart.records.base.configuration.path}")
	private String smartRecordsBaseConfigurationPath;

	@Autowired
	private AslResourceFactory aslResourceFactory;
	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;
	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	@Autowired
	private FactoryService<IModelBuilder> modelBuilderFactoryService;

	@Test
	public void enriched_record_model_confs_test() {
		productionModelConfsTest(enrichedRecordsBaseConfigurationPath);
	}

	@Test
	public void feature_aggr_record_model_confs_test() {
		productionModelConfsTest(featureAggrRecordsBaseConfigurationPath);
	}

	@Test
	public void smart_record_model_confs_test() {
		productionModelConfsTest(smartRecordsBaseConfigurationPath);
	}

	private void productionModelConfsTest(String baseConfigurationPath) {
		Resource[] baseConfigurationResources = aslResourceFactory.getResources(baseConfigurationPath);
		ModelConfService modelConfService = new ModelConfService(baseConfigurationResources, null, null);
		modelConfService.loadAslConfigurations();
		List<ModelConf> modelConfs = modelConfService.getModelConfs();
		notEmpty(modelConfs, "Missing model confs.");

		for (ModelConf modelConf : modelConfs) {
			IContextSelectorConf contextSelectorConf = modelConf.getContextSelectorConf();
			AbstractDataRetrieverConf dataRetrieverConf = modelConf.getDataRetrieverConf();
			IModelBuilderConf modelBuilderConf = modelConf.getModelBuilderConf();

			// Global models don't have context selectors
			if (contextSelectorConf != null) {
				notNull(contextSelectorFactoryService.getProduct(contextSelectorConf), "Null context selector.");
			}

			notNull(dataRetrieverFactoryService.getProduct(dataRetrieverConf), "Null data retriever.");
			notNull(modelBuilderFactoryService.getProduct(modelBuilderConf), "Null model builder.");
		}
	}
}
