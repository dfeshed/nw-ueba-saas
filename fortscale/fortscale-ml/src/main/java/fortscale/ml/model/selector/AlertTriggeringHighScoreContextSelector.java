package fortscale.ml.model.selector;

import fortscale.domain.core.Alert;
import fortscale.domain.core.dao.AlertsRepositoryCustom;
import fortscale.domain.dto.DateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Configurable(preConstruction = true)
public abstract class AlertTriggeringHighScoreContextSelector implements IContextSelector {
	@Autowired
	private AlertsRepositoryCustom alertsRepository;

	@Override
	public List<String> getHighScoreContexts(Date startTime, Date endTime) {
		return alertsRepository.getAlertsByTimeRange(new DateRange(startTime.getTime(), endTime.getTime()), null, true)
				.stream()
				.map(Alert::getEntityName)
				.collect(Collectors.toList());
	}
}
