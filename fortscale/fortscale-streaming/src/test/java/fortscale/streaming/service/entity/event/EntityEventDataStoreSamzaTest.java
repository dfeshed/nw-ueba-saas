package fortscale.streaming.service.entity.event;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.entity.event.EntityEventData;
import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.utils.time.TimestampUtils;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/entity-event-data-store-samza-test-context.xml"})
public class EntityEventDataStoreSamzaTest {
	private static final String STORE_NAME_PROPERTY = "fortscale.entity.events.store.name";

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDbUtilService mongoDbUtilService;

	private EntityEventDataStoreSamza createStore() {
		ExtendedSamzaTaskContext context = Mockito.mock(ExtendedSamzaTaskContext.class);
		Config configMock = Mockito.mock(Config.class);
		KeyValueStore<String, EntityEventData> storeMock = Mockito.mock(KeyValueStore.class);

		String storeName = "storeName";
		Mockito.when(context.getConfig()).thenReturn(configMock);
		Mockito.when(configMock.get(STORE_NAME_PROPERTY)).thenReturn(storeName);
		Mockito.when(context.getStore(storeName)).thenReturn(storeMock);

		return new EntityEventDataStoreSamza(context);
	}

	@Test
	public void shouldGetEventsAccordingToModificationTime() throws InterruptedException {
		// create an EntityEventDataStoreSamza with mocked mongodb and leveldb
		ExtendedSamzaTaskContext context = Mockito.mock(ExtendedSamzaTaskContext.class);
		Config configMock = Mockito.mock(Config.class);
		KeyValueStore<String, EntityEventData> storeMock = Mockito.mock(KeyValueStore.class);
		String storeName = "storeName";
		Mockito.when(context.getConfig()).thenReturn(configMock);
		Mockito.when(configMock.get(STORE_NAME_PROPERTY)).thenReturn(storeName);
		Mockito.when(context.getStore(storeName)).thenReturn(storeMock);
		EntityEventDataStoreSamza store = new EntityEventDataStoreSamza(context);

		// store one event in mongodb and leveldb
		EntityEventData event = new EntityEventData();
		Mockito.when(storeMock.get(store.getEntityEventDataKey(event))).thenReturn(event);
		Mockito.when(mongoDbUtilService.collectionExists(Mockito.anyString())).thenReturn(true);
		List<EntityEventData> eventsStoredInMongo = new ArrayList<>();
		final long timeBeforeModification = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		eventsStoredInMongo.add(event);
		Mockito.when(mongoTemplate.find(Mockito.any(Query.class), Mockito.eq(EntityEventData.class), Mockito.anyString())).thenReturn(eventsStoredInMongo);

		// the event should be retrieved
		List<EntityEventData> entityEventDatas = store.getEntityEventDataWithModifiedAtEpochtimeLte("normalized_username_hourly", timeBeforeModification);
		Assert.assertEquals(1, entityEventDatas.size());

		// modify the event and store it - so leveldb will have the true state (while mongodb will have a wrong modification time)
		EntityEventData eventAfterModification = new EntityEventData() {
			@Override
			public long getModifiedAtEpochtime() {
				return timeBeforeModification + 100;
			}
		};
		Mockito.when(storeMock.get(store.getEntityEventDataKey(event))).thenReturn(eventAfterModification);

		// now the event shouldn't be retrieved
		entityEventDatas = store.getEntityEventDataWithModifiedAtEpochtimeLte("normalized_username_hourly", timeBeforeModification);
		Assert.assertEquals(0, entityEventDatas.size());
	}
}
