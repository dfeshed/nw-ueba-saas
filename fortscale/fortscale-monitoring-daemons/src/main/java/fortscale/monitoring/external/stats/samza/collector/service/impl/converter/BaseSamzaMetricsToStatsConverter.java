package fortscale.monitoring.external.stats.samza.collector.service.impl.converter;

import com.kenai.jaffl.struct.Struct;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.samza.metricMessageModels.MetricMessage;

import java.util.*;

/**
 * Basic class for samza metrics conversion to stats metrics.
 */
public abstract class BaseSamzaMetricsToStatsConverter {
    protected StatsService statsService;
    private static final Logger logger = Logger.getLogger(BaseSamzaMetricsToStatsConverter.class);
    protected Map metricsMap;

    public BaseSamzaMetricsToStatsConverter(StatsService statsService)
    {
        this.statsService=statsService;
        metricsMap = new HashMap<>();

    }

    public abstract void convert(Map<String, Object> metricEntries, String JobName, long time, String hostname);

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
                String message = String.format("unexpected error happened while manul updating metric with key: %s ",metric);
                logger.error(message ,e);
            }
        }
    }


}
