package fortscale.utils.standardProcess;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public abstract class StandardProcessBase {
    private static final Logger logger = Logger.getLogger(StandardProcessBase.class);
    protected void baseContextInit(List<Class> springContexts){
        logger.info("loading spring context");
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        for (Class springContext: springContexts)
        {
            annotationConfigApplicationContext.register(springContext);
        }
        annotationConfigApplicationContext = groupEditAppContext(annotationConfigApplicationContext);
        annotationConfigApplicationContext.refresh();
        annotationConfigApplicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE,true);
        annotationConfigApplicationContext.registerShutdownHook();

    }
    protected void main(String [] args,List<Class> springContexts)
    {

        baseContextInit(springContexts);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.error("failed to join current thread");
        }

    }

    protected abstract void groupContextInit(List<Class> springContexts);
    protected abstract AnnotationConfigApplicationContext groupEditAppContext(AnnotationConfigApplicationContext springContext);




}
