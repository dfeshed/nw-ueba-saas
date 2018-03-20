package fortscale.ml.model;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.metrics.ModelingServiceMetricsContainer;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.IContextSelectorConf;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * Given a {@link ModelConf}, this factory creates the corresponding {@link ModelingEngine}
 * using factory services for selectors, retrievers and builders. The engines also require
 * a {@link ModelStore}, therefore it should be passed in the c'tor.
 *
 * @author Lior Govrin
 */
public class ModelingEngineFactory {
	private FactoryService<IContextSelector> contextSelectorFactoryService;
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
	private FactoryService<IModelBuilder> modelBuilderFactoryService;
	private ModelStore modelStore;
	private ModelingServiceMetricsContainer modelingServiceMetricsContainer;
	private Duration allContextsForcedSelectionInterval;

	/**
	 * C'tor.
	 *
	 * @param contextSelectorFactoryService the factory service for selectors
	 * @param dataRetrieverFactoryService   the factory service for retrievers
	 * @param modelBuilderFactoryService    the factory service for builders
	 * @param modelStore                    the model store
	 */
	public ModelingEngineFactory(
			FactoryService<IContextSelector> contextSelectorFactoryService,
			FactoryService<AbstractDataRetriever> dataRetrieverFactoryService,
			FactoryService<IModelBuilder> modelBuilderFactoryService,
			ModelStore modelStore,
			ModelingServiceMetricsContainer modelingServiceMetricsContainer,
			Duration allContextsForcedSelectionInterval) {

		this.contextSelectorFactoryService = contextSelectorFactoryService;
		this.dataRetrieverFactoryService = dataRetrieverFactoryService;
		this.modelBuilderFactoryService = modelBuilderFactoryService;
		this.modelStore = modelStore;
		this.modelingServiceMetricsContainer = modelingServiceMetricsContainer;
		this.allContextsForcedSelectionInterval = allContextsForcedSelectionInterval;
	}

	/**
	 * @param modelConf includes selectorConf, retrieverConf and builderConf
	 * @return the corresponding {@link ModelingEngine}
	 */
	public ModelingEngine getModelingEngine(ModelConf modelConf) {
		IContextSelector contextSelector = getContextSelector(modelConf);
		AbstractDataRetriever dataRetriever = dataRetrieverFactoryService.getProduct(modelConf.getDataRetrieverConf());
		Assert.notNull(dataRetriever, String.format("Null data retriever for model conf %s.", modelConf.getName()));
		IModelBuilder modelBuilder = modelBuilderFactoryService.getProduct(modelConf.getModelBuilderConf());
		Assert.notNull(modelBuilder, String.format("Null model builder for model conf %s.", modelConf.getName()));
		return new ModelingEngine(modelConf, contextSelector, dataRetriever, modelBuilder,
				modelStore, modelingServiceMetricsContainer, allContextsForcedSelectionInterval);
	}

	// If it's a global modelConf, a context selector is not configured
	private IContextSelector getContextSelector(ModelConf modelConf) {
		IContextSelectorConf contextSelectorConf = modelConf.getContextSelectorConf();

		if (contextSelectorConf == null) {
			return null;
		} else {
			IContextSelector contextSelector = contextSelectorFactoryService.getProduct(contextSelectorConf);
			String message = String.format("Null context selector for model conf %s.", modelConf.getName());
			Assert.notNull(contextSelector, message);
			return contextSelector;
		}
	}
}
