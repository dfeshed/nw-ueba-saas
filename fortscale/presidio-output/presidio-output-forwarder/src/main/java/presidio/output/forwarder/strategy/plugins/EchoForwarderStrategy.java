package presidio.output.forwarder.strategy.plugins;

import com.google.auto.service.AutoService;
import presidio.output.forwarder.ForwardMassage;
import presidio.output.forwarder.strategy.ForwarderStrategy;

import java.util.List;
import java.util.Map;

@AutoService(ForwarderStrategy.class)
public class EchoForwarderStrategy implements ForwarderStrategy {

    @Override
    public String getName() {
        return "echo";
    }

    @Override
    public void init() {
        System.out.println("init echo forwarder");
    }

    @Override
    public void forward(List<ForwardMassage> messages, PAYLOAD_TYPE type) throws Exception {
        messages.forEach(message ->
            System.out.println(String.format("message %s: %s", type, message))
        );
    }

    @Override
    public void close() {
        System.out.println("closing echo forwarder");
    }
}
