package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.ml.model.ModelConfService;
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
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@Import({
//        application-specific confs
        ScorersModelConfServiceConfig.class,
        DataRetrieverFactoryServiceConfig.class,
//        common application confs
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
    @Value("#{T(java.time.Duration).parse('${fortscale.model.cache.maxDiffBetweenCachedModelAndEvent}')}")
    public Duration maxDiffBetweenCachedModelAndEvent;
    @Value("${fortscale.model.cache.size}")
    public int cacheSize;
    @Autowired
    public ModelCacheMetricsContainer modelCacheMetricsContainer;
    @Bean
    public ModelsCacheService modelCacheServiceInMemory() {
        return new ModelCacheServiceInMemory(modelConfService, modelStore, dataRetrieverFactoryService, maxDiffBetweenCachedModelAndEvent, cacheSize, 1, modelCacheMetricsContainer);
    }
}
