package presidio.manager.api.configuration;


import java.util.List;

public interface SyslogSenderConfiguration {

    boolean isValid();

    List<String> badParams();
}
