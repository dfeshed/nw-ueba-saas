package fortscale.ml.scorer.factory;

import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractServiceAutowiringScorerFactory<T> implements InitializingBean, Factory<T> {
    @Autowired
    protected ScorersFactoryService  scorersFactoryService;

    @Override
    public void afterPropertiesSet() throws Exception {
        scorersFactoryService.register(getFactoryName(), this);
    }

    public abstract String getFactoryName();


}
