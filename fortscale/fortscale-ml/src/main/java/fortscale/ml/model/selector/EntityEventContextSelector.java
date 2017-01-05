package fortscale.ml.model.selector;

import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventDataReaderService;
import fortscale.ml.model.exceptions.InvalidEntityEventConfNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.Set;

@Configurable(preConstruction = true)
public class EntityEventContextSelector extends AlertTriggeringHighScoreContextSelector {
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventDataReaderService entityEventDataReaderService;

	private EntityEventConf entityEventConf;
	public EntityEventContextSelector(EntityEventContextSelectorConf config) {
		String entityEventConfName = config.getEntityEventConfName();
		entityEventConf = entityEventConfService.getEntityEventConf(entityEventConfName);
		validate(config);
	}
	private void validate(EntityEventContextSelectorConf config)
	{
		if(entityEventConf == null)
			throw new InvalidEntityEventConfNameException( config.getEntityEventConfName());
	}
	@Override
	public Set<String> getContexts(Date startTime, Date endTime) {
		return entityEventDataReaderService.findDistinctAcmContextsByTimeRange(
				entityEventConf, startTime, endTime);
	}
}
