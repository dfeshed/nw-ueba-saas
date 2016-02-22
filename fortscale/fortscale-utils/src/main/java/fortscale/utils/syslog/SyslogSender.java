package fortscale.utils.syslog;

import ch.qos.logback.core.net.SyslogConstants;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;

import java.util.Objects;

/**
 * Created by tomerd on 22/02/2016.
 */
public class SyslogSender {
	final int SYSLOG_MESSAGE_MAX_LENGTH = 4096;
	private SyslogIF syslogger;

	public SyslogSender(String ip, int port, String protocol) {
		SyslogConfigIF syslogConfig;

		// set transport protocol
		if (Objects.equals(protocol.toLowerCase(), "tcp")) {
			syslogConfig = new TCPNetSyslogConfig();
		} else {
			syslogConfig = new UDPNetSyslogConfig();
		}

		syslogConfig.setMaxMessageLength(SYSLOG_MESSAGE_MAX_LENGTH);
		syslogConfig.setHost(ip);
		syslogConfig.setPort(port);
		syslogConfig.setSendLocalName(false);
		syslogConfig.setSendLocalTimestamp(false);

		syslogger = Syslog.createInstance("Fortscale", syslogConfig);

	}

	public SyslogSender(String ip, int port) {
		this(ip, port, "tcp");
	}

	public void sendEvent(String event) {
		syslogger.log(SyslogConstants.INFO_SEVERITY, event);
	}
}
