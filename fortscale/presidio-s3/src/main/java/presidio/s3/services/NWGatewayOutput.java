package presidio.s3.services;

import com.amazonaws.services.s3.AmazonS3;
import fortscale.common.s3.NWGatewayService;
import fortscale.utils.logging.Logger;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

/**
 * A netwitness gateway output service that supply services over S3.
 */

public class NWGatewayOutput {

    private static final Logger logger = Logger.getLogger(NWGatewayOutput.class);
    private NWGatewayService nwGatewayService;

    public NWGatewayOutput(String bucketName, String tenant, String region, AmazonS3 s3) {
        this.nwGatewayService = new NWGatewayService(bucketName, tenant, region, s3);
    }

    /**
     * Check and wait for hour to be ready for reading by checking if later file is exists.
     *
     * @param endDate   the hour end time
     * @param schema    the data schema
     * @param timeToSleepInSeconds    the time to sleep between each iteration
     * @param timeout    the execution timeout
     *
     * @return true if ready
     * @throws InterruptedException if the current thread is interrupted
     */
    public boolean waitTillHourIsReady(Instant endDate, String schema, int timeToSleepInSeconds, int timeout) throws InterruptedException, TimeoutException {
        long endTimeMillis = System.currentTimeMillis() + 1000 * timeout;

        while (true) {
            if (System.currentTimeMillis() < endTimeMillis) {
                if (nwGatewayService.isHourReady(endDate, schema)){
                logger.info("Hour {} is ready!.", endDate);
                return true;
                }
                else {
                    logger.info("Hour {} is not ready!, going to sleep for {} seconds.", endDate, timeToSleepInSeconds);
                    Thread.sleep(timeToSleepInSeconds * 1000);
                }
            } else {
                throw new TimeoutException(String.format("timeout %d seconds has been exceeded.", timeout));
            }
        }
    }
}


