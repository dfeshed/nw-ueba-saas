package fortscale.monitoring.grafana.init;

import fortscale.utils.logging.Logger;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;


/**
 * initiating grafana local db
 */
public class GrafanaInit {

    private static final Logger logger = Logger.getLogger(GrafanaInit.class);
    private final int rotate;
    private final boolean overrideDestinationFile;
    private final String dbFilePath;
    private final String dbDestinationFilePath;

    /**
     * ctor
     * @param dbFilePath resource file to copy
     * @param dbDestinationFilePath db destination path
     * @param overrideDestinationFile should file be overridden in case exists?
     * @param rotateFiles if file is overridden, a backup is saved aside. how much backups would you like to save?
     */
    public GrafanaInit(String dbFilePath, String dbDestinationFilePath, boolean overrideDestinationFile, int rotateFiles) {
        this.rotate=rotateFiles;
        this.overrideDestinationFile=overrideDestinationFile;
        this.dbFilePath=dbFilePath;
        this.dbDestinationFilePath=dbDestinationFilePath;
        copyGrafanaDbAndDashboardFiles();
        rotateBackUpFiles();
    }

    /**
     * grafana service can start before metricsadapter. in that case we would like to override grafana's db file.
     * moreover, you should not add new dashboards in production. if you do, you should export them to dashboards folder
     */
    public void rotateBackUpFiles()
    {
        try {
            File dir= new File(Paths.get(dbDestinationFilePath).getParent().toString());
            Object [] files = Arrays.asList(dir.listFiles((FileFilter) FileFileFilter.FILE)).stream().filter(x -> x.getName().contains(".old")).toArray();
            File[] oldFiles = Arrays.copyOf(files,files.length,File[].class);
            Arrays.sort(oldFiles, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
            if (files.length<rotate) {
                return;
            }
            else {
                for (int i=rotate ; i<files.length;i++)
                {
                    oldFiles[i].delete();
                }
            }
        }
        catch (Exception e)
        {
            logger.error("unexpected error while trying to rotate grafana.db backupfiles",e);
        }
    }
    /**
     * this method will not overwrite destination file if exits
     *
     *
     */
    public void copyGrafanaDbAndDashboardFiles() {
        File dbDestinationFile = new File(dbDestinationFilePath);
        File dbDestinationBackupFile = new File(String.format("%s.%s.old",dbDestinationFilePath, Long.toString(Instant.now().getEpochSecond())));
        try {
            if (overrideDestinationFile && dbDestinationFile.exists()) {
                logger.info("overriding grafana db file");
                dbDestinationFile.renameTo(dbDestinationBackupFile);
            }
            if (!overrideDestinationFile && dbDestinationFile.exists()) {
                return;
            }

            Files.copy(Paths.get(dbFilePath), dbDestinationFile.toPath());
        } catch (IOException e) {
            logger.error(String.format("failed to copy grafana db file %s to destination %s", dbFilePath, dbDestinationFilePath), e);
        }


    }
}
