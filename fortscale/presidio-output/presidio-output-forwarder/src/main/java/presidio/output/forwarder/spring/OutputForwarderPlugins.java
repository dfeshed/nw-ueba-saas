package presidio.output.forwarder.spring;


import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RabbitAutoConfiguration.class)
public class OutputForwarderPlugins {

}
