package fortscale.ml.model.selector;

import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventDataReaderService;
import fortscale.ml.model.InvalidEntityEventConfNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class EntityEventContextSelector implements IContextSelector {
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventDataReaderService entityEventDataReaderService;

	private EntityEventConf entityEventConf;

	public EntityEventContextSelector(EntityEventContextSelectorConf config) {
		validate(config);
		String entityEventConfName = config.getEntityEventConfName();
		entityEventConf = entityEventConfService.getEntityEventConf(entityEventConfName);
	}
	private void validate(EntityEventContextSelectorConf config)
	{
		String entityEventConfName = config.getEntityEventConfName();
		entityEventConf = entityEventConfService.getEntityEventConf(entityEventConfName);
		if(entityEventConf == null)
			throw new InvalidEntityEventConfNameException(entityEventConfName);
	}
	@Override
	public List<String> getContexts(Date startTime, Date endTime) {
		return entityEventDataReaderService.findDistinctContextsByTimeRange(
				entityEventConf, startTime, endTime);
	}
}
