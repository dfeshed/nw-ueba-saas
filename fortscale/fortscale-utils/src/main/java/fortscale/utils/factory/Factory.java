package fortscale.utils.factory;

public interface Factory<T> {
	public T getProduct(FactoryConfig factoryConfig);
}
