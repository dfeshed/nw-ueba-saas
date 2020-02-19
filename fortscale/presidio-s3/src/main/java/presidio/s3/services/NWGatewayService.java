package presidio.s3.services;

import com.amazonaws.services.s3.AmazonS3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import static fortscale.common.s3.NWGateway.isHourReady;

/**
 * A netwitness gateway service that supply services over s3.
 */

public class NWGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(NWGatewayService.class);
    private String bucketName;
    private String tenant;
    private String account;
    private String region;
    private AmazonS3 s3;

    public NWGatewayService(String bucketName, String tenant, String account, String region, AmazonS3 s3) {
        this.bucketName = bucketName;
        this.tenant = tenant;
        this.account = account;
        this.region = region;
        this.s3 = s3;
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
                if (isHourReady(endDate, schema, bucketName, tenant, account, region, s3)){
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


