package presidio.manager.api.records;


import java.util.List;

public interface ForwarderConfiguration {

    boolean isValid();

    List<String> badParams();
}
