package fortscale.utils.factory;

public interface Factory<T> {
	/**
	 * Get a product created according to the given configuration.
	 *
	 * @param factoryConfig configures how the product should be created
	 * @return the product
	 */
	T getProduct(FactoryConfig factoryConfig);
}
