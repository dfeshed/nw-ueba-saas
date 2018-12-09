package fortscale.ml.model.retriever;

import fortscale.common.feature.Feature;
import fortscale.ml.model.*;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.pagination.PriorModelPaginationService;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.model.joiner.MultiContextContinuousModelJoiner;
import fortscale.utils.pagination.PageIterator;
import org.springframework.util.Assert;

import java.util.*;

public class JoinModelRetriever extends AbstractDataRetriever {
    private final JoinModelRetrieverConf config;
    private ModelConfService modelConfService;
    private ModelStore modelStore;

    private ModelConf mainModelConf;
    private ModelConf secondaryModelConf;
    private PriorModelPaginationService modelPaginationService;
    private MultiContextContinuousModelJoiner multiContextModelJoiner;

    public JoinModelRetriever(JoinModelRetrieverConf config, MultiContextContinuousModelJoiner multiContextModelJoiner,
                              PriorModelPaginationService modelPaginationService, ModelStore modelStore) {
        super(config);
        this.config = config;
        this.modelStore = modelStore;
        this.modelPaginationService = modelPaginationService;
        this.multiContextModelJoiner = multiContextModelJoiner;
    }

    @Override
    public ModelBuilderData retrieve(String contextId, Date endTime) {
        Assert.isNull(contextId, String.format("%s can't be used with a context", getClass().getSimpleName()));
        fillModelConfService();

        List<ModelDAO> secondaryModels = new ArrayList<>(modelStore.getAllContextsModelDaosWithLatestEndTimeLte(secondaryModelConf, endTime.toInstant()));
        List<PageIterator<ModelDAO>> pageIterators = modelPaginationService.getPageIterators(mainModelConf, endTime.toInstant());

        List<Model> models = new ArrayList<>();
        for (PageIterator<ModelDAO> pageIterator : pageIterators) {
            while (pageIterator.hasNext()) {
                List<ModelDAO> mainModels = pageIterator.next();
                List<Model> joinedModels = multiContextModelJoiner.joinModels(mainModels, secondaryModels);
                models.addAll(joinedModels);
            }
        }

        if (models.isEmpty()) {
            return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
        } else {
            return new ModelBuilderData(models);
        }
    }


    private void fillModelConfService() {
        if (modelConfService == null) {
            modelConfService = DynamicModelConfServiceContainer.getModelConfService();
            String modelConfName = config.getModelConfName();
            String secondaryModelConfName = config.getSecondaryModelConfName();
            mainModelConf = this.modelConfService.getModelConf(modelConfName);
            secondaryModelConf = this.modelConfService.getModelConf(secondaryModelConfName);
            Assert.notNull(mainModelConf, String.format("failed to find modelConf for modelConfName=%s", modelConfName));
        }
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
