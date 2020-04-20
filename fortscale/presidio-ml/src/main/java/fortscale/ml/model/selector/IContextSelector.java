package fortscale.ml.model.selector;

import fortscale.utils.time.TimeRange;

import java.util.Set;

public interface IContextSelector {
	/**
	 * @param timeRange the selector will look for distinct context IDs in this time range
	 * @return a set of distinct context IDs
	 */
	Set<String> getContexts(TimeRange timeRange);
}
