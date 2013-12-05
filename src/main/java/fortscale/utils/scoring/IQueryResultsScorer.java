package fortscale.utils.scoring;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IQueryResultsScorer {
	public IEBSResult runEBSOnQueryResults(List<Map<String, Object>> resultsMap, Map<String, String> rowFieldRegexFilter, Set<String> timeFieldName, Set<String> fieldNamesFilter);
}
