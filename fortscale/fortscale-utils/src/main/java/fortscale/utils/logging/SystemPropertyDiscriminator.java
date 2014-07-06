package fortscale.utils.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;

public class SystemPropertyDiscriminator extends AbstractDiscriminator<ILoggingEvent> {

	private String key;

	@Override
	public String getDiscriminatingValue(ILoggingEvent e) {
		try {
			return System.getProperty(key, "");
		} catch (SecurityException | NullPointerException | IllegalArgumentException  exp) {
			return "";
		}
	}

	@Override
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

}
