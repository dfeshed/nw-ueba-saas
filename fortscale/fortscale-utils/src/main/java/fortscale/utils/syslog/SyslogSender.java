package fortscale.utils.syslog;

import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.sender.AbstractSyslogMessageSender;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;

import java.io.IOException;

/**
 * Created by tomerd on 22/02/2016.
 */
public class SyslogSender {

	private static final String TCP_PROTOCOL = "tcp";
	final int CONNECTION_TIMEOUT = 2000;
	final boolean USE_SSL = false;
	final int MAX_RETRIES = 3;

	AbstractSyslogMessageSender messageSender;

	public SyslogSender(String ip, int port, String protocol, MessageFormat messageFormat) {
		messageSender = createMessageSender(ip, port, protocol, messageFormat);
	}

	public SyslogSender(String ip, int port, MessageFormat messageFormat) {
		this(ip, port, "tcp", messageFormat);
	}

	/**
	 * Send event using syslog protocol
	 * @param event
	 */
	public boolean sendEvent(String event) {
		boolean sendSuccessfully = false;
		int numberOfRetries = 0;

		while (numberOfRetries < MAX_RETRIES && !sendSuccessfully) {
			try {
				messageSender.sendMessage(event);
				sendSuccessfully = true;
			} catch (IOException e) {
				numberOfRetries++;
			}
		}

		return sendSuccessfully;
	}

	/**
	 * Create syslog sender
	 *
	 * @param ip
	 * @param port
	 * @param protocol
	 * @param messageFormat
	 * @return
	 */
	private AbstractSyslogMessageSender createMessageSender(String ip, int port, String protocol, MessageFormat messageFormat) {
		AbstractSyslogMessageSender messageSender = null;
		if (protocol.equalsIgnoreCase(TCP_PROTOCOL)) {
			messageSender = new TcpSyslogMessageSender();
			((TcpSyslogMessageSender) messageSender).setSyslogServerHostname(ip);
			((TcpSyslogMessageSender) messageSender).setSyslogServerPort(port);
			((TcpSyslogMessageSender) messageSender).setSocketConnectTimeoutInMillis(CONNECTION_TIMEOUT);
			((TcpSyslogMessageSender) messageSender).setSsl(USE_SSL);
		} else {
			messageSender = new UdpSyslogMessageSender();
			((UdpSyslogMessageSender) messageSender).setSyslogServerHostname(ip);
			((UdpSyslogMessageSender) messageSender).setSyslogServerPort(port);
		}
		messageSender.setMessageFormat(messageFormat);

		return messageSender;
	}
}
