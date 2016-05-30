package fortscale.monitoring.grafana.init;

import fortscale.utils.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * initiating grafana local db
 */
public class GrafanaInit {

    private static final Logger logger = Logger.getLogger(GrafanaInit.class);

    public GrafanaInit(String dbFilePath, String dbDestinationFilePath) {
        copyGrafanaDbAndDashboardFiles(dbFilePath, dbDestinationFilePath);
    }

    /**
     * this method will not overwrite destination file if exits
     *
     * @param dbFilePath database file path
     * @param dbDestinationFilePath destination to copy into
     */
    public void copyGrafanaDbAndDashboardFiles(String dbFilePath, String dbDestinationFilePath) {
        File dbDestinationFile = new File(dbDestinationFilePath);
        if (!dbDestinationFile.exists()) {
            try {
                Files.copy(Paths.get(dbFilePath), dbDestinationFile.toPath());
            } catch (IOException e) {
                logger.error(String.format("failed to copy grafana db file %s to destination %s", dbFilePath, dbDestinationFilePath), e);
            }
        }

    }
}
