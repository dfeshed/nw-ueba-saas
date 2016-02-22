package fortscale.services.computer;

import static org.python.google.common.base.Preconditions.*;;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.domain.core.Computer;
import fortscale.services.computer.classifier.LoginClassifier;
import fortscale.services.computer.classifier.NameMatchingClassifier;
import fortscale.services.computer.classifier.OperatingSystemClassifier;

@Component
public class EndpointDetectionServiceImpl implements EndpointDetectionService {

	@Autowired
	private OperatingSystemClassifier operatingSystemClassifier;
	
	@Autowired
	private NameMatchingClassifier nameMatchingClassifier;
	
	@Autowired
	private LoginClassifier loginClassifier;
	
	/**
	 * Attempts to classify the given computer as end-point or server.
	 * Classification results will be updated in the computer instance 
	 */
	@Override
	public boolean classifyComputer(Computer computer) {
		checkNotNull(computer);
		
		// pass the computer instance to every classifier
		// and set the classification value result in the computer instance
		boolean changeMade = false;
		changeMade |= operatingSystemClassifier.classify(computer);
		changeMade |= nameMatchingClassifier.classify(computer);
		changeMade |= loginClassifier.classify(computer);
		
		return changeMade;
	}
	
	public boolean classifyNewComputer(Computer computer) {
		checkNotNull(computer);
		
		// pass the computer instance to every classifier
		// and set the classification value result in the computer instance
		boolean changeMade = false;
		changeMade |= operatingSystemClassifier.classify(computer);
		changeMade |= nameMatchingClassifier.classify(computer);
		
		return changeMade;
	}
}