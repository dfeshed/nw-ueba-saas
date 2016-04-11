package fortscale.services.monitoring.stats.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * An annotation for fields of classes that extends StatsMetricGroup. It designates the field as double metric field.
 *
 * Supported data types: long, Long, int, Integet, float, Float, double, Double. (int data types are rounded to double)
 *
 *    name - field name. If empty, use the group name from StatsMetricsGroupAttributes
 *    factor - // TODO
 *    rateSeconds - // TODO
 *    precisionDigits - // TODO
 *
 * Created by gaashh on 4/4/16.
 */
@Repeatable(StatsDoubleMetricParamsList.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StatsDoubleMetricParams {
    String name()            default "";
    double factor()          default -1;   // <0 -> no factor
    long   rateSeconds()     default 0;    // 0  -> normal operation
    long   precisionDigits() default 0;
}
