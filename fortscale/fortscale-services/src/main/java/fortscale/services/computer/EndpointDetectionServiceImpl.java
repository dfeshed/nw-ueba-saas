package fortscale.services.computer;

import static org.python.google.common.base.Preconditions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.domain.core.Computer;
import fortscale.services.computer.classifier.NameMatchingClassifier;
import fortscale.services.computer.classifier.OperatingSystemClassifier;

@Component
public class EndpointDetectionServiceImpl implements EndpointDetectionService {

	@Autowired
	private OperatingSystemClassifier operatingSystemClassifier;
	
	@Autowired
	private NameMatchingClassifier nameMatchingClassifier;
	
	/**
	 * Attempts to classify the given computer as end-point or server.
	 * Classification results will be updated in the computer instance 
	 */
	@Override
	public void classifyComputer(Computer computer) {
		checkNotNull(computer);
		
		// pass the computer instance to every classifier
		// and set the classification value result in the computer instance
		operatingSystemClassifier.classify(computer);
		nameMatchingClassifier.classify(computer);
	}
	
	@Override
	public MachineInfo getMachineInfo(String hostname) {
		// lookup the machine info from the repository
		if (true) {
			// return the cached value for the machine if it was set
		}
		
		// if machine was not found or classifiers not set
		// calculate classifiers
		
		// update the machine info in the repository 
		
		
		return null;
	}
}