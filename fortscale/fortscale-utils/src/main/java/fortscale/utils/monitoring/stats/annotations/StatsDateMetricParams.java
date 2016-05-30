package fortscale.utils.monitoring.stats.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * An annotation for fields of classes that extends StatsMetricGroup. It designates the field as date metric field.
 *
 * Supported data types: long, Long, int, Integer, float, Float, double, Double, AtomicLong, AtomicInteger,
 *                       StatsLongFlexMetric, StatsDoubleFlexMetric
 *
 * The value is convert into date into steps:
 * 1. The field value is converted to date. By default the field contains epoch.
 *    The epoch units (seconds, mSec, uSec or nSec) are converted to seconds automatically (by educated guess)
 * 2. The date is converted into date in long format: YYYYMMDDHHMMSS (14 digit long number). This format is used since
 *    influxdb and Grafana does not support date fields
 *
 * Float data types are rounded to integers
 *
 *    name        - field name. If empty, use the group name from StatsMetricsGroupAttributes
 *
 * Created by gaashh on 5/29/16.
 */
@Repeatable(StatsDateMetricParamsList.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StatsDateMetricParams {
    String name()           default "";
}

