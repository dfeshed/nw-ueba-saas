package fortscale.ml.model.selector;

import java.util.Date;
import java.util.List;

public interface IContextSelector {
	List<String> getContexts(Date startTime, Date endTime);
	List<String> getHighScoreContexts(Date startTime, Date endTime);
}
