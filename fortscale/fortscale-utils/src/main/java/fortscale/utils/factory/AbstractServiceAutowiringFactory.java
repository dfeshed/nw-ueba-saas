package fortscale.utils.factory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractServiceAutowiringFactory<T> implements InitializingBean, Factory<T> {
	@Autowired
	protected FactoryService<T> factoryService;

	@Override
	public void afterPropertiesSet() throws Exception {
		factoryService.register(getFactoryName(), this);
	}

	public abstract String getFactoryName();
}
