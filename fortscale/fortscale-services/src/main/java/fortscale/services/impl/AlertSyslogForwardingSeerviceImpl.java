package fortscale.services.impl;

import fortscale.services.AlertsService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tomerd on 21/02/2016.
 */
public class AlertSyslogForwardingSeerviceImpl {

	private static Logger logger = Logger.getLogger(AlertSyslogForwardingSeerviceImpl.class);

	public static final String IP_KEY = "system.alertsSyslogForwarding.ip";
	public static final String PORT_KEY = "system.alertsSyslogForwarding.port";
	public static final String SENDING_METHOD_KEY = "system.alertsSyslogForwarding.sendingmethod";

	@Autowired
	private AlertsService alertsService;
	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;

	private String ip;
	private int port;
	private String sendingMethod;


}
