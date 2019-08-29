package presidio.input.core.services.transformation.transformer;

import presidio.input.core.services.transformation.BeanPropertiesAutowireService;

public abstract class AbstractInputDocumentTransformer implements InputDocumentTransformer {

    public void postAutowireProcessor(BeanPropertiesAutowireService beanPropertiesAutowireService) {
        beanPropertiesAutowireService.autowireBeanProperties(this);
    }


}
