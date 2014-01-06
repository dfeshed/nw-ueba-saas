package fortscale.collection;

import java.util.List;

public interface ItemsProcessor {

	/**
	 * Process input item and output the resulting item. If the item should be discarded
	 * from computation return null. 
	 * @param item the input item to process
	 * @return the resulting item or null to skip
	 */
	Object process(Object item);
	
}
