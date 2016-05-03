package fortscale.utils.standardProcess;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public abstract class standardProcessBase {
    private static final Logger logger = Logger.getLogger(standardProcessBase.class);
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

    }
    protected void main(String [] args,List<Class> springContexts)
    {
        Runtime.getRuntime().addShutdownHook(new Thread() {
                                                 @Override
                                                 public void run() {
                                                     logger.info("shutting down");
                                                     baseShutDown();
                                                 }
                                             });

        baseContextInit(springContexts);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.error("failed to join current thread");
        }

    }

    protected void baseShutDown()
    {
        groupShutDown();
    }
    protected abstract void groupShutDown();
    protected abstract void groupContextInit(List<Class> springContexts);
    protected abstract AnnotationConfigApplicationContext groupEditAppContext(AnnotationConfigApplicationContext springContext);




}
