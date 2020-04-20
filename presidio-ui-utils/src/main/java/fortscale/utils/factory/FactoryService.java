package fortscale.utils.factory;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class FactoryService<T> {
	private Map<String, Factory<T>> factoryMap = new HashMap<>();

	public void register(String factoryName, Factory<T> factory) {
		Assert.hasText(factoryName);
		Assert.notNull(factory);
		factoryMap.put(factoryName, factory);
	}

	public Factory<T> getFactory(String factoryName) {
		return factoryMap.get(factoryName);
	}

	public T getProduct(FactoryConfig factoryConfig) {
		Assert.notNull(factoryConfig);
		Factory<T> factory = getFactory(factoryConfig.getFactoryName());
		return factory == null ? null : factory.getProduct(factoryConfig);
	}
}
