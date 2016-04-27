package fortscale.utils.monitoring.stats.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * An annotation for fields of classes that extends StatsMetricGroup. It designates the field as double metric field.
 *
 * Supported data types: long, Long, int, Integer, float, Float, double, Double, AtomicLong, AtomicInteger,
 *                       StatsLongFlexMetric, StatsDoubleFlexMetric
 *
 *    name        - field name. If empty, use the group name from StatsMetricsGroupAttributes
 *    factor      - multiply the value by the factor. The default, negative value indicates no factor.
 *    rateSeconds - calc the value change rate between the last two scans. The rate is over the rate seconds period. for
 *                  example: rateSeconds = 1 => events/seconds, rateSeconds = 3600 => events/hour.
 *                  formula is value = (v2-v1)/(t2-t1)*rateSeconds.
 *                  if t1=t2 or if there is no previous sample, value is not written to the engine
 *                  default is rateSeconds = 0 => normal operation (no rate calculation)
 *    precisionDigits - round the number to 10^precisionDigits. zero (The default), don't round.
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
