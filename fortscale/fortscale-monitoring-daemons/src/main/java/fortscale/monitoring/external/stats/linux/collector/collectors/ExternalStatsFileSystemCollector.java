//package fortscale.monitoring.external.stats.linux.collector.collectors;
//
//import fortscale.monitoring.external.stats.linux.collector.metrics.ExternalStatsFileSystemCollectorMetrics;
//import fortscale.monitoring.external.stats.collector.impl.linux.parsers.LinuxProcFileParser;
//import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
//import fortscale.utils.system.FileSystemUtils;
//
//import java.util.Map;
//
///**
// * collects metrics about the file system: what its size, free space and used space - in MB
// * Created by galiar on 01/05/2016.
// */
//public class ExternalStatsFileSystemCollector  extends AbstractExternalStatsCollector {
//
//
//    FileSystemUtils fileSystemUtils = new FileSystemUtils();
//
//    private ExternalStatsFileSystemCollectorMetrics fileSystemMetrics = new ExternalStatsFileSystemCollectorMetrics(new StatsMetricsGroupAttributes()); //TODO user real attributes
//    private String rootDir;
//
//    public ExternalStatsFileSystemCollector(String rootDir){
//        this.rootDir = rootDir;
//    }
//
//    @Override
//    public void collect(Map<String, LinuxProcFileParser> parsers) {
//
//        Long freeDiskSpace = convertBytesToMB(fileSystemUtils.getFreeSpace(rootDir));
//        Long totalDiskSpace = convertBytesToMB(fileSystemUtils.getTotalSpace(rootDir));
//        Long usedDiskSpace = totalDiskSpace - freeDiskSpace;
//
//        fileSystemMetrics.setFreeSpace(freeDiskSpace);
//        fileSystemMetrics.setTotalFileSystemSize(totalDiskSpace);
//        fileSystemMetrics.setUsedSpace(usedDiskSpace);
//
//    }
//
//    public ExternalStatsFileSystemCollectorMetrics getFileSystemMetrics() {
//        return fileSystemMetrics;
//    }
//
//    //for testing only
//    public void setFileSystemUtils(FileSystemUtils fileSystemUtils) {
//        this.fileSystemUtils = fileSystemUtils;
//    }
//
//}
