//package fortscale.monitoring.external.stats.linux.collector.metrics;
//
//import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
//import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
//
///**
// * holds the metrics of file system collector.
// * metrics are: file system size, free space and used space. (all in MB)
// * Created by galiar on 02/05/2016.
// */
//public class ExternalStatsFileSystemCollectorMetrics /*extends StatsMetricsGroup*/ { //TODO return the inheritance when the superclass is ready
//
//    public ExternalStatsFileSystemCollectorMetrics(StatsMetricsGroupAttributes attributes) {
//        //super(ExternalStatsOSMemoryCollector.class, attributes); //TODO when the inheritance returns, uncomment
//    }
//
//    @StatsLongMetricParams
//    Long totalFileSystemSize;
//
//    @StatsLongMetricParams
//    Long freeSpace;
//
//    @StatsLongMetricParams
//    Long usedSpace;
//
//    public Long getTotalFileSystemSize() {
//        return totalFileSystemSize;
//    }
//
//    public void setTotalFileSystemSize(Long totalFileSystemSize) {
//        this.totalFileSystemSize = totalFileSystemSize;
//    }
//
//    public Long getFreeSpace() {
//        return freeSpace;
//    }
//
//    public void setFreeSpace(Long freeSpace) {
//        this.freeSpace = freeSpace;
//    }
//
//    public Long getUsedSpace() {
//        return usedSpace;
//    }
//
//    public void setUsedSpace(Long usedSpace) {
//        this.usedSpace = usedSpace;
//    }
//}
