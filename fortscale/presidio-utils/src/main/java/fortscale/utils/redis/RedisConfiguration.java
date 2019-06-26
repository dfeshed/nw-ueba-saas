package fortscale.utils.redis;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
public class RedisConfiguration {
    private final String hostName;
    private final int port;

    @Autowired
    public RedisConfiguration(
            @Value("${presidio.redis.host.name:localhost}") String hostName,
            @Value("${presidio.redis.port:6379}") int port) {

        Validate.notBlank(hostName, "hostName cannot be blank.");
        Validate.inclusiveBetween(0, 65535, port, "port must be in the range [0,65535].");
        this.hostName = hostName;
        this.port = port;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(hostName, port));
    }
}
