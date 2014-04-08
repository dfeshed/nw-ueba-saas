package fortscale.services.computer;

import fortscale.domain.core.Computer;

/**
 * Classifier for computer usages according to server or end-point usage
 */
public interface EndpointClassifier {

	/**
	 * Determines if the classifier can classify the computer 
	 */
	boolean canClassify(Computer computer);
	
	/**
	 * classify the given computer according to server or end-point usage
	 * @return a value indicating if a classification change was made
	 */
	boolean classify(Computer computer);
	
}
