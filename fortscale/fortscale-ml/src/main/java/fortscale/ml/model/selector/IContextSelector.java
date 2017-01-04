package fortscale.ml.model.selector;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IContextSelector {
	default Set<String> getContextsSet(Date startTime, Date endTime)
	{
		return new HashSet<>(getContexts(startTime,endTime));
	}
	List<String> getContexts(Date startTime, Date endTime);
	Set<String> getHighScoreContexts(Date startTime, Date endTime);
}
