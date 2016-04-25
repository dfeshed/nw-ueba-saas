package fortscale.services.hdfs;

import fortscale.utils.cleanup.CleanupUtil;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.services.impala.ImpalaService;
import fortscale.utils.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class HDFSService implements CleanupUtil {

    private static Logger logger = Logger.getLogger(HDFSService.class);

    private enum PartitionType { daily, monthly }

    private String DEFAULT_PARTITION_TYPE = PartitionType.daily.name();

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

    @Autowired
    private ImpalaService impalaUtils;

    /***
     *
     * This method deletes all partitions from HDFS
     *
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean deleteAllEntities(boolean doValidate) {
        logger.info("attempting to delete all HDFS folders");
        FileSystem hadoopFS;
        try {
            hadoopFS = getHadoopFileSystem();
        } catch (IOException ex) {
            logger.error("failed to get hadoop fs - {}", ex);
            return false;
        }
        boolean dataSuccess = deletePath(hadoopFS, dataPath, doValidate);
        boolean rawDataSuccess = deletePath(hadoopFS, rawDataPath, doValidate);
        boolean enrichedDataSuccess = deletePath(hadoopFS, enrichedDataPath, doValidate);
        boolean processedDataSuccess = deletePath(hadoopFS, processedDataPath, doValidate);
        closeHadoopFS(hadoopFS);
        if (doValidate) {
            impalaUtils.refreshAllTables();
        }
        return dataSuccess && rawDataSuccess && enrichedDataSuccess && processedDataSuccess;
    }

    /***
     *
     * This method deletes a list of files/folders from the file system
     *
     * @param hdfsPaths   a list of paths to delete
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    public boolean deleteEntities(Collection<String> hdfsPaths, boolean doValidate) {
        int numberOfDeletedEntities = 0;
        FileSystem hadoopFS;
        try {
            hadoopFS = getHadoopFileSystem();
        } catch (IOException ex) {
            logger.error("failed to get hadoop fs - {}", ex);
            return false;
        }
        logger.debug("attempting to delete {} files/folders from hdfs", hdfsPaths.size());
        for (String hdfsPath: hdfsPaths) {
            if (deletePath(hadoopFS, hdfsPath, doValidate)) {
                numberOfDeletedEntities++;
            }
        }
        closeHadoopFS(hadoopFS);
        if (doValidate) {
            impalaUtils.refreshAllTables();
        }
        if (numberOfDeletedEntities == hdfsPaths.size()) {
            logger.info("deleted all {} files/folders", hdfsPaths.size());
            return true;
        }
        logger.error("failed to delete all {} files/folders, deleted only {}", hdfsPaths.size(),
                numberOfDeletedEntities);
        return false;
    }

    /***
     *
     * This method deletes the path provided from the file system
     *
     * @param hadoopFS    hadoop file system object
     * @param hdfsPath    a path to delete
     * @param doValidate  flag to determine should we perform validations
     * @return
     */
    private boolean deletePath(FileSystem hadoopFS, String hdfsPath, boolean doValidate) {
        boolean success = false;
        if (!hdfsPath.contains(basePath)) {
            hdfsPath = basePath + "/" + hdfsPath;
        }
        hdfsPath = hdfsPath.trim();
        logger.debug("attempting to remove {}", hdfsPath);
        try {
            hadoopFS.delete(new Path(hdfsPath), true);
            if (doValidate) {
                if (!hadoopFS.exists(new Path(hdfsPath))) {
                    success = true;
                    logger.info("{} deleted successfully", hdfsPath);
                } else {
                    logger.error("failed to remove {}", hdfsPath);
                }
            } else {
                success = true;
            }
        } catch (Exception ex) {
            logger.error("failed to remove path {} - {}", hdfsPath, ex.getMessage());
        }
        return success;
    }

    /***
     *
     * This method receives a folder containing backup csv files and restores them to HDFS
     *
     * @param backupPath
     * @return
     */
    public boolean restoreSnapshot(String backupPath) {
        logger.info("attempting to restore snapshot");
        File directory = new File(backupPath);
        if (!directory.exists() || !directory.isDirectory()) {
            logger.error("failed to find backup folder");
            return false;
        }
        FileSystem hadoopFS;
        try {
            hadoopFS = getHadoopFileSystem();
        } catch (IOException ex) {
            logger.error("failed to get hadoop fs - {}", ex);
            return false;
        }
        Iterator<File> iterator = FileUtils.iterateFiles(directory, new String[] { "csv" }, true);
        while (iterator.hasNext()) {
            File file = iterator.next();
            String restorePath = file.getAbsolutePath();
            String hdfsPath = restorePath.replace(backupPath, "");
            try {
                if (!restoreFile(hadoopFS, hdfsPath, restorePath)) {
                    logger.error("failed to restore file {} to {}", restorePath, hdfsPath);
                    closeHadoopFS(hadoopFS);
                    impalaUtils.refreshAllTables();
                    return false;
                }
            } catch (IOException ex) {
                logger.error("failed to restore file {} to {} - {}", restorePath, hdfsPath, ex);
                closeHadoopFS(hadoopFS);
                impalaUtils.refreshAllTables();
                return false;
            }
        }
        closeHadoopFS(hadoopFS);
        impalaUtils.refreshAllTables();
        return true;
    }

    /***
     *
     * This method attempts to restore a file from the local file system to HDFS
     *
     * @param hadoopFS     hadoop file system object
     * @param hdfsPath     the path to the file to be deletes on HDFS
     * @param restorePath  the path to the local backup file to be uploaded instead
     * @return
     */
    private boolean restoreFile(FileSystem hadoopFS, String hdfsPath, String restorePath) throws IOException {
        boolean success = false;
        final String TEMPSUFFIX = "_clean-job-temp-suffix";
        if (!hdfsPath.contains(basePath)) {
            hdfsPath = basePath + "/" + hdfsPath;
        }
        hdfsPath = hdfsPath.trim();
        restorePath = restorePath.trim();
        logger.debug("verify that resources exist");
        if (!hadoopFS.exists(new Path(hdfsPath)) || !new File(restorePath).exists()) {
            logger.error("no origin or backup resource found");
            return success;
        }
        String tempResourceName = hdfsPath + TEMPSUFFIX;
        tempResourceName = tempResourceName.trim();
        logger.debug("verify that destination resource temp name doesn't exist");
        //sanity check - shouldn't be found
        if (hadoopFS.exists(new Path(tempResourceName))) {
            logger.info("temp resource {} already exists, deleting...", tempResourceName);
            if (!deletePath(hadoopFS, tempResourceName, true)) {
                logger.error("failed to delete temp resource, manually delete it before continuing");
                return success;
            }
        }
        try {
            logger.debug("renaming origin collection {} to {}", hdfsPath, tempResourceName);
            if (!hadoopFS.rename(new Path(hdfsPath), new Path(tempResourceName))) {
                logger.error("renaming failed, abort");
                return success;
            }
            logger.debug("uploading backup file {} to {}", restorePath, hdfsPath);
            hadoopFS.copyFromLocalFile(false, true, new Path(restorePath), new Path(hdfsPath));
            if (!hadoopFS.exists(new Path(hdfsPath))) {
                logger.error("failed to upload {}", hdfsPath);
                return success;
            }
            logger.info("snapshot restored");
            success = true;
            //remove old file
            if (!deletePath(hadoopFS, tempResourceName, true)) {
                logger.warn("deleting temporary file {} failed, delete manually", tempResourceName);
            }
            return success;
        } catch (Exception ex) {
            logger.error("snapshot failed to restore - {}", ex);
        }
        logger.error("snapshot failed to restore - manually upload backup file");
        return success;
    }

    /***
     *
     * This method deletes entities from HDFS according to their start/end dates
     *
     * @param hdfsPath       the base path on the file system to delete from
     * @param partitionType  partition type is daily/monthly etc.
     * @param startDate      start date after which items will be deleted
     * @param endDate        end date before which items will be deleted
     * @return
     */
    @Override
    public boolean deleteEntityBetween(String hdfsPath, String partitionType, Date startDate, Date endDate) {
        if ((startDate == null && endDate != null) || (startDate != null && endDate == null)) {
            logger.error("must provide both start and end dates to run");
            return false;
        }
        if (startDate != null && endDate != null) {
            hdfsPath = buildFileList(hdfsPath, partitionType, startDate, endDate);
        }
        FileSystem hadoopFS;
        try {
            hadoopFS = getHadoopFileSystem();
        } catch (Exception ex) {
            logger.error("failed to delete path {} - {}", hdfsPath, ex);
            return false;
        }
        boolean success = deletePath(hadoopFS, hdfsPath, true);
        closeHadoopFS(hadoopFS);
        impalaUtils.refreshAllTables();
        return success;
    }

    /***
     *
     * This method shuts down the hadoop fs descriptor
     *
     * @param hadoopFS  hadoop file system object
     */
    private void closeHadoopFS(FileSystem hadoopFS) {
        if (hadoopFS != null) {
            try {
                hadoopFS.close();
            } catch (Exception ex) {}
        }
    }

    /***
     *
     * This method builds the file list to delete according the partition type
     *
     * @param hdfsBasePath   the base path on the file system to delete from
     * @param partitionType  partition type is daily/monthly etc.
     * @param startDate      start date after which items will be deleted
     * @param endDate        end date before which items will be deleted
     * @return
     */
    private String buildFileList(String hdfsBasePath, String partitionType, Date startDate, Date endDate) {
        StringBuilder sb = new StringBuilder();
        //if partition type is invalid or not supported yet
        if (!EnumUtils.isValidEnum(PartitionType.class, partitionType)) {
            //revert to default partition type
            partitionType = DEFAULT_PARTITION_TYPE;
        }
        PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy(partitionType);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        //creating list of files by advancing the date one unit at a time (according to strategy) from start to end
        while (calendar.getTimeInMillis() < endDate.getTime()) {
            sb.append(hdfsBasePath + "/" + partitionStrategy.getImpalaPartitionName(calendar.getTimeInMillis()) + " ");
            if (partitionStrategy instanceof MonthlyPartitionStrategy) {
                calendar.add(Calendar.MONTH, 1);
            } else {
                calendar.add(Calendar.DATE, 1);
            }
        }
        return sb.toString();
    }

    /***
     *
     * This method initializes the Hadoop FS
     *
     * @return
     * @throws IOException
     */
    private FileSystem getHadoopFileSystem() throws IOException {
        Configuration hadoopFSConf = new Configuration();
        hadoopFSConf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
        hadoopFSConf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
        hadoopFSConf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        hadoopFSConf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        return FileSystem.get(hadoopFSConf);
    }

}