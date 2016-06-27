package fortscale.collection.metrics;

/**
 * Created by gaashh on 5/29/16.
 */

import fortscale.collection.jobs.event.process.EventProcessJob;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for HDFSWriterStreamTask
 * Note: StreamingTaskCommonMetrics provides the common stream task metrics
 */
@StatsMetricsGroupParams(name = "etl.Event-Process.job")
public class ETLCommonJobMetircs extends StatsMetricsGroup {

    public ETLCommonJobMetircs(StatsService statsService, String dataSource) {
        // Call parent ctor
        super(statsService, EventProcessJob.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("data-source", dataSource);
                    }
                }
        );

    }

    // Number of event messages with unknown data source
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long lines;


    @StatsDoubleMetricParams(rateSeconds = 1)
    public long linesSuccessfully;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long linesTotalFailures;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long linesFailuresInMorphline;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long linesFailuresInMorphlineEnrichment;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long linesFailuresInTecordToHadoopString;


    @StatsDoubleMetricParams(rateSeconds = 1)
    public long processExecutions;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long processFiles;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long processFilesSuccessfully;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long processFilesFailures;

    //The metric count how many files completed, but had at least one failed line
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long processFilesSuccessfullyWithFailedLines;


//    // Number of messages without HDFS writer (e.g. bad config key)
//    @StatsDoubleMetricParams(rateSeconds = 1)
//    public long HDFSWriterNotFoundMessages;
//
//    // Number of task coordinate calls
//    @StatsDoubleMetricParams(rateSeconds = 1)
//    public long coordinate;
//
//    // Number of task coordinate calls exceptions
//    @StatsDoubleMetricParams(rateSeconds = 1)
//    public long coordinateExceptions;

}


