package fortscale.utils.standardProcess;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public abstract class StandardProcessBase {
    protected void baseEarlyInit(List<Class> springContexts){
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        for (Class springContext: springContexts)
        {
            annotationConfigApplicationContext.register(springContext);

        }
        annotationConfigApplicationContext = groupEditAppContext(annotationConfigApplicationContext);
        annotationConfigApplicationContext.refresh();
        annotationConfigApplicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE,true);

    }
    protected void preBaseInit(){preGroupInit();}
    protected void postBaseInit(){postGroupInit();}
    protected void baseStart(){groupStart();}
    protected void setShouldStop(){}
    protected void baseStop(){}
    protected abstract void groupStart();
    protected abstract void preGroupInit();
    protected abstract void postGroupInit();
    protected abstract AnnotationConfigApplicationContext groupEditAppContext(AnnotationConfigApplicationContext springContext);



}
