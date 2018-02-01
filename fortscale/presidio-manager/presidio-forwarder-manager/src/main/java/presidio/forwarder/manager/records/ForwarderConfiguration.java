package presidio.forwarder.manager.records;


import java.util.List;

public interface ForwarderConfiguration {

    boolean isValid();

    List<String> badParams();
}
