package fortscale.utils.factory;

public interface Factory<T> {
	T getProduct(FactoryConfig factoryConfig);
}
