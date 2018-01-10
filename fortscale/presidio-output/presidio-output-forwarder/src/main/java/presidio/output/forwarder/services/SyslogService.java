package presidio.output.forwarder.services;

import com.cloudbees.syslog.*;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class SyslogService {

    private static final String APP_NAME = "presidio";

    private String hostName = "localhost";
    private String pid;

    public SyslogService() {
        try {hostName = InetAddress.getLocalHost().getHostName();} catch (UnknownHostException ex) {};
        pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }


    public void send(String type, String id, String message, String host, int port) throws IOException {

        TcpSyslogMessageSender  messageSender = new TcpSyslogMessageSender();
        messageSender.setSyslogServerHostname(host);
        messageSender.setSyslogServerPort(port);
        messageSender.setMessageFormat(MessageFormat.RFC_5424); // optional, default is RFC 3164
        messageSender.setSsl(false);

        SyslogMessage syslogMessage = new SyslogMessage()
                    .withFacility(Facility.USER)
                    .withTimestamp(new Date())
                    .withHostname(hostName)
                    .withAppName(APP_NAME)
                    .withSeverity(Severity.INFORMATIONAL)
                    .withProcId(pid)
                    .withMsgId(type)
                    .withMsg(message);


            SDElement sdElement = new SDElement("meta@presidio");
            SDParam sdParam = new SDParam("eventID", id);
            sdElement.addSDParam(sdParam);
            syslogMessage.withSDElement(sdElement);


        messageSender.sendMessage(syslogMessage);


    }

}
