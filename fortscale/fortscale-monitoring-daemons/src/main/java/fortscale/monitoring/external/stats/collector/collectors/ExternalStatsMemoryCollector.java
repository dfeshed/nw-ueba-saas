package fortscale.monitoring.external.stats.collector.collectors;

/**
 *
 * This collector collects memory info from proc files.
 * memory info is: total, used, free, real-free, buffers, cache, shared, dirty, swap-in,
 * swap-out, buffer-in, buffer-out -- All in MBytes
 *
 * Created by galiar on 14/04/2016.
 */
public class ExternalStatsMemoryCollector extends AbstractExternalStatsCollector {


    //relevant filenames: meminfo vmstat

    // relevant parse functions: parse single value for both (meminfo and vmstat)

    //when calling to parser util class - don't forget to check if the map is null

    //get relevant data

    //save it to memory metrics


}
