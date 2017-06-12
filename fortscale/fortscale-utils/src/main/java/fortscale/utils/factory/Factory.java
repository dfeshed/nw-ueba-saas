package fortscale.utils.factory;

public interface Factory<T> {
	/**
	 * Get a product created according to the given configuration.
	 *
	 * @param factoryConfig configures how the product should be created
	 * @return the product
	 */
	T getProduct(FactoryConfig factoryConfig);

	/**
	 * Get this factory's default product (does not require a configuration).
	 *
	 * @return the default product
	 */
	default T getDefaultProduct() {
		String message = String.format("Factory %s does not have a default product.", getClass().getSimpleName());
		throw new UnsupportedOperationException(message);
	}
}
