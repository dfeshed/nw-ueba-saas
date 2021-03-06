package presidio.ade.smart.config;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.ModelConfServiceConfig;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelCacheServiceInMemory;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.cache.metrics.ModelCacheMetricsContainer;
import fortscale.ml.model.cache.metrics.ModelCacheMetricsContainerConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.model.store.ModelStoreConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

/**
 * Created by barak_schuster on 6/6/17.
 */
@Configuration
@Import({
        ModelConfServiceConfig.class,
        ModelStoreConfig.class,
        ModelCacheMetricsContainerConfig.class
})
public class ModelCacheServiceInMemoryConfig {
    @Autowired
    public ModelConfService modelConfService;
    @Autowired
    public ModelStore modelStore;
    @Autowired
    public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
    @Autowired
    public ModelCacheMetricsContainer modelCacheMetricsContainer;
    @Value("#{T(java.time.Duration).parse('${fortscale.model.cache.maxDiffBetweenCachedModelAndEvent}')}")
    public Duration maxDiffBetweenCachedModelAndEvent;
    @Value("${fortscale.model.cache.size}")
    public int cacheSize;
    @Value("${fortscale.model.cache.num.of.models.per.context:30}")
    public int numOfModelsPerContextId;

    @Bean
    public ModelsCacheService modelCacheServiceInMemory() {
        return new ModelCacheServiceInMemory(modelConfService, modelStore, dataRetrieverFactoryService, maxDiffBetweenCachedModelAndEvent, cacheSize, numOfModelsPerContextId, modelCacheMetricsContainer);
    }

    @Bean
    public EventModelsCacheService eventModelsCacheService() {
        return new EventModelsCacheService();
    }
}
