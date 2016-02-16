package fortscale.ml.model.selector;

import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventDataReaderService;
import fortscale.ml.model.Exceptions.InvalidEntityEventConfNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class EntityEventContextSelector implements IContextSelector {
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private EntityEventDataReaderService entityEventDataReaderService;

	private EntityEventConf entityEventConf;
	private String entityEventConfName;
	public EntityEventContextSelector(EntityEventContextSelectorConf config) {
		entityEventConfName = config.getEntityEventConfName();
		entityEventConf = entityEventConfService.getEntityEventConf(entityEventConfName);
		validate();
	}
	private void validate()
	{
		if(entityEventConf == null)
			throw new InvalidEntityEventConfNameException(entityEventConfName);
	}
	@Override
	public List<String> getContexts(Date startTime, Date endTime) {
		return entityEventDataReaderService.findDistinctContextsByTimeRange(
				entityEventConf, startTime, endTime);
	}
}
