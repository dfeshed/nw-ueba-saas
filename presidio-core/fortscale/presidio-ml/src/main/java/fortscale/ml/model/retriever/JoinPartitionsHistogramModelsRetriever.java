package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.ml.model.*;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.builder.gaussian.ContinuousHistogramModelBuilder;
import fortscale.ml.model.joiner.PartitionsDataModelJoiner;
import fortscale.ml.model.pagination.PriorModelPaginationService;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.utils.MaxValuesResult;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.pagination.PageIterator;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.util.*;
import java.util.stream.Collectors;

public class JoinPartitionsHistogramModelsRetriever extends AbstractDataRetriever {
    private final JoinPartitionsHistogramModelsRetrieverConf config;
    private ModelStore modelStore;

    private PriorModelPaginationService priorModelPaginationService;
    private PartitionsDataModelJoiner partitionsDataModelJoiner;
    private int numOfMaxValuesSamples;
    private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;


    public JoinPartitionsHistogramModelsRetriever(JoinPartitionsHistogramModelsRetrieverConf config, PartitionsDataModelJoiner partitionsDataModelJoiner,
                                                  int numOfMaxValuesSamples, PriorModelPaginationService priorModelPaginationService, ModelStore modelStore,
                                                  FactoryService<AbstractDataRetriever> dataRetrieverFactoryService) {
        super(config);
        this.config = config;
        this.modelStore = modelStore;
        this.priorModelPaginationService = priorModelPaginationService;
        this.partitionsDataModelJoiner = partitionsDataModelJoiner;
        this.numOfMaxValuesSamples = numOfMaxValuesSamples;
        this.dataRetrieverFactoryService = dataRetrieverFactoryService;
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        Assert.isNull(contextId, String.format("%s can't be used with a context", getClass().getSimpleName()));
        ModelConfService modelConfService = getModelConfService();
        ModelConf mainModelConf = getMainModelConf(modelConfService);
        ModelConf secondaryModelConf = getSecondaryModelConf(modelConfService);

        List<ModelDAO> secondaryModels = new ArrayList<>(modelStore.getAllContextsModelDaosWithLatestEndTimeLte(secondaryModelConf, endTime.toInstant()));
        List<PageIterator<ModelDAO>> pageIterators = priorModelPaginationService.getPageIterators(mainModelConf, endTime.toInstant());

        List<Model> models = new ArrayList<>();
        for (PageIterator<ModelDAO> pageIterator : pageIterators) {
            while (pageIterator.hasNext()) {
                List<ModelDAO> mainModels = pageIterator.next();
                List<Model> joinedModels = joinModels(mainModels, secondaryModels, secondaryModelConf);
                models.addAll(joinedModels);
            }
        }

        if (models.isEmpty()) {
            return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
        } else {
            return new ModelBuilderData(models);
        }
    }

    /**
     * joinModels
     * @param multiContextModels multiContextModels
     * @param secondaryModels secondaryModels
     * @return  List<Model>
     */
    private List<Model> joinModels(List<ModelDAO> multiContextModels, List<ModelDAO> secondaryModels, ModelConf secondaryModelConf) {
        Map<String, ModelDAO> contextIdToSecondaryModel = secondaryModels.stream().collect(Collectors.toMap(ModelDAO::getContextId, e -> e));
        AbstractDataRetriever secondaryRetriever = dataRetrieverFactoryService.getProduct(secondaryModelConf.getDataRetrieverConf());
        List<String> secondaryContextNames = secondaryRetriever.getContextFieldNames();

        List<Model> models = new ArrayList<>();
        for (ModelDAO multiContextModel : multiContextModels) {
            String multiContextId = multiContextModel.getContextId();
            Map<String, String> multiContext = AdeContextualAggregatedRecord.getContext(multiContextId);
            multiContext = reduceContext(multiContext,secondaryContextNames);
            ModelDAO meetSecondaryModel = contextIdToSecondaryModel.get(AdeContextualAggregatedRecord.buildContextId(multiContext));

            if (!(multiContextModel.getModel() instanceof PartitionsDataModel && meetSecondaryModel.getModel() instanceof PartitionsDataModel)) {
                throw new IllegalArgumentException("models should be of type " + PartitionsDataModel.class.getSimpleName());
            }

            MaxValuesResult maxValuesResult = partitionsDataModelJoiner.joinModels((PartitionsDataModel) multiContextModel.getModel(),(PartitionsDataModel) meetSecondaryModel.getModel());
            Model continuousModel = new ContinuousHistogramModelBuilder().build(maxValuesResult.getMaxValues().values(), numOfMaxValuesSamples);
            models.add(continuousModel);
        }
        return models;
    }


    /**
     * Reduce context
     * @param context main model context
     * @param secondaryContextNames secondary model context names
     * @return Map<String, String> context
     */
    private Map<String, String> reduceContext(Map<String, String> context,  List<String> secondaryContextNames){
        return context.entrySet().stream().filter(e -> secondaryContextNames.contains(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private ModelConfService getModelConfService (){
            return DynamicModelConfServiceContainer.getModelConfService();
    }

    private ModelConf getMainModelConf(ModelConfService modelConfService){
        String modelConfName = config.getModelConfName();
        ModelConf mainModelConf = modelConfService.getModelConf(modelConfName);
        Assert.notNull(mainModelConf, String.format("failed to find modelConf for modelConfName=%s", modelConfName));
        return mainModelConf;
    }

    private ModelConf getSecondaryModelConf(ModelConfService modelConfService){
        String secondaryModelConfName = config.getSecondaryModelConfName();
        ModelConf secondaryModelConf = modelConfService.getModelConf(secondaryModelConfName);
        Assert.notNull(secondaryModelConf, String.format("failed to find modelConf for modelConfName=%s", secondaryModelConfName));
        return secondaryModelConf;
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
        throw new UnsupportedOperationException(String.format(
                "%s does not support retrieval of a single feature",
                getClass().getSimpleName()));
    }

    @Override
    public Set<String> getEventFeatureNames() {
        throw new UnsupportedOperationException(String.format("%s should be used to create \"additional-models\" only",
                getClass().getSimpleName()));
    }

    @Override
    public List<String> getContextFieldNames() {
        return Collections.emptyList();
    }

    @Override
    public String getContextId(Map<String, String> context) {
        return null;
    }
}
