package fortscale.services.monitoring.stats.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gaashh on 4/4/16.
 */
@Repeatable(StatsNumericMetricParamsList.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StatsNumericMetricParams {
    String name()            default "";
    // TODO type
    double factor()          default 0;
    long   precisionDigits() default 0;
    long   rateSeconds()    default 0;    // 0 = normal operation
}

