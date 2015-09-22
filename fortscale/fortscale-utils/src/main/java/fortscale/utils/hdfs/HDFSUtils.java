package fortscale.utils.hdfs;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Amir Keren on 22/09/15.
 */
public class HDFSUtils {

    private static Logger logger = Logger.getLogger(HDFSUtils.class);

    @Value("${hdfs.user.data.path}")
    private String dataPath;
    @Value("${hdfs.user.rawdata.path}")
    private String rawDataPath;
    @Value("${hdfs.user.enricheddata.path}")
    private String enrichedDataPath;
    @Value("${hdfs.user.processeddata.path}")
    private String processedDataPath;

    public boolean deleteAllHDFS() {
        return deleteHDFSPath(dataPath) && deleteHDFSPath(rawDataPath) &&
                deleteHDFSPath(enrichedDataPath) && deleteHDFSPath(processedDataPath);
    }

    private boolean deleteHDFSPath(String hdfsPath) {
        boolean success = false;
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

    public boolean deleteHDFSFilesBetween(String hdfsPath, Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            hdfsPath = buildFileList(hdfsPath, startDate, endDate);
        }
        return deleteHDFSPath(hdfsPath);
    }

    private String buildFileList(String hdfsPath, Date startDate, Date endDate) {
        StringBuilder sb = new StringBuilder();
        //TODO - generalize this to account for different strategies (monthly partitions for example)
        DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        //creating list of files by advancing the date one day at a time from startDate to endDate
        while (calendar.getTimeInMillis() < endDate.getTime()) {
            sb.append(hdfsPath + sdf.format(calendar.getTime()) + " ");
            calendar.add(Calendar.DATE, 1);
        }
        return sb.toString();
    }

}