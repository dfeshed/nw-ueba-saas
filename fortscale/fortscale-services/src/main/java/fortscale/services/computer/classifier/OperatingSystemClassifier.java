package fortscale.services.computer.classifier;

import org.apache.commons.lang.StringUtils;
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
	
	@Override
	public boolean canClassify(Computer computer) {
		return computer!=null && StringUtils.isNotEmpty(computer.getOperatingSystem());
	}

	@Override
	public void classify(Computer computer) {
		if (!canClassify(computer))
			return;
		
		// classify according to operating system name
		String os = computer.getOperatingSystem().toUpperCase();
		ComputerUsageType usageType = ComputerUsageType.Unknown;
		if (os.contains("SERVER"))
			usageType = ComputerUsageType.Server;
		if (os.contains("WINDOWS 8") || os.contains("WINDOWS 7") || os.contains("WINDOWS VISTA") || os.contains("MAC OS X"))
			usageType = ComputerUsageType.Desktop;
		
		// update classification is computer
		ComputerUsageClassifier classification = new ComputerUsageClassifier(CLASSIFIER_NAME, usageType);
		computer.putUsageClassifier(classification);
	}

}
