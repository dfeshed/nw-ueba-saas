package fortscale.utils.monitoring.stats;

/**
 *
 * An interface class for flex metric that provides Double metric value.
 *
 * The application implements the getValue function and may use it as numeric field metric.
 *
 * StatsService will call getValue() to get the metric's value
 *
 * Created by gaashh on 4/19/16.
 */
public interface StatsDoubleFlexMetric {

    Double getValue();

}
