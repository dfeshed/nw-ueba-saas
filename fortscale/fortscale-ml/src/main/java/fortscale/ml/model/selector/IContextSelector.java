package fortscale.ml.model.selector;

import java.util.Date;
import java.util.Set;

public interface IContextSelector {

	Set<String> getContexts(Date startTime, Date endTime);
	Set<String> getHighScoreContexts(Date startTime, Date endTime);

}
