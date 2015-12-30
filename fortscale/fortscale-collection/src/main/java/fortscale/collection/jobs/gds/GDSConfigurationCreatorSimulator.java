package fortscale.collection.jobs.gds;

import fortscale.utils.logging.Logger;

import java.io.IOException;

/**
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
            logger.error("There was an exception during the execution - {}", e);
            System.out.println("There was an exception during execution. please see more info at the log");
        }
    }
}
