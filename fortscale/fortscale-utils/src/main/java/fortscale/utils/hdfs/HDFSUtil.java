package fortscale.utils.hdfs;

import fortscale.utils.CustomUtil;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class HDFSUtil implements CustomUtil {

    private static Logger logger = Logger.getLogger(HDFSUtil.class);

    @Value("${hdfs.user.path}")
    private String basePath;
    @Value("${hdfs.user.data.path}")
    private String dataPath;
    @Value("${hdfs.user.rawdata.path}")
    private String rawDataPath;
    @Value("${hdfs.user.enricheddata.path}")
    private String enrichedDataPath;
    @Value("${hdfs.user.processeddata.path}")
    private String processedDataPath;

    public boolean deleteAll() {
        return deletePath(dataPath) && deletePath(rawDataPath) &&
               deletePath(enrichedDataPath) && deletePath(processedDataPath);
    }

    public boolean deleteFiles(Collection<String> hdfsPaths) {
        int numberOfDeletedFiles = 0;
        logger.debug("attempting to delete {} files from hdfs", hdfsPaths.size());
        for (String hdfsPath: hdfsPaths) {
            if (deletePath(hdfsPath)) {
                numberOfDeletedFiles++;
            }
        }
        if (numberOfDeletedFiles == hdfsPaths.size()) {
            logger.info("deleted all {} files", hdfsPaths.size());
            return true;
        }
        logger.error("failed to delete all {} files, deleted only {}", hdfsPaths.size(), numberOfDeletedFiles);
        return false;
    }

    private boolean deletePath(String hdfsPath) {
        boolean success = false;
        hdfsPath = basePath + "/" + hdfsPath;
        logger.debug("attempting to remove {}", hdfsPath);
        try {
            Process process = Runtime.getRuntime().exec("hdfs dfs -rm -r -skipTrash " + hdfsPath);
            if (process.waitFor() != 0) {
                logger.error("failed to remove {}", hdfsPath);
            } else {
                process = Runtime.getRuntime().exec("hdfs dfs -ls " + hdfsPath);
                if (process.waitFor() != 0) {
                    success = true;
                    logger.info("deleted successfully");
                } else {
                    logger.error("failed to remove {}", hdfsPath);
                }
            }
        } catch (Exception ex) {
            logger.error("failed to remove partition {} - {}", hdfsPath, ex.getMessage());
        }
        return success;
    }

    @Override
    public boolean restoreSnapshot(String hdfsPath, String restorePath) {
        boolean success = false;
        hdfsPath = basePath + "/" + hdfsPath;
        logger.debug("check if backup file exists");
        if (new File(restorePath).exists()) {
            logger.debug("delete destination file");
            if (!deletePath(hdfsPath)) {
                logger.debug("deleting failed, abort");
                return success;
            }
            logger.debug("uploading backup file");
            try {
                Process process = Runtime.getRuntime().exec("hdfs dfs -put " + restorePath + " " + hdfsPath);
                if (process.waitFor() != 0) {
                    logger.error("failed to remove {}", hdfsPath);
                } else {
                    process = Runtime.getRuntime().exec("hdfs dfs -ls " + hdfsPath);
                    if (process.waitFor() == 0) {
                        success = true;
                        logger.info("snapshot restored");
                    } else {
                        logger.error("snapshot failed to restore - could not upload file");
                    }
                }
            } catch (Exception ex) {
                logger.error("snapshot failed to restore - manually upload backup file");
                return success;
            }
        } else {
            logger.error("snapshot failed to restore - no backup file {} found", restorePath);
            return success;
        }
        logger.error("snapshot failed to restore - manually upload backup file");
        return success;
    }

    @Override
    public boolean deleteEntityBetween(String hdfsPath, String partitionType, Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            hdfsPath = buildFileList(hdfsPath, partitionType, startDate, endDate);
        }
        return deletePath(hdfsPath);
    }

    private String buildFileList(String hdfsBasePath, String partitionType, Date startDate, Date endDate) {
        StringBuilder sb = new StringBuilder();
        PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy(partitionType);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        //creating list of files by advancing the date one unit at a time (according to strategy) from start to end
        while (calendar.getTimeInMillis() < endDate.getTime()) {
            sb.append(hdfsBasePath + partitionStrategy.getImpalaPartitionName(calendar.getTimeInMillis()) + " ");
            if (partitionStrategy instanceof MonthlyPartitionStrategy) {
                calendar.add(Calendar.MONTH, 1);
            } else {
                calendar.add(Calendar.DATE, 1);
            }
        }
        return sb.toString();
    }

}