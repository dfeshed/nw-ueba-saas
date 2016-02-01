package fortscale.ml.model.selector;

import java.util.Date;
import java.util.List;

public interface IContextSelector {
	public List<String> getContexts(Date startTime, Date endTime);
}
