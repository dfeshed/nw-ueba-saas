package fortscale.ml.model.selector;

import fortscale.ml.model.DynamicModelConfServiceContainer;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.retriever.ModelRetriever;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.time.TimeRange;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModelsSubContextSelector implements IContextSelector{

    private ModelStore modelStore;

    private String modelConfName;
    private String contextFieldName;

    public ModelsSubContextSelector(ModelStore modelStore, String modelConfName, String contextFieldName){
        this.modelStore = modelStore;
        this.modelConfName = modelConfName;
        this.contextFieldName = contextFieldName;
    }

    @Override
    public Set<String> getContexts(TimeRange timeRange) {
        ModelConfService modelConfService = DynamicModelConfServiceContainer.getModelConfService();
        ModelConf modelConf = modelConfService.getModelConf(modelConfName);

        Set<String> ret = new HashSet<>();
        for(String contextValue: modelStore.getAllSubContextsWithLatestEndTimeLte(modelConf, contextFieldName, timeRange.getEnd())){
            ret.add(ModelRetriever.buildContextId(Collections.singletonMap(contextFieldName, contextValue)));
        }
        return ret;
    }
}
