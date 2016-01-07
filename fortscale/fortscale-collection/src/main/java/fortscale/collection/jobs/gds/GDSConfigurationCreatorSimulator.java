package fortscale.collection.jobs.gds;

import fortscale.utils.logging.Logger;

import java.io.IOException;

/**
 * Generic Data Source configuration creator job runner
 *
 * @author gils
 * 29/12/2015
 */
public class GDSConfigurationCreatorSimulator {

    private static Logger logger = Logger.getLogger(GDSConfigurationCreatorSimulator.class);

    public static void main(String[] args) throws IOException {
        GDSConfigurationCreatorJob GDSConfigurationCreatorJob = new GDSConfigurationCreatorJob();

        try {
            GDSConfigurationCreatorJob.runSteps();
        }
        catch(Exception e)
        {
            logger.error("Exception during execution - {}", e);
            System.out.println("Exception during execution. please see more info at the log");
        }
    }
}
