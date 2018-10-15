package presidio.output.forwarder.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

@Configuration
@Import(OutputForwarderPlugins.class)
public class OutputForwarderConfigBeans {

    @Value("${presidio.output.forwarder.strategy.name:rabbitMq}")
    private String forwarderStrategyName;


    @Bean
    public ForwarderConfiguration forwarderStrategyConfiguration(){
        return new ForwarderConfiguration(forwarderStrategyName);
    }

    @Bean
    public ForwarderStrategyFactory forwarderStrategyFactory(){
        return new ForwarderStrategyFactory();
    }
}
