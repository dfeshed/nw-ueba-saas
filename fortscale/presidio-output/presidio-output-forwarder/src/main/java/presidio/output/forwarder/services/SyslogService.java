package presidio.output.forwarder.services;

import com.cloudbees.syslog.*;
import com.cloudbees.syslog.sender.SyslogMessageSender;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;
import org.springframework.data.util.Pair;
import presidio.output.forwarder.handlers.syslog.SyslogEventsEnum;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyslogService {

    private static final String APP_NAME = "UEBA";
    private static final String METADATA_TAG = "meta@presidio";
    private static final String EVENT_ID = "eventID";

    private static final int SOCKET_CONNECT_TIMEOUT_IN_MILLIS = 60000;

    private String hostName = "localhost";
    private String pid;

    private Map<Pair<String, Integer>, SyslogMessageSender> messageSenders;

    public SyslogService() {
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
        }
        ;
        pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        messageSenders = new ConcurrentHashMap<>();
    }

    public void send(SyslogEventsEnum type, String id, String message, String host, int port) throws IOException {

        SyslogMessageSender messageSender = messageSenders.computeIfAbsent(Pair.of(host, port),
                                                                           k -> getMessageSender(host, port));
        SyslogMessage syslogMessage = new SyslogMessage()
                .withFacility(Facility.USER)
                .withTimestamp(new Date())
                .withHostname(hostName)
                .withAppName(APP_NAME)
                .withSeverity(Severity.INFORMATIONAL)
                .withProcId(pid)
                .withMsgId(type.getValue())
                .withMsg(message);

        SDElement sdElement = new SDElement(METADATA_TAG);
        SDParam sdParam = new SDParam(EVENT_ID, id);
        sdElement.addSDParam(sdParam);
        syslogMessage.withSDElement(sdElement);

        messageSender.sendMessage(syslogMessage);
    }

    private SyslogMessageSender getMessageSender(String host, int port) {
        TcpSyslogMessageSender messageSender = new TcpSyslogMessageSender();
        messageSender.setMessageFormat(MessageFormat.RFC_5424); // optional, default is RFC 3164
        messageSender.setSocketConnectTimeoutInMillis(SOCKET_CONNECT_TIMEOUT_IN_MILLIS);
        messageSender.setSyslogServerHostname(host);
        messageSender.setSyslogServerPort(port);
        return messageSender;
    }

}
