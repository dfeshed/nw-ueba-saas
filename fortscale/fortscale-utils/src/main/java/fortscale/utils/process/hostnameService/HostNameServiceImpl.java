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
    private long cacheAge;

    /**
     * ctor
     *
     * @param cacheAge - hostname is cached for cacheAge seconds.
     *                    if a getHostname request is made and cached period has not finished- hostname will be returned from cache;
     */
    public HostNameServiceImpl(long cacheAge) {
        this.cacheAge = cacheAge;
        getHostname();
    }

    /**
     * get hostname from cache. if cache period has passed: fetch hostname again
     *
     * @return hostname
     */
    @Override
    public synchronized String getHostname() {
        Instant now = Instant.now();
        if (Duration.between(lastHostNameFetch, now).getSeconds() > cacheAge ||
                hostname == null) {
            try {
                logger.debug("preforming hostname fetch, last offest fetched before {} seconds and was {}", lastHostNameFetch,hostname);
                hostname = InetAddress.getLocalHost().getHostName();
                lastHostNameFetch = now;
                logger.debug("hostname: {}",hostname);

            } catch (UnknownHostException e) {
                logger.error("Failed to get current hostname", e);
            }
        }
        return hostname;
    }
}
