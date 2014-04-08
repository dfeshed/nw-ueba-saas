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
 * Classify computer according to operating system 
 */
@Component
public class OperatingSystemClassifier implements EndpointClassifier {

	public static final String CLASSIFIER_NAME = "OperatingSystemClassifier";
	
	@Value("${endpoint.desktop.os.regex}")
	private String desktopRegex;
	@Value("${endpoint.server.os.regex}")
	private String serverRegex;
	
	private Pattern desktopPattern;
	private Pattern serverPattern;
	
	
	@Override
	public boolean canClassify(Computer computer) {
		return computer!=null && StringUtils.isNotEmpty(computer.getOperatingSystem());
	}

	@Override
	public boolean classify(Computer computer) {
		if (!canClassify(computer))
			return false;
		
		// ensure regex patterns are set
		ensurePatterns();
		
		// check if the computer already has a classification that match the computer timestamp
		Date computerTimestamp = computer.getTimestamp();
		ComputerUsageClassifier classification = computer.getUsageClassifier(CLASSIFIER_NAME);
		if (classification!=null && classification.getWhenComputed().equals(computerTimestamp))
			return false;
		
		// classify according to operating system name
		String os = computer.getOperatingSystem().toUpperCase();
		ComputerUsageType usageType = ComputerUsageType.Unknown;
		if (serverPattern!=null && serverPattern.matcher(os).matches())
			usageType = ComputerUsageType.Server;
		if (desktopPattern!=null && desktopPattern.matcher(os).matches())
			usageType = ComputerUsageType.Desktop;
		
		// update classification is computer
		computer.putUsageClassifier(new ComputerUsageClassifier(CLASSIFIER_NAME, usageType, computer.getTimestamp()));
		return true;
	}

	private void ensurePatterns() {
		if (desktopPattern==null) {
			if (desktopRegex==null)
				desktopRegex = "WINDOWS 8.*|WINDOWS 7.*|WINDOWS VISTA.*|MAC OS X";
			desktopPattern = Pattern.compile(desktopRegex, Pattern.CASE_INSENSITIVE);
		}
		
		if (serverPattern==null) {
			if (serverRegex==null)
				serverRegex = ".*Server.*";
			serverPattern = Pattern.compile(serverRegex, Pattern.CASE_INSENSITIVE);
		}
	}
	
}
