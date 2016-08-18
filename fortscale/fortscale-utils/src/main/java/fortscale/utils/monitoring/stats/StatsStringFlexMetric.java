package fortscale.utils.monitoring.stats;

/**
 *
 * An interface class for flex metric that provides String metric value.
 *
 * The application implements the getValue function and may use it as string field metric.
 *
 * StatsService will call getValue() to get the metric's value
 *
 * Created by gaashh on 5/29/16.
 */
public interface StatsStringFlexMetric {

    String getValue();

}

