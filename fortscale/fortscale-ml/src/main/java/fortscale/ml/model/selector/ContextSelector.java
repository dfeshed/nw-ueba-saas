package fortscale.ml.model.selector;

import java.util.Date;
import java.util.List;

public interface ContextSelector {
	public List<String> getContexts(Date startTime, Date endTime);
}
