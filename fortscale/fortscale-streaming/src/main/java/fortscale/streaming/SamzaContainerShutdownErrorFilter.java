package fortscale.streaming;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Filter logging events coming from samza container with
 * java.lang.InterruptedException, as this is a normal graceful notification
 * that we want to swallow and not propagate to the log
 */
public class SamzaContainerShutdownErrorFilter extends Filter<ILoggingEvent> {

	@Override
	public FilterReply decide(ILoggingEvent event) {
		
		if (("org.apache.samza.container.SamzaContainer".equals(event.getLoggerName()) ||
			 "org.apache.samza.job.local.ThreadJob".equals(event.getLoggerName())) &&
			"java.lang.InterruptedException".equals(event.getThrowableProxy().getClassName())) {
			
			return FilterReply.DENY;
		} else {
			return FilterReply.NEUTRAL;
		}
	}

}
