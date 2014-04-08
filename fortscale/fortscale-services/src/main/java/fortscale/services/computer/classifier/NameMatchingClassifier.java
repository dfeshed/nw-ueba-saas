package fortscale.services.computer.classifier;

import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageClassifier;
import fortscale.domain.core.ComputerUsageType;
import fortscale.services.computer.EndpointClassifier;

/**
 * Classify machine according to regular expressions for naming conventions
 */
@Component
public class NameMatchingClassifier implements EndpointClassifier {

	public static final String CLASSIFIER_NAME = "NameMatchingClassifier";
	
	@Value("${endpoint.desktop.name.regex}")
	private String desktopRegex;
	@Value("${endpoint.server.name.regex}")
	private String serverRegex;
	
	private Pattern desktopPattern;
	private Pattern serverPattern;
	
	public void setDesktopRegex(String desktopRegex) {
		this.desktopRegex = desktopRegex;
		this.desktopPattern = null;
	}
	
	public void setServerRegex(String serverRegex) {
		this.serverRegex = serverRegex;
		this.serverPattern = null;
	}
	
	@Override
	public boolean canClassify(Computer computer) {
		return computer!=null && StringUtils.isNotEmpty(computer.getName());
	}

	@Override
	public boolean classify(Computer computer) {
		if (!canClassify(computer))
			return false;
		
		// ensure patterns are compiled
		if (desktopPattern==null && StringUtils.isNotEmpty(desktopRegex))
			desktopPattern = Pattern.compile(desktopRegex, Pattern.CASE_INSENSITIVE);
		if (serverPattern==null && StringUtils.isNotEmpty(serverRegex))
			serverPattern = Pattern.compile(serverRegex, Pattern.CASE_INSENSITIVE);
		
		// check if we have a classifier already set that match the computer timestamp
		Date computerTimestamp = computer.getTimestamp();
		ComputerUsageClassifier classification = computer.getUsageClassifier(CLASSIFIER_NAME);
		if (classification!=null && classification.getWhenComputed().equals(computerTimestamp))
			return false;
		
		// check the name according to the regular expressions set
		String name = computer.getName();
		ComputerUsageType usageType = ComputerUsageType.Unknown;
		if (StringUtils.isNotEmpty(name)) {
			if (desktopPattern!=null && desktopPattern.matcher(name).matches())
				usageType = ComputerUsageType.Desktop;
			if (serverPattern!=null && serverPattern.matcher(name).matches())
				usageType = ComputerUsageType.Server;
		}
		
		computer.putUsageClassifier(new ComputerUsageClassifier(CLASSIFIER_NAME, usageType, computerTimestamp));
		return true;
	}

}
