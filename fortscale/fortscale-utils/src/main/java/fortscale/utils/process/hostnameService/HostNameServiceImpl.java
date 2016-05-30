package fortscale.utils.process.hostnameService;

import fortscale.utils.logging.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;


public class HostNameServiceImpl implements HostnameService {
    private static final Logger logger = Logger.getLogger(HostNameServiceImpl.class);

    private String hostname;
    private Instant lastHostNameFetch;
    private long cachePeriod;

    /**
     * ctor
     *
     * @param cachePeriod - hostname is cached for cacheperiod seconds.
     *                    if a getHostname request is made and cached period has not finished- hostname will be returened from cache;
     */
    public HostNameServiceImpl(long cachePeriod) {
        this.cachePeriod = cachePeriod;
        lastHostNameFetch = Instant.now().minusSeconds(cachePeriod);
        getHostname();
    }

    /**
     * get hostname from cache. if cache period has passed: fetch hostname again
     *
     * @return hostname
     */
    @Override
    public String getHostname() {
        Instant now = Instant.now();
        if (Duration.between(lastHostNameFetch, now).getSeconds() > cachePeriod) {
            try {
                logger.debug("preforming hostname fetch");
                hostname = InetAddress.getLocalHost().getHostName();
                lastHostNameFetch = Instant.now();
                logger.debug("hostname: {}",hostname);

            } catch (UnknownHostException e) {
                logger.error("Failed to get current hostname", e);
            }
        }
        return hostname;
    }
}
