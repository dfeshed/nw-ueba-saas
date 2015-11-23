package fortscale.ml.model.selector;

import org.joda.time.DateTime;
import java.util.List;

public interface ContextSelector {
	public List<String> getContexts(DateTime startTime, DateTime endTime);
}
