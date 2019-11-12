package presidio.output.forwarder.spring;

import com.rabbitmq.client.DefaultSaslConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.util.Collections;

@Configuration
@Import(OutputForwarderPlugins.class)
public class OutputForwarderConfigBeans {

    @Value("${presidio.output.forwarder.strategy.name:rabbitMq}")
    private String forwarderStrategyName;

    @Autowired
    RabbitProperties rabbitProperties;

    @Autowired
    public void setExternalSaslConfig(CachingConnectionFactory cachingConnectionFactory) {
        if (rabbitProperties.getSsl().isEnabled() && rabbitProperties.getSsl().getKeyStore() != null) {
            cachingConnectionFactory.getRabbitConnectionFactory().setSaslConfig(DefaultSaslConfig.EXTERNAL);
        }
    }

    @Bean
    public ForwarderConfiguration forwarderStrategyConfiguration(){
        return new ForwarderConfiguration(forwarderStrategyName);
    }

    @Bean
    public ForwarderStrategyFactory forwarderStrategyFactory(){
        return new ForwarderStrategyFactory(Collections.singletonList(forwarderStrategyName));
    }
}
