package fortscale.monitoring;

import fortscale.utils.standardProcess.StandardProcessBase;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by baraks on 4/25/2016.
 */
public abstract class MonitoringProcessGroupCommon extends StandardProcessBase {
    @Override
    protected void groupStart() {
        start();
    }

    @Override
    protected void preGroupInit() {
        init();
        postGroupInit();
    }

    @Override
    protected void postGroupInit() {
        postBaseInit();
    }

    @Override
    protected AnnotationConfigApplicationContext groupEditAppContext(AnnotationConfigApplicationContext springContext) {
        return editAppContext(springContext);
    }

    protected abstract AnnotationConfigApplicationContext editAppContext(AnnotationConfigApplicationContext springContext);

    protected abstract void doStart();

    protected abstract void start();

    protected abstract void stop();

    protected abstract void doStop();

    protected abstract void doInit();

    protected abstract void init();

}
