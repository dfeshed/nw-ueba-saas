package fortscale.services.monitoring.stats.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gaashh on 4/4/16.
 */
@Repeatable(StatsNumericMetricList.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StatsNumericMetricParams {
    String name()          default "";
    // TODO type
    double scale()         default 0;    // TODO
    //double precisionDigits() default 0;  // TODO
    int    ratioSeconds()    default 0;   // TODO
}
