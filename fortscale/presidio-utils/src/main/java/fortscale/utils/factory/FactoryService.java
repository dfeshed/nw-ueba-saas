package fortscale.utils.factory;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class FactoryService<T> {
	private Map<String, Factory<T>> factoryMap = new HashMap<>();

	public void register(String factoryName, Factory<T> factory) {
		Assert.hasText(factoryName, "Factory name cannot be blank.");
		Assert.notNull(factory, "Factory cannot be null.");
		factoryMap.put(factoryName, factory);
	}

	public Factory<T> getFactory(String factoryName) {
		return factoryMap.get(factoryName);
	}

	public T getProduct(FactoryConfig factoryConfig) {
		Assert.notNull(factoryConfig, "Factory config cannot be null.");
		Factory<T> factory = getFactory(factoryConfig.getFactoryName());
		return factory == null ? null : factory.getProduct(factoryConfig);
	}

	public T getDefaultProduct(String factoryName) {
		Factory<T> factory = getFactory(factoryName);
		return factory == null ? null : factory.getDefaultProduct();
	}
}
