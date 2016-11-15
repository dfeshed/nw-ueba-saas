package fortscale.ml.model.retriever;

import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventDataCachedReaderService;
import fortscale.entity.event.JokerEntityEventData;
import fortscale.entity.event.JokerFunction;
import fortscale.ml.model.retriever.metrics.EntityEventValueRetrieverMetrics;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.stream.Stream;

@Configurable(preConstruction = true)
public class EntityEventValueRetriever extends AbstractEntityEventValueRetriever {
	private EntityEventDataCachedReaderService entityEventDataCachedReaderService;
	private String entityEventConfName;
	private EntityEventConf entityEventConf;
	private JokerFunction jokerFunction;
	private EntityEventValueRetrieverMetrics metrics;

	public EntityEventValueRetriever(
			EntityEventValueRetrieverConf config,
			EntityEventDataCachedReaderService entityEventDataCachedReaderService) {
		super(config, false);
		this.entityEventDataCachedReaderService = entityEventDataCachedReaderService;
	}

	protected Stream<JokerEntityEventData> readJokerEntityEventData(EntityEventConf entityEventConf, String contextId, Date startTime, Date endTime) {
		return entityEventDataCachedReaderService.findEntityEventsJokerDataByContextIdAndTimeRange(
				entityEventConf,
				contextId,
				getStartTime(endTime),
				endTime
		).stream();
	}
}