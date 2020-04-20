package presidio.output.forwarder.strategy.plugins.amqp;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.output.forwarder.ForwardMassage;
import presidio.output.forwarder.strategy.ForwarderStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoService(ForwarderStrategy.class)
public class RabbitForwarderStrategy implements ForwarderStrategy {

    @Autowired
    RabbitTemplate rabbitTemplate;

    private Map<String, String> constantHeaders = new HashMap<>();


    @Override
    public String getName() {
        return "rabbitMq";
    }

    @Override
    public void init() {

        System.out.println("init rabbitMq forwarder");
        constantHeaders = ImmutableMap.of("carlos.event.signature_id","UEBAIOC",
                                  "carlos.event.device.vendor","RSA",
                                  "carlos.event.device.product","User Entity Behavior Analytics",
                                  "carlos.event.device.version","1.0.0");
    }

    @Override
    public void forward(List<ForwardMassage> messages, PAYLOAD_TYPE type) throws Exception {

        messages.forEach(message ->
            rabbitTemplate.convertAndSend("carlos.alerts", "", message.getPayload(), m -> {
                m.getMessageProperties().getHeaders().putAll(message.getHeader());
                m.getMessageProperties().getHeaders().putAll(constantHeaders);
                return m;
            })
        );
    }

    @Override
    public void close() {
        System.out.println("closing rabbitMq forwarder");
    }
}
