package fortscale.monitoring;

import fortscale.utils.standardProcess.standardProcessBase;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * Created by baraks on 4/25/2016.
 */
public abstract class MonitoringProcessGroupCommon extends standardProcessBase {

    @Override
    protected void groupContextInit(List<Class> springContexts)
    {
        baseContextInit(springContexts);
    }

    @Override
    protected AnnotationConfigApplicationContext groupEditAppContext(AnnotationConfigApplicationContext springContext) {
        return editAppContext(springContext);
    }

    @Override
    protected void groupShutDown()
    {
        shutDown();
    }

    protected abstract AnnotationConfigApplicationContext editAppContext(AnnotationConfigApplicationContext springContext);

    protected abstract void contextInit();
    protected abstract void shutDown();

}
