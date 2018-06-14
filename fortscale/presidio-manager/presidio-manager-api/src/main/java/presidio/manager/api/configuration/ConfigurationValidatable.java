package presidio.manager.api.configuration;


import java.util.List;

public interface ConfigurationValidatable {

    boolean isValid();

    List<String> badParams();

    List<String> missingParams();
}
