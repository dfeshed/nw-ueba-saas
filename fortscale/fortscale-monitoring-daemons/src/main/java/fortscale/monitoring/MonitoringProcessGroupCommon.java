package fortscale.monitoring;

import fortscale.utils.process.standardProcess.StandardProcessBase;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * Created by baraks on 4/25/2016.
 */
public abstract class MonitoringProcessGroupCommon extends StandardProcessBase {

    @Override
    protected void groupContextInit(List<Class> springContexts)
    {
        baseContextInit(springContexts);
    }

    @Override
    protected AnnotationConfigApplicationContext groupEditAppContext(AnnotationConfigApplicationContext springContext) {
        return editAppContext(springContext);
    }

    protected abstract AnnotationConfigApplicationContext editAppContext(AnnotationConfigApplicationContext springContext);

    protected abstract void contextInit();
}
