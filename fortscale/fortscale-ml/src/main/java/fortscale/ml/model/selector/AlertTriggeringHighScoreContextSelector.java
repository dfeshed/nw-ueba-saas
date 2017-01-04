package fortscale.ml.model.selector;

import fortscale.domain.core.Alert;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.dto.DateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Configurable(preConstruction = true)
public abstract class AlertTriggeringHighScoreContextSelector implements IContextSelector {
	@Autowired
	protected AlertsRepository alertsRepository;

	@Override
	public Set<String> getHighScoreContexts(Date startTime, Date endTime) {
		Set<String> modelContexts = getContextsSet(startTime, endTime);
		Set<String> alertContexts = alertsRepository.getAlertsByTimeRange(new DateRange(startTime.getTime(), endTime.getTime()), null, true)
				.stream()
				.map(Alert::getEntityName)
				.distinct()
				.collect(Collectors.toSet());

		// model contexts will contain the intersection between contexts that are relevant to the model and those that have alert
		if (modelContexts != null && !modelContexts.isEmpty() ) {
			modelContexts.retainAll(alertContexts);
		}
		return modelContexts;
	}
}
