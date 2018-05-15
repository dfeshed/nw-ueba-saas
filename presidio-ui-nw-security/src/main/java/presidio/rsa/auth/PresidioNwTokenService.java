package presidio.rsa.auth;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;

/**
 * A cache service for the authentication base on token string
 *
 */
public class PresidioNwTokenService {

    private static final Logger logger = LoggerFactory.getLogger(PresidioNwTokenService.class);
    private static final Cache restApiAuthTokenCache = CacheManager.getInstance().getCache("restApiAuthTokenCache");
    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;

    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    public void evictExpiredTokens() {
        logger.info("Evicting expired tokens");
        restApiAuthTokenCache.evictExpiredElements();
    }

    public String generateNewToken() {
        return UUID.randomUUID().toString();
    }

    public void store(String token, PresidioNwAuthenticationToken authentication) {
        restApiAuthTokenCache.put(new Element(token, authentication));
    }

    public boolean contains(String token) {
        return restApiAuthTokenCache.get(token) != null;
    }

    public PresidioNwAuthenticationToken retrieve(String token) {
        Element element = restApiAuthTokenCache.get(token);
        return element == null ? null : (PresidioNwAuthenticationToken) element.getObjectValue();
    }
}
