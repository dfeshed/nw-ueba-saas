package fortscale.ml.model.pagination;

import fortscale.ml.model.store.ModelStore;
import fortscale.ml.model.store.ModelStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@Configurable
@Import(ModelStoreConfig.class)
public class PriorModelPaginationServiceConfig {

    @Value("${model.pageIterator.pageSize}")
	private int pageSize;
	@Value("${model.pageIterator.maxGroupSize}")
	private int maxGroupSize;
	@Autowired
    private ModelStore modelStore;

	@Bean
    public PriorModelPaginationService getModelPaginationService(){
        return new PriorModelPaginationService(modelStore, pageSize, maxGroupSize);
    }

}
