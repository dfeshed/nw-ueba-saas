package fortscale.ml.model.selector;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IContextSelector {
	List<String> getContexts(Date startTime, Date endTime, Map<String, String> extraParams);
}
