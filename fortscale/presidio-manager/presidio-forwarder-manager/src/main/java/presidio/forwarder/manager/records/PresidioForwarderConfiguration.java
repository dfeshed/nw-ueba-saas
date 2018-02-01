package presidio.forwarder.manager.records;


import java.util.List;

public interface PresidioForwarderConfiguration {

    boolean isValid();

    List<String> badParams();
}
