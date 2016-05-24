package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import com.kenai.jaffl.struct.Struct;
import fortscale.monitoring.external.stats.samza.collector.service.stats.SamzaMetricCollectorMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.*;
import org.springframework.util.Assert;

/**
 * Basic class for samza metrics conversion to stats metrics.
 */
public abstract class BaseSamzaMetricsToStatsConverter {
    protected StatsService statsService;
    protected SamzaMetricCollectorMetrics samzaMetricCollectorMetrics;
    private static final Logger logger = Logger.getLogger(BaseSamzaMetricsToStatsConverter.class);
    protected Map metricsMap;

    public BaseSamzaMetricsToStatsConverter(StatsService statsService, SamzaMetricCollectorMetrics samzaMetricCollectorMetrics)
    {
        this.samzaMetricCollectorMetrics=samzaMetricCollectorMetrics;
        this.statsService=statsService;
        metricsMap = new HashMap<>();

    }

    public void convert(Map<String, Object> metricEntries, String jobName, long time, String hostname)
    {
        Assert.notEmpty(metricEntries);
        Assert.hasText(jobName);
        Assert.hasText(hostname);
    }

    /**
     * manual update metrics time
     * @param metrics metrics to manual update
     * @param time updated time
     */
    protected  void manualUpdateMetricsMap(Map metrics, long time)
    {
        for ( Object metric: metrics.values()) {
            try {
                ((StatsMetricsGroup)metric).manualUpdate(time);
            }
            catch (Exception e)
            {
                String message = String.format("unexpected error happened while manual updating metric",metric);
                logger.error(message ,e);
            }
        }
    }


}

