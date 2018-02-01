package presidio.manager.api.configuration;


import java.util.List;

public interface PresidioForwarderConfiguration {

    boolean isValid();

    List<String> badParams();
}
