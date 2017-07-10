package fortscale.ml.model.selector;

import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventDataReaderService;
import fortscale.ml.model.exceptions.InvalidEntityEventConfNameException;
import fortscale.utils.time.TimeRange;

import java.util.Date;
import java.util.Set;

public class EntityEventContextSelector implements IContextSelector {
	private EntityEventConf entityEventConf;
	private EntityEventDataReaderService entityEventDataReaderService;

	public EntityEventContextSelector(
			EntityEventContextSelectorConf conf,
			EntityEventConfService entityEventConfService,
			EntityEventDataReaderService entityEventDataReaderService) {

		this.entityEventConf = entityEventConfService.getEntityEventConf(conf.getEntityEventConfName());
		this.entityEventDataReaderService = entityEventDataReaderService;
		validate(conf);
	}

	@Override
	public Set<String> getContexts(TimeRange timeRange) {
		return entityEventDataReaderService.findDistinctAcmContextsByTimeRange(
				entityEventConf,
				Date.from(timeRange.getStart()),
				Date.from(timeRange.getEnd()));
	}

	private void validate(EntityEventContextSelectorConf conf) {
		if (entityEventConf == null) {
			throw new InvalidEntityEventConfNameException(conf.getEntityEventConfName());
		}
	}
}
