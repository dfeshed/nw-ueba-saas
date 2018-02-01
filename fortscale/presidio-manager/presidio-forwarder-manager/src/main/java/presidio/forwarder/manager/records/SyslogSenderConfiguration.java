package presidio.forwarder.manager.records;


import java.util.List;

public interface SyslogSenderConfiguration {

    boolean isValid();

    List<String> badParams();
}
