package fortscale.utils.factory;


public abstract class AbstractServiceAutowiringFactory<T> implements Factory<T> {
	protected FactoryService<T> factoryService;

	public void registerFactoryService(FactoryService<T> factoryService) {
		// todo: this is temporal and should be refactored
		this.factoryService = factoryService;
		this.factoryService.register(this.getFactoryName(),this);
	}

	public abstract String getFactoryName();
}
