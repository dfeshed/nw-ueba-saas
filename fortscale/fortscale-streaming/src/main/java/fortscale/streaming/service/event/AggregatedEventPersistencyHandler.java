package fortscale.streaming.service.event;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AggregatedEventPersistencyHandler implements EventPersistencyHandler, InitializingBean {
	@Autowired
	private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;
	@Autowired
	private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;
	@Autowired
	private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;

	@Value("${streaming.event.field.type.aggr_event}")
	private String eventType;

	@Override
	public void afterPropertiesSet() throws Exception {
		eventPersistencyHandlerFactory.register(eventType, this);
	}

	@Override
	public void saveEvent(JSONObject event) {
		AggrEvent aggregatedFeatureEvent = aggrFeatureEventBuilderService.buildEvent(event);
		aggregatedFeatureEventsMongoStore.storeEvent(aggregatedFeatureEvent);
	}
}
