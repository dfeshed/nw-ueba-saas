package fortscale.utils.monitoring.stats.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * An annotation for fields of classes that extends StatsMetricGroup. It designates the field as string metric field.
 *
 * Supported data types: String, StatsStringFlexMetric
 *
 * Notes:
 *  1. While influxdb support strings, Grafana does not.
 *  2. Strings consumes a lot of disk space comparing to number. Use with great care and store rarely (e.g. only hourly)
 *
 *    name        - field name. If empty, use the group name from StatsMetricsGroupAttributes
 *
 * Created by gaashh on 5/29/16.
 */
@Repeatable(StatsStringMetricParamsList.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StatsStringMetricParams {
    String name()           default "";
}


