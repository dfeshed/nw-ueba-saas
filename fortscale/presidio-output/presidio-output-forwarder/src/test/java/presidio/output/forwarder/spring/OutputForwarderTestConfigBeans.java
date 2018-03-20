package presidio.output.forwarder.spring;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.forwarder.MemoryStrategy;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

@Configuration
public class OutputForwarderTestConfigBeans {

        @Bean
        public MemoryStrategy memoryStrategy() {
            return new MemoryStrategy();
        }

        @Bean
        public ForwarderStrategyFactory forwarderStrategyFactory() {
            ForwarderStrategyFactory forwarderStrategyFactory = Mockito.mock(ForwarderStrategyFactory.class);
            Mockito.when(forwarderStrategyFactory.getStrategy(Mockito.anyString())).thenReturn(memoryStrategy());
            return forwarderStrategyFactory;
        }

        @Bean
        public ForwarderConfiguration forwarderConfiguration() {
            ForwarderConfiguration forwarderConfiguration = Mockito.mock(ForwarderConfiguration.class);
            Mockito.when(forwarderConfiguration.getForwardingStrategy(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class))).thenReturn(MemoryStrategy.MEMORY);
            Mockito.when(forwarderConfiguration.isForwardEntity(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class))).thenReturn(true);
            Mockito.when(forwarderConfiguration.getForwardBulkSize(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class))).thenReturn(1);
            return forwarderConfiguration;
        }
}
