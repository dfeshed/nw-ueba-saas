package presidio.manager.api.records;


import java.util.List;

public interface SyslogSenderConfiguration {

    boolean isValid();

    List<String> badParams();
}
