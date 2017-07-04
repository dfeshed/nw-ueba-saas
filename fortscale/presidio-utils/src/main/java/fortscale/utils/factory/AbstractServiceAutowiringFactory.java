package fortscale.utils.factory;

import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractServiceAutowiringFactory<T> implements InitializingBean, Factory<T> {
	protected FactoryService<T> factoryService;

	@Override
	public void afterPropertiesSet() throws Exception {
		//todo: change it to be non-spring
//		factoryService.register(getFactoryName(), this);
	}

	public void registerFactoryService(FactoryService<T> factoryService) {
		// todo: this is temporal and should be refactored
		this.factoryService = factoryService;
		this.factoryService.register(this.getFactoryName(),this);
	}

	public abstract String getFactoryName();
}
