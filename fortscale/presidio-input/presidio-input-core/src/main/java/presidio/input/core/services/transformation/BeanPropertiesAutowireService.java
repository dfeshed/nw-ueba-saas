package presidio.input.core.services.transformation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import presidio.input.core.services.transformation.transformer.InputDocumentTransformer;


public class BeanPropertiesAutowireService  implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void autowireBeanProperties(InputDocumentTransformer clzz) {
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(clzz, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}