package presidio.output.forwarder.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

@Configuration
@Import(OutputForwarderPlugins.class)
public class OutputForwarderConfigBeans {

    @Bean
    public ForwarderConfiguration forwarderStrategyConfiguration(){
        return new ForwarderConfiguration();
    }

    @Bean
    public ForwarderStrategyFactory forwarderStrategyFactory(){
        return new ForwarderStrategyFactory();
    }
}
