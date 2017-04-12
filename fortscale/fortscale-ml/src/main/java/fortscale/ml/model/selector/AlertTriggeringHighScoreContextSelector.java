package fortscale.ml.model.selector;

import com.google.common.collect.Sets;
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

}
